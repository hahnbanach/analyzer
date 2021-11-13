package com.hahnbanach.analyzer.atoms

import com.hahnbanach.analyzer.entities.{AnalyzersDataInternal, Result}

/**
  * Created by angelo on 05/05/21.
  */

/** calculate the size of a variable
  *
  * variableSize(variable_name)
  *
  * @param arguments: <variable>
  */
class VariableSizeAtomic(val arguments: List[String], restrictedArgs: Map[String, String]) extends AbstractAtomic {
  val varName: String = arguments.headOption match {
    case Some(t) => t
    case _ => throw ExceptionAtomic("RegexVariableValue: must have a variable name")
  }

  override def toString: String = "variableSize"
  val isEvaluateNormalized: Boolean = true

  /** Calculate the size of the content of a variable <varname>
    *
    * @param query the user query
    * @param data the dictionary of variables
    * @return Result score will be N.0 with the size of variable, 0.0 if the variable doesn't exists
    */
  def evaluate(query: String, data: AnalyzersDataInternal = AnalyzersDataInternal()): Result = {
    val score = data.stateData.variables.get(varName) match {
      case Some(value) => value.size.toDouble
      case _ => 0.0d
    }
    Result(score = score)
  }
}