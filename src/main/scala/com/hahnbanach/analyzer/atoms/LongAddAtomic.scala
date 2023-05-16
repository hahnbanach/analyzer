package com.hahnbanach.analyzer.atoms

import cats.implicits._
import com.hahnbanach.analyzer.atoms.{AbstractAtomic, BaseAtomicException, KeyValueArgsBaseAtomic}
import com.hahnbanach.analyzer.entities.{AnalyzersData, Result, StateVariables}
import com.hahnbanach.analyzer.util._

/**
  * Created by angelo on 16/05/23.
  */

/** Send WhatsApp messages
  *
  * @param arguments see description below
  */
class LongAddAtomic(val arguments: List[String], restrictedArgs: Map[String, String]) extends AbstractAtomic with KeyValueArgsBaseAtomic {
  override def toString: String = "longCompare"
  override val factoryName: String = "longCompare"
  val isEvaluateNormalized: Boolean = true

  override val argumentsDescription: Map[String, (String, Boolean, Option[String])] = Map(
    //name -> explanation, isOptional, default value (if provided then arg is optional)
    "first" -> (
      "the first operand",
      true,
      None),
    "second" -> (
      "the first operand",
      true,
      None),
    "operator" -> (
      "the operator which is either + or -",
      false,
      "-".some),
    "outName" -> (
      "the output variable name",
      false,
      "LONG_ADD_RESULT".some),
    "outPattern" -> (
      "a prefix for the output variables, default is \"local.\"",
      false,
      "local.".some
    )
  )

  /** Sum or Sub Long values
    *
    * @param query the user query
    * @param data the dictionary of variables
    * @return Result 1.0 if no error occurred
    *
    */
  def evaluate(query: String, data: AnalyzersData = AnalyzersData()): Result = {
    val first = parameter("first")(data.stateData.variables).toLong
    val second = parameter("second")(data.stateData.variables).toLong
    val operator = parameter("operator")(data.stateData.variables)
    val name = parameter("outName")(data.stateData.variables)
    val pattern = parameter("outPattern")(data.stateData.variables)

    val result = operator match {
      case "+" | "plus" | "sum" | "add" =>
        first + second
      case _ => first - second
    }

    Result(
      score = 1.0d,
      data = data.copy(
        stateData = data.stateData.copy(
          variables =
            data.stateData.variables.updated(s"$pattern$name", result.toString)
        )
      )
    )
  }
}
