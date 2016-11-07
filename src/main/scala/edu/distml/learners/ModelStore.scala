package edu.distml.learners

import edu.distml.messages.{MLModel, MLModelUpdate}

/**
  * Author : Raajay Viswanathan
  * e-mail : raajay.v@gmail.com
  * Date : 9/28/16
  */
abstract class ModelStore {
  def getModel: MLModel

  def updateModel(model: MLModelUpdate): Unit

  def stopUpdating: Boolean

  var version: Long
}
