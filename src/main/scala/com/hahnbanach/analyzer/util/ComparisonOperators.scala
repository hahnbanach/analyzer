package com.hahnbanach.analyzer.util

/**
  * Created by Angelo on 05/07/2018.
  */

import cats.implicits._
import com.hahnbanach.analyzer.atoms.ExceptionAtomic

import scala.math.Numeric.Implicits._

object ComparisonOperators {
  def compare(x: Long, y: Long, operator: String): Boolean = operator match {
    case "LessOrEqual" => x.compare(y) <= 0
    case "Less" => x.compare(y) < 0
    case "Greater" => x.compare(y) > 0
    case "GreaterOrEqual" => x.compare(y) >= 0
    case "Equal" => x.compare(y) == 0
    case _ => throw ExceptionAtomic("Bad Operator: " +
      "the operator must be: Equal, LessOrEqual, Less, Greater or GreaterOrEqual")
  }

  def compare(x: Double, y: Double, operator: String): Boolean = operator match {
    case "LessOrEqual" => x.compare(y) <= 0
    case "Less" => x.compare(y) < 0
    case "Greater" => x.compare(y) > 0
    case "GreaterOrEqual" => x.compare(y) >= 0
    case "Equal" => x.compare(y) == 0
    case _ => throw ExceptionAtomic("Bad Operator: " +
      "the operator must be: Equal, LessOrEqual, Less, Greater or GreaterOrEqual")
  }

  def compare(x: Int, y: Int, operator: String): Boolean = operator match {
    case "LessOrEqual" => x.compare(y) <= 0
    case "Less" => x.compare(y) < 0
    case "Greater" => x.compare(y) > 0
    case "GreaterOrEqual" => x.compare(y) >= 0
    case "Equal" => x.compare(y) == 0
    case _ => throw ExceptionAtomic("Bad Operator: " +
      "the operator must be: Equal, LessOrEqual, Less, Greater or GreaterOrEqual")
  }
}
