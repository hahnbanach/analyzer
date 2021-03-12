package io.elegans.analyzer.operators

import io.elegans.analyzer.entities.{AnalyzersDataInternal, Result, StateVariables}
import io.elegans.analyzer.expressions._
import scalaz.Scalaz._

/**
  * Created by mal on 21/02/2017.
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

  def evaluate(query: String, data: AnalyzersDataInternal = AnalyzersDataInternal()): Result = {
    def booleanOr(l: List[Expression]): Result = {
      val res = l.headOption match {
        case Some(arg) => arg.matches(query, data)
        case _ => throw OperatorException("BooleanOrOperator: inner expression is empty")
      }
      if (l.tail.isEmpty) {
        Result(score = 1.0d - res.score,
          AnalyzersDataInternal(
            context = data.context,
            stateData = StateVariables(
              traversedStates = data.stateData.traversedStates,
              // map summation order is important, as res elements must override pre-existing elements
              variables = data.stateData.variables ++
                res.data.stateData.variables
            ),
            data = data.data ++ res.data.data
          )
        )
      } else {
        val resTail = booleanOr(l.tail)
        Result(score = (1.0d - res.score) * resTail.score,
          AnalyzersDataInternal(
            context = data.context,
            stateData = StateVariables(
              traversedStates = data.stateData.traversedStates,
              // map summation order is important, as res elements must override resTail existing elements
              variables = resTail.data.stateData.variables ++
                res.data.stateData.variables
            ),
            data = resTail.data.data ++ res.data.data
          )
        )
      }
    }
    val resBooleanOr = booleanOr(children)
    val finalScore = 1.0d - resBooleanOr.score
    if (finalScore < 1.0d) {
      Result(
        score = finalScore,
        data = data
      )
    } else {
      Result(
        score = finalScore,
        data = resBooleanOr.data
      )
    }
  }
}
