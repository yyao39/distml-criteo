package edu.distml

/**
  * Author : Raajay Viswanathan
  * e-mail : raajay.v@gmail.com 
  * Date : 10/12/16
  */
object MailboxType extends Enumeration {
  type MailboxType = Value
  val FirstInFirstOut = Value("FirstInFirstOut")
  val SmallestIterationFirst = Value("SmallestIterationFirst")
}

object Dataset extends Enumeration {
  type Dataset = Value
  val Toy, Toy2, Skin = Value
}

object DelayDistribution extends Enumeration {
  type DelayDistribution = Value
  val Uniform = Value("Uniform")
  val Exponential = Value("Exponential")
  val Pareto = Value("Pareto")
}
