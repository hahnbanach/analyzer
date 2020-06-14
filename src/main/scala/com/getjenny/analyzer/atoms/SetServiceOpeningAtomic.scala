package com.getjenny.analyzer.atoms

/**
  * Created by angelo on 13/02/19.
  */

import com.getjenny.analyzer.expressions.{AnalyzersDataInternal, Result}
import com.getjenny.analyzer.util.{JsonToEntities, Time}
import scalaz.Scalaz._

import scala.util.matching.Regex

/** Check the variables, determine whether or not the service is open and set the variable
  *   to be used by the isServiceOpen atom
  *
  * first argument is a variable name containing service opening variable suffix
  */
class SetServiceOpeningAtomic(val arguments: List[String],
                              restrictedArgs: Map[String, String]) extends AbstractAtomic {

  private[this] val variablesRegex: Regex = """(?:GJ_SERVICEOPEN_([\w\d\.\_]{1,256}))""".r

  override def toString: String = "setServiceOpening()"

  val isEvaluateNormalized: Boolean = true

  private[this] def matchName(variableName: String): String = {
    variableName match {
      case variablesRegex(name) => name
      case _ => ""
    }
  }

  def evaluate(query: String, data: AnalyzersDataInternal = AnalyzersDataInternal()): Result = {
    // fetch the variables, parse JSON and create the OpeningTime data
    val variables = data.extractedVariables.map{ case(k, v) =>
      val name = matchName(k)
      (name, v)
    }.filterKeys(_ =/= "").map{ case(k, v) =>
      (k, JsonToEntities.openingTime(v))
    }.map{ case(k, v) =>
      val hourMin = v.openTime.take(2).toInt
      val hourMax = v.closeTime.take(2).toInt
      val minutesMin = v.openTime.takeRight(2).toInt
      val minutesMax = v.closeTime.takeRight(2).toInt

      val timezone = v.timezone
      val hour = Time.hour(timezone)
      val minutes = Time.minutes(timezone)
      val month = Time.monthInt(timezone)
      val day = Time.monthInt(timezone)
      val weekday = Time.dayOfWeekInt(timezone)

      val isOpen: Boolean = hour >= hourMin && hour <= hourMax && minutes >= minutesMin && minutes <= minutesMax &&
        (v.months.isEmpty || v.months.contains(month)) &&
        (v.days.isEmpty || v.days.contains(day)) &&
        (v.weekDays.isEmpty || v.weekDays.contains(weekday))
      (k, isOpen)
    }.filter{case(_, v) => v}

    val serviceOpenVariable: Map[String, Map[String, Boolean]] = Map("__GJ_INTERNAL_SERVICEOPEN__" -> variables)
    val newData = AnalyzersDataInternal(
      context = data.context,
      traversedStates = data.traversedStates,
      extractedVariables = data.extractedVariables,
      data = data.data ++ serviceOpenVariable
    )

    Result(score = 0.0, newData) // score is ever 0.0, since it is not to be returned
  }
}
