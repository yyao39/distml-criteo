package edu.distml.entities

import java.nio.file.Paths

import akka.actor.Actor
import akka.event.Logging
import edu.distml.learners.LearnerUtils
import edu.distml.messages.{ModelCopy, Ping, WorkerUpdate}
import edu.distml.{DataReader, DelayDistribution}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random

/**
  * Author : Raajay Viswanathan
  * e-mail : raajay.v@gmail.com
  * Date   : 8/3/16
  */
class Worker(id: Int, seed: Int) extends Actor {

  lazy val log = Logging(context.system, this)
  val rng = new Random(seed)
  val config = context.system.settings.config
  lazy val learner = LearnerUtils.getLearnerInstance(config)
  val input = Paths.get(config.getString("data.base-folder"), config.getString("data.dataset"), "sample" + id)
  val (labels, features) = DataReader.readMLData(input.toString)
  //learner.labels = x
  //learner.features = y


  def delay = {
    DelayDistribution.withName(config.getString("worker.delay.type")) match {
      case DelayDistribution.Uniform =>
        val high = config.getDouble("worker.delay.uniform.high")
        val low = config.getDouble("worker.delay.uniform.low")
        low + (high - low) * rng.nextFloat()

      case DelayDistribution.Exponential =>
        // based on formulae from : http://jliszka.github.io/2013/08/19/climbing-the-probability-distribution-ladder.html
        val lambda = config.getDouble("worker.delay.exponential.lambda")
        math.log(rng.nextFloat()) * (-1.0 / lambda)

      case DelayDistribution.Pareto =>
        val xm = config.getDouble("worker.delay.pareto.xm")
        val alpha = config.getDouble("worker.delay.pareto.alpha")
        xm * math.pow(rng.nextFloat(), -1.0 / alpha)
    }
  }


  def receive = {

    case msg: ModelCopy =>
      log.debug("Received model copy from parameter server")
      // calculate the gradient update and send it
      val update = WorkerUpdate(msg.iteration, id, learner.getModelUpdate(msg.model, labels, features))
      val ps = sender()

      context.system.scheduler.scheduleOnce(delay seconds) {
        ps ! update
      }

    case msg: Ping =>
      log.info("Received Ping.")

    case _ => log.warning("Received unknown message")
  }

}
