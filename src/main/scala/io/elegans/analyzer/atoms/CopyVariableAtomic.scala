package io.elegans.analyzer.atoms

import cats.implicits._
import io.elegans.analyzer.entities.{AnalyzersDataInternal, Result}

/**
 * Created by angelo.leto@elegans.io on 25/11/20.
 */

/** iterates over a set of variables and store the value into another destination variable.
 * @param arguments: the list of arguments
 *  sourceVarname: source variable name
 *  destVarname: destination variable name
 * @param restrictedArgs: the list of variables not exposed to the analyzers interface
 */
class CopyVariableAtomic(val arguments: List[String], restrictedArgs: Map[String, String]) extends AbstractAtomic {
  override def toString: String = "CopyVariableAtomicCopyVariableAtomic"
  val isEvaluateNormalized: Boolean = false
  private[this] val usage: String = "sourceVarname=<variableName>, destVarname=<variableName>"

  private[this] val parameters: Map[String, String] =
    arguments.map(_.split("=", 2))
      .filter(_.length === 2).map {
      case Array(v1,v2) => (v1,v2)
      case _ => throw ExceptionAtomic(s"$toString: bad parameter: $arguments")
    }.toMap

  private[this] val sourceVarname: String = parameters.get("sourceVarname") match {
    case Some(t) => t
    case _ => throw ExceptionAtomic(s"$toString: `sourceVarname` argument is mandatory ($usage)")
  }

  private[this] val destVarname: String = parameters.get("destVarname") match {
    case Some(t) => t
    case _ => throw ExceptionAtomic(s"$toString: `destVarname` argument is mandatory ($usage)")
  }

  /** evaluate the atom and set the destination variable
   *
   * @param query the user query
   * @param data the dictionary of variables
   * @return Result with 1.0 if the source variable was found, 0.0 otherwise
   */
  def evaluate(query: String, data: AnalyzersDataInternal = AnalyzersDataInternal()): Result = {

    val (score: Double, updatedVariables: Map[String, String]) =
      data.stateData.variables.get(sourceVarname) match {
        case Some(v) => (1.0d, data.stateData.variables + (destVarname -> v))
        case _ => (0.0d, data.stateData.variables)
      }

    Result(score = score,
      data = data.copy(
        stateData = data.stateData.copy(variables = updatedVariables)
      )
    )
  }
}
