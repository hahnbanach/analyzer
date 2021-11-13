package com.hahnbanach.analyzer.atoms

import com.hahnbanach.analyzer.entities.{AnalyzersDataInternal, Result}

import scala.util.control.NonFatal

/**
 * Created by angelo on 08/05/20.
 */

/** execute the modulus on a variable
 *
 * @param arguments name of the variable, modulus, default value
 */
class ModulusVariableAtomic(val arguments: List[String], restrictedArgs: Map[String, String]) extends AbstractAtomic {
  val varName: String = arguments.headOption match {
    case Some(t) => t
    case _ => throw ExceptionAtomic("ModulusVariable: first argument is mandatory " +
      "(<varname>, <modulus>, <def. value>)")
  }

  val modulus: Double = arguments.lift(1) match {
    case Some(t) => t.toDouble
    case _ => throw ExceptionAtomic("ModulusVariableAtomic: second argument is mandatory " +
      "(<varname>, <modulus>, <def. value>)")
  }

  val defValue: Double = arguments.lift(2) match {
    case Some(t) => t.toDouble
    case _ => throw ExceptionAtomic("ModulusVariable: third argument is mandatory " +
      "(<varname>, <modulus>, <def. value>)")
  }

  override def toString: String = "ModulusVariable"
  val isEvaluateNormalized: Boolean = false

  /** convert a double from a string representation of a variable and execute the modulus
   *
   * @param query the user query
   * @param data the dictionary of variables
   * @return Result with 1.0 if the variable exists score = 0.0 otherwise
   */
  def evaluate(query: String, data: AnalyzersDataInternal = AnalyzersDataInternal()): Result = {
    val score = data.stateData.variables.get(varName) match {
      case Some(value) =>
        try {
          value.toDouble % modulus
        } catch {
          case e: NumberFormatException =>
            throw ExceptionAtomic("ModulusVariable: numerical format exception " +
              "converting string to double", e)
          case NonFatal(e) =>
            throw ExceptionAtomic("ModulusVariable: exception casting string to double: " + e.getMessage)
        }
      case _ =>
        defValue
    }
    Result(score = score)
  }
}
