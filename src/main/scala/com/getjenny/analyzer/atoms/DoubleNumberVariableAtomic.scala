package com.getjenny.analyzer.atoms

import com.getjenny.analyzer.expressions.{AnalyzersDataInternal, Result}
import scala.util.control.NonFatal

/**
  * Created by angelo on 11/02/19.
  */

/** test if a variable exists on dictionary of variables
  *
  * @param arguments name of the variable to be checked
  */
class DoubleNumberVariableAtomic(val arguments: List[String], restrictedArgs: Map[String, String]) extends AbstractAtomic {
  val varName: String = arguments.headOption match {
    case Some(t) => t
    case _ => throw ExceptionAtomic("DoubleNumberVariableAtomic: must have at least " +
      "one argument (<varname>, [def. value])")
  }

  val defValue: Option[Double] = arguments.lift(1) match {
    case Some(t) => Some(t.toDouble)
    case _ => None
  }

  override def toString: String = "doubleNumberVariable"
  val isEvaluateNormalized: Boolean = false

  /** convert a double from a string representation of a variable
    *
    * @param query the user query
    * @param data the dictionary of variables
    * @return Result with 1.0 if the variable exists score = 0.0 otherwise
    */
  def evaluate(query: String, data: AnalyzersDataInternal = AnalyzersDataInternal()): Result = {
    val score = data.extractedVariables.get(varName) match {
      case Some(value) =>
        try {
          value.toDouble
        } catch {
          case e: NumberFormatException =>
            throw ExceptionAtomic("DoubleNumberVariableAtomic: numerical format exception " +
              "converting string to double", e)
          case NonFatal(e) =>
            throw ExceptionAtomic("DoubleNumberVariableAtomic: exception casting string to double: " + e.getMessage)
        }
      case _ =>
        defValue match {
          case Some(v) => v
          case _ => throw ExceptionAtomic("DoubleNumberVariableAtomic: requested variable not found and no " +
            "default value was provided")
        }
    }
    Result(score = score)
  }
}
