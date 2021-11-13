package com.hahnbanach.analyzer.operators

import cats.implicits._
import com.hahnbanach.analyzer.entities.{AnalyzersDataInternal, Result, StateVariables}
import com.hahnbanach.analyzer.expressions._

/**
  * Created by mal on 21/02/2017.
  */

class IfThenElseOperator(children: List[Expression]) extends AbstractOperator(children: List[Expression]) {
  override def toString: String = "IfThenElseOperator(" + children.mkString(", ") + ")"
  def add(e: Expression, level: Int = 0): AbstractOperator = {
    if (level === 0) new IfThenElseOperator(e :: children)
    else {
      children.headOption match {
        case Some(t) =>
          t match {
            case c: AbstractOperator => new IfThenElseOperator(c.add(e, level - 1) :: children.tail)
            case _ => throw OperatorException("IfThenElseOperator: trying to add to smt else than an operator")
          }
        case _ =>
          throw OperatorException("IfThenElseOperator: trying to add None instead of an operator")
      }
    }
  }

  def evaluate(query: String, data: AnalyzersDataInternal = AnalyzersDataInternal()): Result = {
    def ifThenElse(l: List[Expression]): Result = {
      val testExpressionRes = l.get(2)
        .getOrElse(throw OperatorException("Test was not provided"))
        .evaluate(query, data = data)

      val res = if(testExpressionRes.score >= 1.0) {
        l.get(1)
          .getOrElse(throw OperatorException("Then expression was not provided"))
          .evaluate(query, data = data)
      } else {
        l.get(0)
          .getOrElse(throw OperatorException("Else expression was not provided"))
          .evaluate(query, data = data)
      }

      Result(score = res.score,
        AnalyzersDataInternal(
          context = data.context,
          stateData = StateVariables(
            traversedStates = data.stateData.traversedStates,
            variables = data.stateData.variables ++
              res.data.stateData.variables
          ),
          data = data.data ++ res.data.data
        )
      )

    }
    ifThenElse(children)
  }
}
