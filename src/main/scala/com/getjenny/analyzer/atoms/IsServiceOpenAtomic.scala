package com.getjenny.analyzer.atoms

/**
  * Created by angelo on 13/02/19.
  */

import com.getjenny.analyzer.expressions.{AnalyzersDataInternal, Result}

/** Check the variables, to determine whether or not the service is open
  *
  * first argument is a variable name containing service opening variable suffix
  */
class IsServiceOpenAtomic(val arguments: List[String],
                              restrictedArgs: Map[String, String]) extends AbstractAtomic {

  val varSuffix: String = arguments.headOption match {
    case Some(t) => t
    case _ => throw ExceptionAtomic("IsServiceOpen: must have a variable suffix name as argument")
  }

  override def toString: String = "isServiceOpen(\"" + varSuffix + "\")"

  val isEvaluateNormalized: Boolean = true

  def evaluate(query: String, data: AnalyzersDataInternal = AnalyzersDataInternal()): Result = {
    val score = data.data.get("__GJ_INTERNAL_SERVICEOPEN__") match {
      case Some(serviceOpen) =>
        val res = serviceOpen.asInstanceOf[Map[String, Boolean]]
        res.get(varSuffix) match {
          case Some(_) => 1.0d
          case _ => 0.0d
        }
      case _ => 0.0d
    }
    Result(score = score)
  }
}




