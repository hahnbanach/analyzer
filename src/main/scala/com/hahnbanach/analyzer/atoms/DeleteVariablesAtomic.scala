package com.hahnbanach.analyzer.atoms

import cats.implicits._
import com.hahnbanach.analyzer.entities.{AnalyzersData, Result}

import scala.util.matching.Regex

/**
 * Created by angelo.leto@elegans.io on 25/11/20.
 */

/** iterates over a set of variables and store the value into another destination variable.
 * @param arguments: the list of arguments
 *  regex: a regular expression to filter the variables away from the variables ditionary
 * @param restrictedArgs: the list of variables not exposed to the analyzers interface
 */
class DeleteVariablesAtomic(val arguments: List[String], restrictedArgs: Map[String, String]) extends AbstractAtomic {
  override def toString: String = "DeleteVariablesAtomic"
  val isEvaluateNormalized: Boolean = false
  private val usage: String = "regex=<regular expression>"

  private val parameters: Map[String, String] =
    arguments.map(_.split("=", 2))
      .filter(_.length === 2).map {
      case Array(v1,v2) => (v1,v2)
      case _ => throw ExceptionAtomic(s"$toString: bad parameter: $arguments")
    }.toMap

  private val regex: Regex = parameters.get("regex") match {
    case Some(t) => t.r
    case _ => throw ExceptionAtomic(s"$toString: `regex` argument is mandatory ($usage)")
  }

  /** evaluate the atom and set the destination variable
   *
   * @param query the user query
   * @param data the dictionary of variables
   * @return Result with 1.0 if at least one variable was found and deleted, 0.0 otherwise
   */
  def evaluate(query: String, data: AnalyzersData = AnalyzersData()): Result = {
    val variablesCountBefore = data.stateData.variables.size
    val cleanedVariables: Map[String, String] = data.stateData.variables.view
      .filterKeys(key => regex.findFirstIn(key).isEmpty).toMap
    val variablesCountAfter = cleanedVariables.size
    val score = if(variablesCountBefore =!= variablesCountAfter) 1.0d else 0.0d
    Result(score = score,
      data = data.copy(
        stateData = data.stateData.copy(variables = cleanedVariables)
      )
    )
  }
}
