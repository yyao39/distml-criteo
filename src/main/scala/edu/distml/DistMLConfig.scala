package edu.distml

import com.typesafe.config.{Config, ConfigFactory}

/**
  * Author : Raajay Viswanathan
  * e-mail : raajay.v@gmail.com
  * Date   : 8/9/16
  */
/*
object DistMLConfig {
  private val config = ConfigFactory.load()
  lazy val message = "Configuration"

  def getConfig : Config  = {
    config
  }

  // parameter server configuration
  object PSConfig {
    private val psConfig = config.getConfig("server")
    lazy val messageProcessInterval = psConfig.getInt("message-process-interval")

    object PDConfig {
      private val pdConfig = psConfig.getConfig("priority-dispatcher")
      lazy val mbtype = pdConfig.getString("mailbox-type")
    }

  }

  // Stochastic Gradient Descent Configuration
  object SGDConfig {
    private val sgdConfig = config.getConfig("sgd")
    lazy val miniBatchSize = sgdConfig.getInt("mini-batch-size")
    lazy val maxIterations = sgdConfig.getInt("max-iterations")
    lazy val learningRate = sgdConfig.getDouble("learning-rate")
    lazy val numWorkers = sgdConfig.getInt("num-workers")
  }

  object DatasetConfig {
    private val datasetConfig = config.getConfig("data")
    lazy val datasetBaseFolder = datasetConfig.getString("base-folder")
    lazy val datasetName = datasetConfig.getString("dataset")
    lazy val numFeatures = datasetConfig.getInt("num-features")
  }

  object ExperimentConfig {
    private val experimentConfig = config.getConfig("experiment")
    lazy val experimentSeed = experimentConfig.getInt("exp-seed")
  }

}
*/
