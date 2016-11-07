package edu.distml.learners

import com.typesafe.config.Config

/**
  * Author : Raajay Viswanathan
  * e-mail : raajay.v@gmail.com
  * Date   : 9/28/16
  */
object LearnerUtils {

  def getLearnerInstance(config: Config): MLLearner = {
    // should read the config file and create an instance of the correct learner
    new LinearLearner(config)
  }

  def getTesterInstance(config: Config): LinearTester = {
    new LinearTester(config)
  }

  def getModelStoreInstance(config: Config): ModelStore = {
    new LinearModelStore(config)
  }
}
