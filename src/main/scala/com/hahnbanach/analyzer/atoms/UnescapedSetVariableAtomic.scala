package com.hahnbanach.analyzer.atoms

import com.hahnbanach.analyzer.entities.{AnalyzersData, Result}

/**
  * Created by angelo on 26/06/17.
  */

/** Set and export a variable removing a level o escape
  *
  * @param arguments creation arguments
  */
class UnescapedSetVariableAtomic(val arguments: List[String], restrictedArgs: Map[String, String]) extends AbstractAtomic {
  val name: String = arguments.headOption match {
    case Some(t) => t
    case _ => throw ExceptionAtomic("UnescapedSetVariable: must have variable name argument")
  }

  val value: String = arguments.lift(1) match {
    case Some(t) => t
    case _ => throw ExceptionAtomic("UnescapedSetVariable: must have variable value argument")
  }

  override def toString: String = "unescapedSetVariable"
  val isEvaluateNormalized: Boolean = true

  /** Set and export a variable removing a level o escape
    *
    * @param query the user query
    * @param data the dictionary of variables
    * @return Result with 1.0 if the variable exists score = 0.0 otherwise
    */
  def evaluate(query: String, data: AnalyzersData = AnalyzersData()): Result = {
    val unescapedVal = value.replace("\\", "")
    Result(score = 1.0,
      data = data.copy(
        stateData = data.stateData.copy(
          variables = data.stateData.variables.updated(name, unescapedVal)
        )
      )
    )
  }
}
