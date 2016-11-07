package edu.distml.entities

import java.io.{File, PrintWriter}
import java.nio.file.{Files, Paths}

import breeze.linalg.{DenseVector, csvread}
import breeze.plot.{Figure, HistogramBins, hist, plot}
import com.typesafe.config.Config

/**
  * Author : Raajay Viswanathan
  * e-mail : raajay.v@gmail.com
  * Date : 9/28/16
  */
class ServerStatistics(config: Config) {

  val outputdir = Paths.get(config.getString("data.base-folder"),
    config.getString("data.dataset"), "output")
  // make the directory if it is not found
  Files.createDirectories(outputdir)

  val seed = config.getInt("experiment.exp-seed")

  // read from a persisted file in the output directory
  val runId = {
    val idfile = outputdir.resolve("runid").toString
    // read from file
    var id = 0 // default
    try {
      id = scala.io.Source.fromFile(idfile).getLines().take(1).mkString.toInt
    } catch {
      case e: Exception =>
        println(e)
        println("RunId file not found")
    }
    // write to file
    if (id >= 0) {
      val fp = new PrintWriter(new File(idfile))
      fp.write("%d" format (id + 1))
      fp.close()
    }

    id // return value
  }

  class PerIterationStats(iteration: Long) {
    var delay: Long = 0
    var loss: Double = 0.0

    override def toString: String = "%d,%.4f" format(delay, loss)
  }


  val stats = scala.collection.mutable.LinkedHashMap[Long, PerIterationStats]()
  val queue_type = config.getString("distml.network-queuing-type")
  val statsFile = outputdir.resolve(runId + "-" + seed + "-" + queue_type + ".stats")
  val statsWriter = new PrintWriter(new File(statsFile.toString))


  private def init(iteration: Long): Unit = {
    if (!stats.contains(iteration)) {
      stats.put(iteration, new PerIterationStats(iteration))
    }
  }

  def addLoss(iteration: Long, loss: Double): Unit = {
    init(iteration)
    stats(iteration).loss = loss
  }

  def addDelay(iteration: Long, delay: Long): Unit = {
    init(iteration)
    stats(iteration).delay = delay
  }

  def shutdown(): Unit = {
    flush() // write out all the remaining data
    statsWriter.close()
    makecharts()
  }

  def flush(): Unit = {
    stats.values foreach (x => {
      statsWriter.write(x.toString + "\n")
    })
    statsWriter.flush()
    stats.clear() // clear the buffer
  }

  def makecharts(): Unit = {
    val data = csvread(new File(statsFile.toString))

    val fig = Figure()
    fig.visible = false

    val p1 = fig.subplot(0)
    p1 += plot(DenseVector.rangeD(0, data.rows.toDouble), data(::, 1) map (x => x * 100.0))
    p1.ylabel = "Loss in (%)"
    p1.setYAxisDecimalTickUnits()
    p1.xlabel = "Iteration count"

    val p2 = fig.subplot(2, 1, 1)
    // we use 2 time the number of workers. If the number of o
    val nbins = 2 * config.getInt("distml.num-workers")
    p2 += hist(data(::, 0), HistogramBins.fromNumber(nbins))
    p2.ylabel = "Count"
    p2.xlabel = "Delay"

    fig.refresh()
    fig.saveas(outputdir.resolve(runId + "-" + seed + "-" + queue_type + ".pdf").toString)

  }
}
