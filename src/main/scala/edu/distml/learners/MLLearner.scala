package edu.distml.learners

import breeze.linalg.{DenseMatrix, DenseVector}
import edu.distml.messages.{MLModel, MLModelUpdate}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Author : Raajay Viswanathan
  * e-mail : raajay.v@gmail.com
  * Date   : 9/28/16
  */
abstract class MLLearner {

  //var labels: DenseVector[Double] =

  // criteo modification
  //var features : ListBuffer[mutable.HashMap[Int, Int]]

  // origin code starts here
  //var features: DenseMatrix[Double]

  //def getLoss(model: MLModel): Double

  //def getPredictionError(model: MLModel): Double

  //def getRegularizedLoss(model: MLModel): Double

  def getModelUpdate(model: MLModel, labels: DenseVector[Double], features : ListBuffer[mutable.HashMap[Int, Int]]): MLModelUpdate
}
