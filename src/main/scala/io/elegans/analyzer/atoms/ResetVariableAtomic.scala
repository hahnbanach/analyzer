package io.elegans.analyzer.atoms

import io.elegans.analyzer.entities.{AnalyzersDataInternal, Result}

/**
 * Created by angelo on 26/06/17.
 */

/** set and export variables
 *
 * @param arguments creation arguments
 */
class ResetVariableAtomic(val arguments: List[String], restrictedArgs: Map[String, String]) extends AbstractAtomic {
  val name: String = arguments.headOption match {
    case Some(t) => t
    case _ => throw ExceptionAtomic("ResetVariableAtomic: must have variable name argument")
  }

  val value: Option[String] = arguments.lift(1)

  override def toString: String = "setVariable"
  val isEvaluateNormalized: Boolean = true

  /** Reset and export a variable
   *
   * @param query the user query
   * @param data the dictionary of variables
   * @return Result with 1.0 if the variable exists score = 0.0 otherwise
   */
  def evaluate(query: String, data: AnalyzersDataInternal = AnalyzersDataInternal()): Result = {
    val newMap = value match {
      case Some(t) => data.stateData.variables + (name -> t)
      case _ => data.stateData.variables - name
    }
    Result(score = 1.0,
      data = data.copy(
        stateData = data.stateData.copy(
          variables = newMap
        )
      )
    )
  }
}
