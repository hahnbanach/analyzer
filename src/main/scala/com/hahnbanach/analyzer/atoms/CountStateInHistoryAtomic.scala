package com.hahnbanach.analyzer.atoms

import cats.implicits._
import com.hahnbanach.analyzer.entities.{AnalyzersData, Result}
import scala.util.matching.Regex

/** Count states on history
  *
  * @param arguments see description below
  */
class CountStateInHistoryAtomic(val arguments: List[String],
                                restrictedArgs: Map[String, String]) extends AbstractAtomic with KeyValueArgsBaseAtomic {
  override def toString: String = "countStateInHistory"
  override val factoryName: String = "countStateInHistory"
  val isEvaluateNormalized: Boolean = false
  override val argumentsDescription: Map[String, (String, Boolean, Option[String])] = Map(
    //name -> explanation, default (if provided then arg is optional)
    "state" -> (
      "state name to count",
      true,
      None),
    "floor" -> (
      "floor value, used as a base",
      false,
      "0".some
    ),
    "maxContext" -> (
      "restrict the count to a limited amount of states starting from the end of the history," +
        "if not provided count on the entire history",
      false,
      None)
  )

  val state: Map[String, String] => String = parameter("state")
  val maxContext: Map[String, String] => Option[String] = optParameter("maxContext")
  val floor: Map[String, String] => Option[String] = optParameter("floor")

  private[this] val nRegex: Regex = "^([0-9]+)$".r

  def evaluate(query: String, data: AnalyzersData = AnalyzersData()): Result = {
    val stateName = state(data.stateData.variables)
    val floorValue = floor(data.stateData.variables) match {
      case Some(v) => v.toDouble
      case _ => 0.0d
    }
    val maxCtxSize = maxContext(data.stateData.variables).getOrElse("") match {
      case nRegex(value) => value.toInt
      case _ => data.stateData.traversedStates.size
    }
    val count = data.stateData.traversedStates.reverse.take(maxCtxSize).count(s => {
      s.state === stateName
    })
    val finalCount = floorValue + count
    Result(finalCount, data)
  }
}
