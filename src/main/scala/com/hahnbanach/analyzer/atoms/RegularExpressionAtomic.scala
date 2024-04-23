package com.hahnbanach.analyzer.atoms

import com.hahnbanach.analyzer.entities.{AnalyzersData, Result}

/**
  * Created by mal on 20/02/2017.
  */

/** Counts the occurrences of a pattern into a string
  *
  * @param arguments regular expression
  */
class RegularExpressionAtomic(arguments: List[String], restrictedArgs: Map[String, String]) extends AbstractAtomic {
  /**
    * A Regex
    */
  val re: String = arguments.headOption match {
    case Some(t) => t
    case _ => throw ExceptionAtomic("RegularExpressionAtomic: must have one argument")
  }
  override def toString: String = "regex(\"" + re + "\")"
  val isEvaluateNormalized: Boolean = false
  private val rx = re.r

  def evaluate(query: String, data: AnalyzersData = AnalyzersData()): Result = {
    val score = rx.findAllIn(query).toList.length
    Result(score = score)
  }
}
