package com.hahnbanach.analyzer.operators

import cats.implicits._
import com.hahnbanach.analyzer.entities.{AnalyzersData, Result, StateVariables}
import com.hahnbanach.analyzer.expressions._

/** Division Operator
  *
  * It divide the first expression result, by the second
  *
  * Created by Angelo Leto on 18/01/2023.
  */
class DivisionOperator(child: List[Expression]) extends AbstractOperator(child: List[Expression]) {
  require(child.length <= 2, s"DivisionOperator can only have two expressions instead: ${child.length}")
  override def toString: String = "division(" + child.mkString(", ") + ")"

  def add(e: Expression, level: Int = 0): AbstractOperator = {
    if (level === 0) new DivisionOperator(e :: child)
    else {
      child.headOption match {
        case Some(t) =>
          t match {
            case c: AbstractOperator => new DivisionOperator(c.add(e, level - 1) :: child.tail)
            case _ => throw OperatorException("DivisionOperator: trying to add to smt else than an operator")
          }
        case _ =>
          throw OperatorException("DivisionOperator: trying to add None instead of an operator")
      }
    }
  }

  def evaluate(query: String, data: AnalyzersData = new AnalyzersData): Result = {
    val secondArgument = child.headOption match {
      case Some(t) => t
      case _ =>
        throw OperatorException("DivisionOperator: requires an expression as first argument")
    }

    val firstArgument = child.tail.headOption match {
      case Some(t) => t
      case _ =>
        throw OperatorException("DivisionOperator: requires an expression as second argument")
    }

    val res1: Result = firstArgument.evaluate(query = query, data = data)
    val res2: Result = secondArgument.evaluate(query = query, data = data)
    val score = if(res2.score =!= 0.0d) {
      res1.score / res2.score
    } else {
      0.0d
    }
    val resData = AnalyzersData(
      context = res1.data.context,
      stateData = StateVariables(
        traversedStates = res1.data.stateData.traversedStates,
        variables = res1.data.stateData.variables ++
          res2.data.stateData.variables
      ),
      internal = (res1.data.internal.getOrElse(Map.empty) ++
        res2.data.internal.getOrElse(Map.empty)).some
    )
    Result(score=score, data = resData)
  }
}
