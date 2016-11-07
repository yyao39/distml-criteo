package edu.distml.learners

import breeze.linalg._
import breeze.numerics._
import com.typesafe.config.Config
import edu.distml.messages.{DenseLinearModel, GradientLinearModel, MLModel, MLModelUpdate}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by yyao39 on 11/6/16.
  */
class LinearTester(config: Config) extends MLLearner {

  def getPredictionError(model: MLModel, labels : DenseVector[Double],
  features : ListBuffer[mutable.HashMap[Int, Int]]): Double = {
    model match {
      case DenseLinearModel(w) =>
        assert(labels.length == features.length)
        val predictions: DenseVector[Double] = DenseVector.zeros[Double](labels.length)

        for (num_line <- 0 until labels.length) predictions(num_line) = get_p(features(num_line), w)

        val loss: Double = logloss(predictions, labels)
        loss/500
      case _ => throw new IllegalArgumentException("Linear learner requires DenseLinearModel for update")
    }

  }

  override def getModelUpdate(model: MLModel, labels : DenseVector[Double],
                              features : ListBuffer[mutable.HashMap[Int, Int]]): MLModelUpdate = {
    GradientLinearModel(labels)
  }

  def get_p(x: mutable.HashMap[Int, Int], w: DenseVector[Double]): Double = {
    var wTx = 0.0
    for ((i, xi) <- x) wTx += w(i) * xi
    1 / (1 + Math.exp(-Math.max(Math.min(wTx, 50.0), -50.0)))
  }

  def logloss(ps : DenseVector[Double], ys : DenseVector[Double]) : Double = {
    assert(ps.length == ys.length)
    val ones = DenseVector.ones[Double](ps.size)
    ps.map(p => Math.max(Math.min(p, 1.0 - 10e-17), 10e-17))
    -sum((ys :* log(ps)) :+ ((ones :- ys) :* log(ones :- ps)))
  }

}
