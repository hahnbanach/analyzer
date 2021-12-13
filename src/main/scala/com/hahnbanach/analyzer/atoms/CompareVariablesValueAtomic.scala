package com.hahnbanach.analyzer.atoms

import cats.implicits._
import com.hahnbanach.analyzer.entities.{AnalyzersData, Result}

/**
  * Created by angelo on 10/02/20.
  */

/** test if a variable exists on dictionary of variables and match the value, eg:
  *
  * checkVariableValue(variable1_name, variable2_name)
  *
  * @param arguments: <variable1>, <variable2>
  */
class CompareVariablesValueAtomic(val arguments: List[String], restrictedArgs: Map[String, String]) extends AbstractAtomic {
  val varName1: String = arguments.headOption match {
    case Some(t) => t
    case _ => throw ExceptionAtomic("CompareVariablesValue: must have a first variable name")
  }
  val varName2: String = arguments.lift(1) match {
    case Some(t) => t
    case _ => throw ExceptionAtomic("CompareVariablesValue: must have a second variable name")
  }
  override def toString: String = "compareVariablesValue"
  val isEvaluateNormalized: Boolean = true

  /** Compare the values of two variables, return 1.0 only if the value of both
    *   is the same, return false in all the other cases
    *
    * @param query the user query
    * @param data the dictionary of variables
    * @return Result with 1.0 if the variables both exists and the content is the same, score = 0.0 otherwise
    */
  def evaluate(query: String, data: AnalyzersData = AnalyzersData()): Result = {
    val variables = data.stateData.variables
    (variables.get(varName1), variables.get(varName2)) match {
      case (Some(v1), Some(v2)) =>
        if(v1 === v2)
          Result(score = 1.0)
        else
          Result(score = 0.0)
      case _ =>
        Result(score = 0.0)
    }
  }
}