package com.hahnbanach.analyzer.operators

import cats.implicits._
import com.hahnbanach.analyzer.entities.{AnalyzersDataInternal, Result, StateVariables}
import com.hahnbanach.analyzer.expressions._

/**
  * Created by mal on 21/02/2017.
  */

class BooleanAndOperator(children: List[Expression]) extends AbstractOperator(children: List[Expression]) {
  override def toString: String = "BooleanAndOperator(" + children.mkString(", ") + ")"
  def add(e: Expression, level: Int = 0): AbstractOperator = {
    if (level === 0) new BooleanAndOperator(e :: children)
    else {
      children.headOption match {
        case Some(t) =>
          t match {
            case c: AbstractOperator => new BooleanAndOperator(c.add(e, level - 1) :: children.tail)
            case _ => throw OperatorException("BooleanAndOperator: trying to add to smt else than an operator")
          }
        case _ =>
          throw OperatorException("BooleanAndOperator: trying to add None instead of an operator")
      }
    }
  }

  def evaluate(query: String, data: AnalyzersDataInternal = AnalyzersDataInternal()): Result = {
    def booleanAnd(l: List[Expression]): Result = {
      val valHead = l.headOption match {
        case Some(arg) => arg.matches(query, data)
        case _ => throw OperatorException("BooleanAndOperator: inner expression is empty")
      }
      if (l.tail.isEmpty) {
        Result(score = valHead.score,
          AnalyzersDataInternal(
            context = data.context,
            stateData = StateVariables(
              traversedStates = data.stateData.traversedStates,
              // map summation order is important, as valHead elements must override pre-existing elements
              variables = data.stateData.variables ++
                valHead.data.stateData.variables
            ),
            data = data.data ++ valHead.data.data
          )
        )
      } else {
        val valTail = booleanAnd(l.tail)
        val finalScore = valHead.score * valTail.score
        if (finalScore  =!= 1.0d) {
          Result(score = finalScore, data = data)
        } else {
          Result(score = finalScore,
            AnalyzersDataInternal(
              context = data.context,
              stateData = StateVariables(
              traversedStates = data.stateData.traversedStates,
              // map summation order is important, as valHead elements must override valTail existing elements
              variables = valTail.data.stateData.variables ++
                valHead.data.stateData.variables
              ),
              data = valTail.data.data ++ valHead.data.data
            )
          )
        }
      }
    }
    booleanAnd(children)
  }
}
