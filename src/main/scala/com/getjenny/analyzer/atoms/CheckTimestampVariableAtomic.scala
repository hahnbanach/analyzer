package com.getjenny.analyzer.atoms

/**
  * Created by angelo on 05/07/18.
  */

import com.getjenny.analyzer.expressions.{AnalyzersDataInternal, Result}
import com.getjenny.analyzer.util.{ComparisonOperators, Time}

/** Check if the current time is Equal, LessOrEqual, Less, Greater, GreaterOrEqual to the argument time in EPOC
  *
  * first argument is a variable name containing a timestamp: in EPOC (in seconds)
  * second argument is the operator: any of Equal, LessOrEqual, Less, Greater, GreaterOrEqual
  */
class CheckTimestampVariableAtomic(val arguments: List[String],
                                   restrictedArgs: Map[String, String]) extends AbstractAtomic {

  val varName: String = arguments.headOption match {
    case Some(t) => t
    case _ => throw ExceptionAtomic("CheckTimestampVariable: must have a variable name as first argument")
  }

  val argOperator: String = arguments.lift(1) match {
    case Some(t) => t
    case _ => "GreaterOrEqual"
  }

  override def toString: String = "checkTimestampVariable(\"" + varName + ", " + argOperator + "\")"

  val isEvaluateNormalized: Boolean = true

  def evaluate(query: String, data: AnalyzersDataInternal = AnalyzersDataInternal()): Result = {
    val currTimestamp: Long = Time.timestampEpoc
    data.extractedVariables.get(varName) match {
      case Some(value) =>
        try {
          if (ComparisonOperators.compare(currTimestamp, value.toLong, argOperator))
            Result(score = 1.0)
          else
            Result(score = 0.0)
        } catch {
          case e: NumberFormatException =>
            throw ExceptionAtomic("DoubleNumberVariableAtomic: numerical format exception " +
              "converting string to long", e)
          case _: Throwable =>
            throw ExceptionAtomic("DoubleNumberVariableAtomic: unknown exception casting string to long")
        }
      case _ =>
        Result(score = 0.0)
    }
  }
}
