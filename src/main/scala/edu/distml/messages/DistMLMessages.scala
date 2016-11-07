package edu.distml.messages

import akka.actor.ActorRef
import breeze.linalg.{DenseVector, SparseVector}

/**
  * Author : Raajay Viswanathan
  * e-mail : raajay.v@gmail.com
  * Date   : 8/3/16
  */

sealed trait DistMLMessages

// Messages regarding setting up distributed computation
case class Start(workers: Array[ActorRef]) extends DistMLMessages

case class Terminate() extends DistMLMessages

case class Ping() extends DistMLMessages

case class PingAll(workers: Array[ActorRef]) extends DistMLMessages

// ML algorithm related messages
case class WorkerUpdate(iteration: Long, workerId: Int, update: MLModelUpdate) extends DistMLMessages

case class ModelCopy(iteration: Long, model: MLModel) extends DistMLMessages


sealed trait MLModel

case class SparseLinearModel(w: SparseVector[Double]) extends MLModel

case class DenseLinearModel(w: DenseVector[Double]) extends MLModel

sealed trait MLModelUpdate

case class GradientLinearModel(g: DenseVector[Double]) extends MLModelUpdate
