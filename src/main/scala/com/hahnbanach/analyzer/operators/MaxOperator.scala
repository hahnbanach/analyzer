package com.hahnbanach.analyzer.operators

import cats.implicits._
import com.hahnbanach.analyzer.entities.{AnalyzersDataInternal, Result, StateVariables}
import com.hahnbanach.analyzer.expressions._

/**
  * Created by angelo on 21/06/17.
  */

class MaxOperator(children: List[Expression]) extends AbstractOperator(children: List[Expression]) {
  override def toString: String = "MaxOperator(" + children.mkString(", ") + ")"
  def add(e: Expression, level: Int = 0): AbstractOperator = {
    if (level === 0) new MaxOperator(e :: children)
    else {
      children.headOption match {
        case Some(t) =>
          t match {
            case c: AbstractOperator => new MaxOperator(c.add(e, level - 1) :: children.tail)
            case _ => throw OperatorException("MaxOperator: trying to add to smt else than an operator")
          }
        case _ =>
          throw OperatorException("MaxOperator: trying to add None instead of an operator")
      }
    }
  }

  def evaluate(query: String, data: AnalyzersDataInternal = new AnalyzersDataInternal): Result = {
    def compMax(l: List[Expression]): Result = {
      val val1 = l.headOption match {
        case Some(arg) => arg.evaluate(query, data)
        case _ => throw OperatorException("MaxOperator: inner expression is empty")
      }
      val resultHead = Result(
        score = val1.score,
        AnalyzersDataInternal(
          context = data.context,
          stateData = StateVariables(
            traversedStates = data.stateData.traversedStates,
            variables = data.stateData.variables ++
              val1.data.stateData.variables
          ),
          data = data.data ++ val1.data.data
        )
      )
      if (l.tail.isEmpty) {
        resultHead
      } else {
        val val2 = compMax(l.tail)
        if (val1.score === val2.score)
          Result(
            score = val1.score,
            AnalyzersDataInternal(
              context = data.context,
              stateData = StateVariables(
                traversedStates = data.stateData.traversedStates,
                variables = val2.data.stateData.variables ++
                  val1.data.stateData.variables
              ),
              data = val2.data.data ++ val1.data.data
            )
          )
        else if(val1.score >= val2.score) resultHead else val2
      }
    }
    compMax(children)
  }
}
