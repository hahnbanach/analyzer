package com.hahnbanach.analyzer.atoms

import cats.implicits._
import com.hahnbanach.analyzer.entities.{AnalyzersDataInternal, Result}
import scala.util.matching.Regex

/** Count states on history
  *
  * @param arguments see description below
  */
class CountStateInHistoryAtomic(val arguments: List[String],
                                restrictedArgs: Map[String, String]) extends AbstractAtomic with KeyValueArgsBaseAtomic {
  override def toString: String = "countStateInHistoryAtomic"
  override val factoryName: String = "countStateInHistoryAtomic"
  val isEvaluateNormalized: Boolean = false
  override val argumentsDescription: Map[String, (String, Option[String])] = Map(
    //name -> explanation, default (if provided then arg is optional)
    "state" -> (
      "state name to count",
      None),
    "maxContext" -> (
      "restrict the count to a limited amount of states starting from the end of the history," +
        "if not provided count on the entire history",
      "".some)
  )

  val state: Map[String, String] => String = parameter("state")
  val maxContext: Map[String, String] => String = parameter("maxContext")

  private[this] val nRegex: Regex = "^([0-9]+)$".r

  def evaluate(query: String, data: AnalyzersDataInternal = AnalyzersDataInternal()): Result = {
    val stateName = state(data.stateData.variables)
    val maxCtxtSize = maxContext(data.stateData.variables) match {
      case nRegex(value) => value.toInt
      case _ => data.stateData.variables.size
    }
    val count = data.stateData.traversedStates.reverse.take(maxCtxtSize).count(s => {
      s.state === stateName
    })
    Result(count, data)
  }
}
