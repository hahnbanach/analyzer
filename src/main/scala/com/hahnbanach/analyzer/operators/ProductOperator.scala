package com.hahnbanach.analyzer.operators

import cats.implicits._
import com.hahnbanach.analyzer.entities.{AnalyzersData, Result, StateVariables}
import com.hahnbanach.analyzer.expressions._

/** Product Operator
  *
  * It multiply the scores of the inner expressions
  *
  * Created by Angelo Leto on 18/01/2023.
  */
class ProductOperator(children: List[Expression]) extends AbstractOperator(children: List[Expression]) {
  override def toString: String = "ProductOperator(" + children.mkString(", ") + ")"
  def add(e: Expression, level: Int = 0): AbstractOperator = {
    if (level === 0) new ProductOperator(e :: children)
    else {
      children.headOption match {
        case Some(t) =>
          t match {
            case c: AbstractOperator => new ProductOperator(c.add(e, level - 1) :: children.tail)
            case _ => throw OperatorException("ProductOperator: trying to add to smt else than an operator")
          }
        case _ =>
          throw OperatorException("ProductOperator: trying to add None instead of an operator")
      }
    }
  }

  def evaluate(query: String, data: AnalyzersData = AnalyzersData()): Result = {
    def operationFunction(l: List[Expression]): Result = {
      val valHead = l.headOption match {
        case Some(arg) => arg.evaluate(query, data)
        case _ => throw OperatorException("ProductOperator: inner expression is empty")
      }
      if (l.tail.isEmpty) {
        Result(score = valHead.score,
          AnalyzersData(
            context = data.context,
            stateData = StateVariables(
              traversedStates = data.stateData.traversedStates,
              // map summation order is important, as valHead elements must override pre-existing elements
              variables = data.stateData.variables ++
                valHead.data.stateData.variables
            ),
            internal = (data.internal.getOrElse(Map.empty) ++
              valHead.data.internal.getOrElse(Map.empty)).some
          )
        )
      } else {
        val valTail = operationFunction(l.tail)
        val finalScore = valHead.score * valTail.score
        Result(score = finalScore,
          AnalyzersData(
            context = data.context,
            stateData = StateVariables(
              traversedStates = data.stateData.traversedStates,
              // map summation order is important, as valHead elements must override valTail existing elements
              variables = valTail.data.stateData.variables ++
                valHead.data.stateData.variables
            ),
            internal = (valTail.data.internal.getOrElse(Map.empty) ++
              valHead.data.internal.getOrElse(Map.empty)).some
          )
        )
      }
    }
    operationFunction(children)
  }
}
