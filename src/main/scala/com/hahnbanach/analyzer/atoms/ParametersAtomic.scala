package com.hahnbanach.analyzer.atoms

import com.hahnbanach.analyzer.entities.{AnalyzersData, Result}

/** just export Strings for operator: wrap the string as atom argument
  *   in this way it can be used to pass a string to an operator see for
  *   example ScoreToVariable operator
  *
  * Created by Angelo Leto on 18/01/2023.
  */

class ParametersAtomic(val arguments: List[String], restrictedArgs: Map[String, String]) extends AbstractAtomic {
  override def toString: String = "parameters(\"" + arguments.mkString(",") + "\")"
  val isEvaluateNormalized: Boolean = false

  def evaluate(query: String, data: AnalyzersData = AnalyzersData()): Result = {
    Result(score = arguments.length.toDouble)
  }
}
