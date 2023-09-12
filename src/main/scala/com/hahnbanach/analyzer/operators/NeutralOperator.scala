package com.hahnbanach.analyzer.operators

import cats.implicits._
import com.hahnbanach.analyzer.analyzers.AnalyzerParsingException
import com.hahnbanach.analyzer.entities.{AnalyzersData, Result}
import com.hahnbanach.analyzer.expressions._

/** Neutral Operator
  *
  * It can only take one argument --we leave List instead of Set as argument
  * so that Parser.gobble_command can add one Expression. Still, we throw exception if
  * more than one child is added
  *
  * Created by mal on 21/02/2017.
  */

class NeutralOperator(child: List[Expression]) extends AbstractOperator(child: List[Expression]) {
  require(child.length <= 1, "NeutralOperator can only have one Expression")
  override def toString: String = "neutral(" + child + ")"
  def add(e: Expression, level: Int = 0): AbstractOperator = {
    if (level === 0) {
      if (child.nonEmpty)
        throw AnalyzerParsingException("neutral: trying to add more than one expression at the root level.")
      new NeutralOperator(e :: child)
    } else child.headOption match {
      case Some(c: AbstractOperator) =>
        child.headOption.map(_ => child.tail) match {
          case Some(tail) =>
            if (tail.nonEmpty)
              throw OperatorException("NeutralOperator: more than one child expression.")
            else
              new NeutralOperator(c.add(e, level - 1) :: child.tail)
          case _ =>
            throw OperatorException("NeutralOperator: requires one argument")
        }
      case _ => throw OperatorException("NeutralOperator: trying to add to smt else than an operator.")
    }
  }

  def evaluate(query: String, data: AnalyzersData = AnalyzersData()): Result = {
    val res = child.headOption match {
      case Some(arg) => arg.evaluate(query, data)
      case _ => throw OperatorException("NeutralOperator: inner expression is empty")
    }
    Result(score = res.score, data = res.data.copy(
      stateData = data.stateData.copy(
        variables = data.stateData.variables ++ res.data.stateData.variables
      ),
      internal = res.data.internal.getOrElse(Map.empty[String, Any]).some)
    )
  }
}
