package com.hahnbanach.analyzer.operators

import cats.implicits._
import com.hahnbanach.analyzer.entities.{AnalyzersDataInternal, Result, StateVariables}
import com.hahnbanach.analyzer.expressions._

/** IfThenElse Operator
  *
  * ifThenElse(test, trueAtom, falseAtom)
  *
  * Created by Angelo Leto on 13/11/2021.
  */

class IfThenElseOperator(child: List[Expression]) extends AbstractOperator(child: List[Expression]) {
  require(child.length === 3, "ifThenElse accept three atoms")
  override def toString: String = "ifThenElse(" + child.mkString(", ") + ")"

  def add(e: Expression, level: Int = 0): AbstractOperator = {
    println("AAAAA")
    if (level === 0) new BooleanAndOperator(e :: child)
    else {
      child.headOption match {
        case Some(t) =>
          t match {
            case c: AbstractOperator => new BooleanAndOperator(c.add(e, level - 1) :: child.tail)
            case _ => throw OperatorException("ifThenElse: trying to add to smt else than an operator")
          }
        case _ =>
          throw OperatorException("ifThenElse: trying to add None instead of an operator")
      }
    }
  }

  def evaluate(query: String, data: AnalyzersDataInternal = new AnalyzersDataInternal): Result = {
    val testArument = child.headOption match {
      case Some(t) => t
      case _ =>
        throw OperatorException("IfThenElse: requires an expression as first argument")
    }

    val thenArgument = child.tail.headOption match {
      case Some(t) => t
      case _ =>
        throw OperatorException("IfThenElse: requires an expression as second argument")
    }

    val elseArgument = child.tail.headOption match {
      case Some(t) => t
      case _ =>
        throw OperatorException("IfThenElse: requires an expression as third argument")
    }

    val testResult : Result = testArument.evaluate(query = query, data = data)
    val res: Result = if(testResult.score === 1.0) {
      thenArgument.evaluate(query = query, data = data)
    } else {
      elseArgument.evaluate(query = query, data = data)
    }

    val resData = AnalyzersDataInternal(
      context = res.data.context,
      stateData = StateVariables(
        traversedStates = res.data.stateData.traversedStates,
        variables = res.data.stateData.variables
      ),
      data = testResult.data.data ++ res.data.data
    )

    Result(score=res.score, data = resData)
  }
}
