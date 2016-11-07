package edu.distml.learners

import breeze.linalg._
import breeze.numerics._
import com.typesafe.config.Config
import edu.distml.messages._

/**
  * Author : Raajay Viswanathan
  * e-mail : raajay.v@gmail.com
  * Date : 9/28/16
  */
class LinearModelStore(config: Config) extends ModelStore {

  var version: Long = 0
  var model = DenseVector.zeros[Double](config.getInt("data.num-features"))
  var g_history = DenseVector.ones[Double](config.getInt("data.num-features"))

  override def getModel: MLModel = {
    DenseLinearModel(model)
  }

  override def updateModel(update: MLModelUpdate): Unit = {
    update match {
      case GradientLinearModel(g) =>

        /**
          * Modified for criteo dataset
          */

        //print("weights updated: \t")
        //println(sum(g))
        model = g
      //model -= g :* config.getDouble("sgd.learning-rate") // Add the model
        version += 1

      case _ => throw new IllegalArgumentException("Update is not of the form of GradientLinearModel")
    }
  }

  override def stopUpdating: Boolean = {
    version > config.getLong("sgd.max-iterations")
  }

}
