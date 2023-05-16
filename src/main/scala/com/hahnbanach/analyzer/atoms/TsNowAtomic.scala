package com.hahnbanach.analyzer.atoms

import cats.implicits._
import com.hahnbanach.analyzer.atoms.{AbstractAtomic, BaseAtomicException, KeyValueArgsBaseAtomic}
import com.hahnbanach.analyzer.entities.{AnalyzersData, Result, StateVariables}
import com.hahnbanach.analyzer.util.Time

/**
  * Created by angelo on 16/05/23.
  */

/** Send WhatsApp messages
  *
  * @param arguments see description below
  */
class TsNowAtomic(val arguments: List[String], restrictedArgs: Map[String, String]) extends AbstractAtomic with KeyValueArgsBaseAtomic {
  override def toString: String = "tsNow"
  override val factoryName: String = "tsNow"
  val isEvaluateNormalized: Boolean = true

  override val argumentsDescription: Map[String, (String, Boolean, Option[String])] = Map(
    //name -> explanation, isOptional, default value (if provided then arg is optional)
    "outName" -> (
      "the output variable name",
      false,
      "NOW_TS_MILLIS".some),
    "outPattern" -> (
      "a prefix for the output variables, default is \"local.\"",
      false,
      "local.".some
    )
  )

  /** send the message
    *
    * @param query the user query
    * @param data the dictionary of variables
    * @return Result 1.0 if no error occurred
    *
    */
  def evaluate(query: String, data: AnalyzersData = AnalyzersData()): Result = {
    val name = parameter("outName")(data.stateData.variables)
    val pattern = parameter("outPattern")(data.stateData.variables)
    Result(
      score = 1.0d,
      data = data.copy(
        stateData = data.stateData.copy(
          variables =
            data.stateData.variables.updated(s"$pattern$name", Time.timestampMillis.toString)
        )
      )
    )
  }
}