package com.hahnbanach.analyzer.operators

import cats.implicits._
import com.hahnbanach.analyzer.entities.{AnalyzersData, Result, StateVariables}
import com.hahnbanach.analyzer.expressions._

import scala.util.{Failure, Success, Try}

/**
 * Created by mal on 21/02/2017.
 * Rewritten by al on 16/08/2023
 */

class BooleanOrOperator(children: List[Expression]) extends AbstractOperator(children: List[Expression]) {
  override def toString: String = "BooleanOrOperator(" + children.mkString(", ") + ")"

  def add(e: Expression, level: Int): AbstractOperator = {
    if (level === 0) new BooleanOrOperator(e :: children)
    else {
      children.headOption match {
        case Some(t) =>
          t match {
            case c: AbstractOperator => new BooleanOrOperator(c.add(e, level - 1) :: children.tail)
            case _ => throw OperatorException("BooleanOrOperator: trying to add to smt else than an operator")
          }
        case _ =>
          throw OperatorException("BooleanOrOperator: trying to add None instead of an operator")
      }
    }
  }

  def evaluate(query: String, data: AnalyzersData = AnalyzersData()): Result = {
    children.reverse.map(expression => { //evaluate in reverse order for backward compatibility
      Try(expression.matches(query, data)) match {
        case Success(value) => value
        case Failure(exception) =>
          throw OperatorException(toString, exception)
      }
    }).find(result => result.score >= 1.0d) match {
      case Some(evalRes) =>
        Result(
          score = 1.0d,
          data = data.copy(
            stateData = StateVariables(
              traversedStates = data.stateData.traversedStates,
              variables = data.stateData.variables ++
                evalRes.data.stateData.variables
            ),
            internal = (data.internal.getOrElse(Map.empty) ++
              evalRes.data.internal.getOrElse(Map.empty)).some
          )
        )
      case _ =>
        Result(score = 0.0d, data = data)
    }
  }
}
