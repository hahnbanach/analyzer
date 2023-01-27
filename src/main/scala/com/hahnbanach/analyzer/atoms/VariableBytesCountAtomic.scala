package com.hahnbanach.analyzer.atoms

import com.hahnbanach.analyzer.entities.{AnalyzersData, Result}

/**
  * Created by angelo on 05/05/21.
  */

/** calculate the size of a variable
  *
  * variableSize(variable_name)
  *
  * @param arguments: <variable>
  */
class VariableBytesCountAtomic(val arguments: List[String], restrictedArgs: Map[String, String]) extends AbstractAtomic {
  val varName: String = arguments.headOption match {
    case Some(t) => t
    case _ => throw ExceptionAtomic("variableBytesCount: must have a variable name")
  }

  override def toString: String = "variableBytesCount"
  val isEvaluateNormalized: Boolean = true

  /** Calculate the size in bytes of the content of a variable <varname>
    *
    * @param query the user query
    * @param data the dictionary of variables
    * @return Result score will be N.0 with the size of variable in byte, 0.0 if the variable doesn't exists
    */
  def evaluate(query: String, data: AnalyzersData = AnalyzersData()): Result = {
    val score = data.stateData.variables.get(varName) match {
      case Some(value) => value.getBytes.length.toDouble
      case _ => 0.0d
    }
    Result(score = score)
  }
}
