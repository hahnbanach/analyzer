package com.hahnbanach.analyzer.operators

import cats.implicits._
import com.hahnbanach.analyzer.entities.{AnalyzersData, Result, StateVariables}
import com.hahnbanach.analyzer.expressions._

/** Floor Operator
  *
  * It can only take one argument and return floor of the inner score
  *
  * Created by Angelo Leto on 25/01/2023.
  */

class FloorOperator(child: List[Expression]) extends AbstractOperator(child: List[Expression]) {
  require(child.length <= 1, "FloorOperator can only have one Expression")
  override def toString: String = "floor(" + child + ")"
  def add(e: Expression, level: Int = 0): AbstractOperator = {
    if (level === 0) {
      if (child.nonEmpty)
        throw OperatorException("FloorOperator: trying to add more than one expression.")
      new FloorOperator(e :: child)
    } else child.headOption match {
      case Some(c: AbstractOperator) =>
        child.headOption.map(_ => child.tail) match {
          case Some(tail) =>
            if (tail.nonEmpty)
              throw OperatorException("FloorOperator: more than one child expression.")
            else
              new FloorOperator(c.add(e, level - 1) :: child.tail)
          case _ =>
            throw OperatorException("FloorOperator: requires one argument")
        }
      case _ => throw OperatorException("FloorOperator: trying to add to smt else than an operator.")
    }
  }

  def evaluate(query: String, data: AnalyzersData = AnalyzersData()): Result = {
    val res = child.headOption match {
      case Some(arg) => arg.evaluate(query, data)
      case _ => throw OperatorException("FloorOperator: inner expression is empty")
    }

    Result(
      score = res.score.floor,
      AnalyzersData(
        context = data.context,
        stateData = StateVariables(
          traversedStates = data.stateData.traversedStates,
          variables = data.stateData.variables ++
            res.data.stateData.variables
        ),
        internal = (data.internal.getOrElse(Map.empty) ++
          res.data.internal.getOrElse(Map.empty)).some
      )
    )
  }
}
