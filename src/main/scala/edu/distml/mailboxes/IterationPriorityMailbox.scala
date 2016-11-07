package edu.distml.mailboxes

import akka.actor.ActorSystem.Settings
import akka.dispatch.{PriorityGenerator, UnboundedPriorityMailbox}
import com.typesafe.config.Config
import edu.distml.messages._

/**
  * Author : Raajay Viswanathan
  * e-mail : raajay.v@gmail.com
  * Date   : 8/5/16
  */
class IterationPriorityMailbox(settings: Settings, config: Config)
  extends UnboundedPriorityMailbox(
    PriorityGenerator {
      case msg: WorkerUpdate => {
        msg.iteration.toInt // TODO take mod over number of workers
      }

      case _ => 0
    }
  )

