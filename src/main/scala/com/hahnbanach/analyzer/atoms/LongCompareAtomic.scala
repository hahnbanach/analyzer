package com.hahnbanach.analyzer.atoms

import cats.implicits._
import com.hahnbanach.analyzer.atoms.{AbstractAtomic, BaseAtomicException, KeyValueArgsBaseAtomic}
import com.hahnbanach.analyzer.entities.{AnalyzersData, Result, StateVariables}
import com.hahnbanach.analyzer.util._

/**
  * Created by angelo on 16/05/23.
  */

/**  Compare long values
  *
  * @param arguments see description below
  */
class LongCompareAtomic(val arguments: List[String], restrictedArgs: Map[String, String]) extends AbstractAtomic with KeyValueArgsBaseAtomic {
  override def toString: String = "longAdd"
  override val factoryName: String = "longAdd"
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
      "any of LessOrEqual, Less, Greater, GreaterOrEqual, Equal",
      false,
      "LessOrEqual".some)
  )

  /** compare Long values the message
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

    val score = if(ComparisonOperators.compare(first, second, operator)) {
      1.0d
    } else {
      0.0d
    }

    Result(
      score = score
    )
  }
}