package edu.distml.learners

import breeze.linalg._
import breeze.numerics._
import com.typesafe.config.Config
import edu.distml.messages._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Author : Raajay Viswanathan
  * e-mail : raajay.v@gmail.com
  * Date   : 9/28/16
  */
class LinearLearner(config: Config) extends MLLearner {
  val logbatch : Int = 500
  //val logbatch : Int = config.getInt("sgd.mini-batch-size")
  val D : Int = 16777216
  val lambda1 : Double = 0.0
  val lambda2 : Double = 0.0
  val alpha : Double = 0.05
  val adapt : Double = 1.0
  val fudge : Double = 0.5
  var batch: Int = 0

  // initialize our model
  // weights = [0.] * D
  val w = DenseVector.zeros[Double](D)

  // sum of historical gradients = [fudge] * D
  val g = DenseVector.ones[Double](D) :* fudge

  var loss: Double = 0.0
  /*
  val rng = scala.util.Random
  // create null labels and features
  var features: DenseMatrix[Double] = DenseMatrix.zeros[Double](0, 0)
  var labels: DenseVector[Int] = DenseVector.zeros[Int](0)

  // sigmoid(x) gives 1 / ( 1 + e^{-x} )
  def negativeLogLikelyhood(index: Int, ww: DenseVector[Double]): Double = {
    val x = features(index, ::).t
    val y = labels(index).toDouble
    val wtx: Double = x dot ww
    -1.0 * log(sigmoid(y * wtx))
  }

  def gradientNegativeLogLikelyhood(index: Int, ww: DenseVector[Double]): DenseVector[Double] = {
    val x = features(index, ::).t
    val y = labels(index).toFloat
    val wtx: Double = x dot ww
    val multiplier = y * (sigmoid(y * wtx) - 1)
    multiplier * x
  }

  val logisticRegularizedLossTrait = new DiffFunction[DenseVector[Double]] {

    def calculate(w: DenseVector[Double]) = {

      val C = 0.001
      val miniBatchSize = config.getInt("sgd.mini-batch-size")
      // mini-batch sample
      val indices = (0 until miniBatchSize).map(_ => rng.nextInt(features.rows))

      val oneOverN = 1.0 / miniBatchSize.toFloat
      // this is the negative log-likelihood function for the data
      val value = oneOverN * indices.map(i => negativeLogLikelyhood(i, w)).sum
      val gradient = oneOverN * indices.map(i => gradientNegativeLogLikelyhood(i, w)).reduce(_ + _)

      // we define the norm as (1/n) ||w|| _2 ^2
      val regLoss = (0 until w.length).map(i => pow(w(i), 2)).sum / w.length.toFloat
      val regGradient = (2.0 / w.length.toFloat) * w

      (value + C * regLoss, gradient + C * regGradient)
    }
  }

  val logisticLossTrait = new DiffFunction[DenseVector[Double]] {
    def calculate(w: DenseVector[Double]) = {
      val indices = 0 until features.rows
      val oneOverN = 1.0 / features.rows.toFloat
      // this is the negative log-likelihood function for the data
      val value = oneOverN * indices.map(i => negativeLogLikelyhood(i, w)).sum
      val gradient = oneOverN * indices.map(i => gradientNegativeLogLikelyhood(i, w)).reduce(_ + _)
      (value, gradient)
    }
  }

  override def getPredictionError(model: MLModel): Double = {
    model match {
      case DenseLinearModel(w) =>
        val predictions: DenseVector[Int] = (features * w).map(x => if (x > 0) 1 else -1)
        val num_wrong: Int = (0 until labels.length).map(i => if (predictions(i) == labels(i)) 0 else 1).sum
        1.0 * num_wrong / labels.length
      case _ => throw new IllegalArgumentException("Linear learner requires DenseLinearModel for update")
    }

  }

  override def getRegularizedLoss(model: MLModel): Double = {
    model match {
      case DenseLinearModel(w) =>
        logisticRegularizedLossTrait.valueAt(w)
      case _ => throw new IllegalArgumentException("Linear learner requires DenseLinearModel for update")
    }
  }

  override def getLoss(model: MLModel): Double = {
    model match {
      case DenseLinearModel(w) =>
        logisticLossTrait.valueAt(w)
      case _ => throw new IllegalArgumentException("Linear learner requires DenseLinearModel for update")
    }
  }
  */

  override def getModelUpdate(model: MLModel, labels : DenseVector[Double],
                              features : ListBuffer[mutable.HashMap[Int, Int]]): MLModelUpdate = {
    model match {
      case DenseLinearModel(w) => GradientLinearModel(SGD(w, labels, features))

      case _ => throw new IllegalArgumentException("Linear learner requires DenseLinearModel for update")
    }
  }

  def SGD (w : DenseVector[Double], labels : DenseVector[Double],
           features : ListBuffer[mutable.HashMap[Int, Int]]) : DenseVector[Double] = {
    assert(w.length == D)
    // main method in criteo-code-distml
    val init = System.currentTimeMillis()


    //var lossb: Double = 0.0
    var num_line: Int = 0

    // main training procedure
    lazy val ys: DenseVector[Double] = labels(batch * logbatch until (batch + 1) * logbatch)
    //ys : DenseVector[Double] = this.labels
    assert(ys.length == logbatch)
    lazy val ps = DenseVector.zeros[Double](logbatch)
    lazy val xs = features.slice(batch * logbatch, (batch + 1) * logbatch)

    for (num_line <- 0 until logbatch) {
      ps(num_line) = get_p(xs(num_line), w)
      //ps(num_line) = get_p(xs.remove(0),w)
      //println(num_line)
    }

    // for progress validation, useless for learning our model
    val lossx: Double = logloss(ps, ys)
    loss += lossx

    // step 3, update model with answer
    val new_w = update_w(w, xs, ps, ys)

    num_line = 0
    batch += 1

    //println(System.currentTimeMillis() - init)
    new_w
  }

  def logloss(ps : DenseVector[Double], ys : DenseVector[Double]) : Double = {
    assert(ps.length == ys.length)
    val ones = DenseVector.ones[Double](ps.size)
    ps.map(p => Math.max(Math.min(p, 1.0 - 10e-17), 10e-17))
    -sum((ys :* log(ps)) :+ ((ones :- ys) :* log(ones :- ps)))
  }

  def update_w(w : DenseVector[Double], xs : ListBuffer[mutable.HashMap[Int, Int]],
               ps : DenseVector[Double], ys : DenseVector[Double]) : DenseVector[Double] = {
    val delreg = 0
    val update = DenseVector.zeros[Double](w.length)
    for(j <- 0 until ys.length) {
      for ((i, xi) <- xs(j)) {
        //delreg = (lambda1 * ((-1.) if w(i) < 0. else 1.) +lambda2 * wi])
        val delta = (ps(j) - ys(j)) * xi + delreg
        if (adapt > 0) g(i) += delta * delta
        update(i) = delta * alpha / Math.pow(sqrt(g(i)), adapt)
      }
    }
    update
  }

  def get_p(x: mutable.HashMap[Int, Int], w: DenseVector[Double]): Double = {
    var wTx = 0.0
    for ((i, xi) <- x) wTx += w(i) * xi
    1 / (1 + Math.exp(-Math.max(Math.min(wTx, 50.0), -50.0)))
  }




























        /** criteo modify
          *

        val logbatch: Int = 500
        var num_line: Int = 0
        var batch: Int = 0
        // main training procedure
        //lazy val ys = DenseVector.zeros[Double](logbatch)
        lazy val ps = DenseVector.zeros[Double](logbatch)
        //var xs = new mutable.HashMap[Int, Int]()
        lazy val xs: ListBuffer[mutable.HashMap[Int, Int]] = this.features
        lazy val ys = this.labels
        val g = DenseVector.ones[Double](D) :* fudge


        while (num_line < logbatch) {

          ps(num_line) = get_p(xs.remove(0), w)

          /*
          val row = content_map.next()
          val x = get_x(row, D)
          xs += x
          ps(num_line) = get_p(x, w)


          if (iter_label.next().equals("1"))
            ys(num_line) = 1.0
          else
            ys(num_line) = 0.0
          */

          num_line += 1
        }
        batch += 1

        // for progress validation, useless for learning our model
        val lossx: Double = logloss(ps, ys)
        loss += lossx

        // step 3, update model with answer
        update_w(w, g, xs, ps, ys)

        // original code starts from here
        //GradientLinearModel(logisticRegularizedLossTrait.gradientAt(w)) // we need the negative gradient
        */



  /*
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

  def update_w(w : DenseVector[Double], g : DenseVector[Double],
               xs : ListBuffer[mutable.HashMap[Int, Int]], ps : DenseVector[Double],
               ys : DenseVector[Double]) : Unit = {
    val delreg = 0
    for(j <- 0 until ys.length) {
      for ((i, xi) <- xs(j)) {
        //delreg = (lambda1 * ((-1.) if w(i) < 0. else 1.) +lambda2 * wi])
        val delta = (ps(j) - ys(j)) * xi + delreg
        if (adapt > 0) g(i) += delta * delta
        w(i) -= delta * alpha / Math.pow(sqrt(g(i)), adapt)
      }
    }
  }
  */
}
