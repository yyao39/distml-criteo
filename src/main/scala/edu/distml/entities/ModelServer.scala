package edu.distml.entities

import java.nio.file.Paths

import akka.actor.Actor
import akka.event.Logging
import edu.distml.DataReader
import edu.distml.learners.LearnerUtils
import edu.distml.messages._

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Author : Raajay Viswanathan
  * e-mail : raajay.v@gmail.com
  * Date   : 8/3/16
  */
class ModelServer extends Actor {

  val log = Logging(context.system, this)
  val config = context.system.settings.config
  val store = LearnerUtils.getModelStoreInstance(config)
  val tester = LearnerUtils.getTesterInstance(config)
  val input = Paths.get(config.getString("data.base-folder"), config.getString("data.dataset"), "test")
  val stats = new ServerStatistics(config)
  val (x, y) = DataReader.readMLData(input.toString)

  var hasTerminated = false
  // state of the parameter server
  val observedLoss = ListBuffer.empty[Double]

  // output handlers


  // TODO need to handle dropped messages. A worker should not go idle, because one
  // its messages was got dropped.

  def receive = {

    case msg: WorkerUpdate =>
      log.debug("Received gradient update from " + msg.workerId)
      if (hasTerminated) {
        log.debug("Ignoring update as parameter server has terminated.")
      } else {
        if (store.stopUpdating) {
          hasTerminated = true
          log.debug("Issuing termination instruction to self")
          self ! Terminate()
        } else {
          store.updateModel(msg.update)
          sender() ! ModelCopy(store.version, store.getModel)
          stats.addDelay(store.version, store.version - msg.iteration - 1)
          val testloss = tester.getPredictionError(store.getModel, x.slice(500,1000), y.slice(500,1000))
          stats.addLoss(store.version, testloss)

          log.info("Prediction error (%%) - Iteration - %d - %f".format(store.version, testloss))

          // XXX sleep for some time
          Thread.sleep(config.getInt("server.message-process-interval"))
        }
      }


    case msg: Start =>
      log.info("Received start notification. Firing up the model server.")
      // send a model copy to all the workers
      log.debug("Model: " + store.getModel)
      //log.info("Prediction error (%%) - %f".format(100 * tester.getPredictionError(store.getModel)))
      msg.workers.foreach(w => {
        w ! ModelCopy(store.version, store.getModel)
      })


    case msg: Terminate =>
      log.info("Received notification to terminate.")
      log.info("Shutting down the stats gatherer")
      stats.shutdown()
      log.info("Shutting down Actor System")
      // println(store.getModel)
      context.system.terminate()


    case msg: PingAll =>
      log.info("Received PingAll")
      /*
      context.system.scheduler.scheduleOnce(2000 milliseconds) {

        msg.workers.foreach(w => {
          w ! Ping()
        })
      }
      */


    case _ =>
      log.warning("Received unknown message at master")
  }
}
