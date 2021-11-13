package com.hahnbanach.analyzer.atoms

import com.hahnbanach.analyzer.entities.{AnalyzersDataInternal, Result}

/**
 * Created by angelo on 26/06/17.
 */

/** set and export variables
 *
 * @param arguments creation arguments
 */
class SetVariableAtomic(val arguments: List[String], restrictedArgs: Map[String, String]) extends AbstractAtomic {
  val name: String = arguments.headOption match {
    case Some(t) => t
    case _ => throw ExceptionAtomic("SetVariableAtomic: must have variable name argument")
  }

  val value: String = arguments.lift(1) match {
    case Some(t) => t
    case _ => throw ExceptionAtomic("SetVariableAtomic: must have variable value argument")
  }

  override def toString: String = "setVariable"
  val isEvaluateNormalized: Boolean = true

  /** Set and export a variable
   *
   * @param query the user query
   * @param data the dictionary of variables
   * @return Result with 1.0 if the variable exists score = 0.0 otherwise
   */
  def evaluate(query: String, data: AnalyzersDataInternal = AnalyzersDataInternal()): Result = {
    Result(score = 1.0,
      data = data.copy(
        stateData= data.stateData.copy(
          variables = data.stateData.variables.updated(name, value)
        )
      )
    )
  }
}
