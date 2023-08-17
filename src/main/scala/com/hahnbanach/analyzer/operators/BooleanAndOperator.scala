package com.hahnbanach.analyzer.operators

import cats.implicits._
import com.hahnbanach.analyzer.entities.{AnalyzersData, Result, StateVariables}
import com.hahnbanach.analyzer.expressions._

import scala.util.{Failure, Success, Try}

/**
 * Created by mal on 21/02/2017.
 * Rewritten by al on 16/08/2023
 */

class BooleanAndOperator(children: List[Expression]) extends AbstractOperator(children: List[Expression]) {
  override def toString: String = "BooleanAndOperator(" + children.mkString(", ") + ")"

  def add(e: Expression, level: Int): AbstractOperator = {
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

  def evaluate(query: String, data: AnalyzersData = AnalyzersData()): Result = {
    children.foldRight(Result(score = 1.0d, data = data))((expression, acc) => { //evaluate in reverse order for backward compatibility
      val evalRes = Try(expression.matches(query, data)) match {
        case Success(value) => value
        case Failure(exception) =>
          throw OperatorException(toString, exception)
      }
      if(acc.score =!= 0.0d && evalRes.score =!= 0.0d) {
        acc.copy(
          score = evalRes.score * acc.score,
          data = acc.data.copy(
            stateData = StateVariables(
              traversedStates = data.stateData.traversedStates,
              variables = acc.data.stateData.variables ++
                evalRes.data.stateData.variables
            ),
            internal = (acc.data.internal.getOrElse(Map.empty) ++
              evalRes.data.internal.getOrElse(Map.empty)).some
          )
        )
      } else {
        Result(score = 0.0d, data = data)
      }
    })
  }
}
