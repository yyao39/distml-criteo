package edu.distml

import java.io.File

import breeze.linalg.{DenseMatrix, DenseVector, csvread}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.util.hashing.MurmurHash3

/**
  * Author : Raajay Viswanathan
  * e-mail : raajay.v@gmail.com
  * Date : 10/1/16
  */
object DataReader {

  def readMLData(filePrefix: String): (DenseVector[Double], ListBuffer[mutable.HashMap[Int, Int]]) = {
    /**
      * criteo modification
      */
    val init = System.currentTimeMillis()
    println("start read data into memory")

    val content_features = Source.fromFile(filePrefix + ".features").getLines.map(_.split(","))
    val header = content_features.next()
    val content_map = content_features.map(header.zip(_).toMap)
    //val iter_label = Source.fromFile(filePrefix + ".labels").getLines
    val labels = new DenseVector[Double](
      Source.fromFile(filePrefix + ".labels").getLines.toArray.map(_.stripLineEnd.toDouble).map(x => (x+1)/2)
    )
    lazy val features = new ListBuffer[mutable.HashMap[Int, Int]]
    val D : Int = 16777216

    while (content_map.hasNext) {

    // 先读500行
    //for(i <- 0 until 500) {
      val x = get_x(content_map.next(), D)
      features += x
    }

    /*
    // original code starts here
    val features = csvread(new File(filePrefix + ".features"), ',')
    val labels = new DenseVector[Int](
      Source.fromFile(filePrefix + ".labels").getLines.toArray.map(_.stripLineEnd.toInt)
    )
    */
    print("finish reading at \t")
    println(System.currentTimeMillis() - init)

    (labels, features)
  }

  def get_x(row: Map[String, String], D: Int): mutable.HashMap[Int, Int] = {
    var fullind = ListBuffer[Int]()
    for ((k, v) <- row) fullind += MurmurHash3.stringHash(k + "=" + v) % D
    val x = new mutable.HashMap[Int, Int]

    for (index <- fullind) {
      if (x.contains(index)) x(index) += 1
      else x.+=((index, 1))
    }

    x
  }

}
