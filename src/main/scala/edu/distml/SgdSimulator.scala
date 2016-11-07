package edu.distml

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.{Config, ConfigFactory}
import edu.distml.entities.{ModelServer, Worker}
import edu.distml.messages.Start

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Random

/**
  * Author : Raajay Viswanathan
  * e-mail : raajay.v@gmail.com
  * Date   : 8/3/16
  */
object SgdSimulator extends App {
  val simulator = new SgdSimulator
  simulator.run(ConfigFactory.load())
}


class SgdSimulator {

  def run(config: Config): Unit = {
    val system = ActorSystem("DistributedMLLearner", config)
    val rng = new Random(config.getInt("experiment.exp-seed"))

    val dispatcher = MailboxType.withName(config.getString("distml.network-queuing-type")) match {
      case MailboxType.FirstInFirstOut => "server.fifo-dispatcher"
      case MailboxType.SmallestIterationFirst => "server.smallest-iteration-first-dispatcher"
      case _ =>
        throw new IllegalArgumentException("ERROR! Unexpected value in configuration in environment variable DISTML_QUEUING_TYPE")
    }


    val ps = system.actorOf(Props[ModelServer].withDispatcher(dispatcher), "parameter-server")
    val workers = (0 until config.getInt("distml.num-workers")).map(i => {
      system.actorOf(Props(new Worker(i, rng.nextInt())).withDispatcher("worker.fifo-dispatcher"), "worker-" + i)
    }).toArray
    ps ! Start(workers) // send a Start message to ps, with list of all workers
    Await.result(system.whenTerminated, Duration.create(config.getInt("sgd.time-limit"), MINUTES))
  }

}
