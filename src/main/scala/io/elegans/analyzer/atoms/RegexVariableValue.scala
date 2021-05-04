package io.elegans.analyzer.atoms

import io.elegans.analyzer.entities.{AnalyzersDataInternal, Result}

import scala.util.matching.Regex

/**
  * Created by angelo on 10/02/20.
  */

/** test if a variable exists on dictionary of variables and match the regex, eg:
  *
  * regexVariableValue(variable_name, regex)
  *
  * @param arguments: <variable>, <regex>
  */
class RegexVariableValue(val arguments: List[String], restrictedArgs: Map[String, String]) extends AbstractAtomic {
  val varName: String = arguments.headOption match {
    case Some(t) => t
    case _ => throw ExceptionAtomic("RegexVariableValue: must have a variable name")
  }
  val regex: Regex = arguments.lift(1) match {
    case Some(t) => t.r
    case _ => throw ExceptionAtomic("RegexVariableValue: must have a value")
  }
  override def toString: String = "regexVariableValue"
  val isEvaluateNormalized: Boolean = true

  /** Check if a variable named <varname> exists on the data variables dictionary and match the <regex>
    *
    * @param query the user query
    * @param data the dictionary of variables
    * @return Result score will be N.0 with the count of matches, 0.0 otherwise
    */
  def evaluate(query: String, data: AnalyzersDataInternal = AnalyzersDataInternal()): Result = {
    val score = data.stateData.variables.get(varName) match {
      case Some(value) => regex.findAllIn(value).toList.size.toDouble
      case _ => 0.0d
    }
    Result(score = score)
  }
}