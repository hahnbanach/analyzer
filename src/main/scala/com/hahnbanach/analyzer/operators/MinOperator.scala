package com.hahnbanach.analyzer.operators

import cats.implicits._
import com.hahnbanach.analyzer.entities.{AnalyzersData, Result, StateVariables}
import com.hahnbanach.analyzer.expressions._

/**
  * Created by angelo on 21/06/17.
  */

class MinOperator(children: List[Expression]) extends AbstractOperator(children: List[Expression]) {
  override def toString: String = "MinOperator(" + children.mkString(", ") + ")"
  def add(e: Expression, level: Int = 0): AbstractOperator = {
    if (level === 0) new MinOperator(e :: children)
    else {
      children.headOption match {
        case Some(t) =>
          t match {
            case c: AbstractOperator => new MinOperator(c.add(e, level - 1) :: children.tail)
            case _ => throw OperatorException("MinOperator: trying to add to smt else than an operator")
          }
        case _ =>
          throw OperatorException("MinOperator: trying to add None instead of an operator")
      }
    }
  }

  def evaluate(query: String, data: AnalyzersData = new AnalyzersData): Result = {
    def compMin(l: List[Expression]): Result = {
      val val1 = l.headOption match {
        case Some(arg) => arg.evaluate(query, data)
        case _ => throw OperatorException("MinOperator: inner expression is empty")
      }
      val resultHead = Result(
        score = val1.score,
        AnalyzersData(
          context = data.context,
          stateData = StateVariables(
            traversedStates = data.stateData.traversedStates,
            variables = data.stateData.variables ++
              val1.data.stateData.variables
          ),
          internal = (data.internal.getOrElse(Map.empty) ++
            val1.data.internal.getOrElse(Map.empty)).some
        )
      )
      if (l.tail.isEmpty) {
        resultHead
      } else {
        val val2 = compMin(l.tail)
        if (val1.score === val2.score)
          Result(
            score = val1.score,
            AnalyzersData(
              context = data.context,
              stateData = StateVariables(
                traversedStates = data.stateData.traversedStates,
                variables = val2.data.stateData.variables ++
                  val1.data.stateData.variables
              ),
              internal = (val2.data.internal.getOrElse(Map.empty) ++
                val1.data.internal.getOrElse(Map.empty)).some
            )
          )
        else if(val1.score <= val2.score) resultHead else val2
      }
    }
    compMin(children)
  }
}
