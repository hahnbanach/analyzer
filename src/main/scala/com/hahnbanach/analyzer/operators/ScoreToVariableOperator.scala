package com.hahnbanach.analyzer.operators

import cats.implicits._
import com.hahnbanach.analyzer.atoms.{AbstractAtomic, ParametersAtomic}
import com.hahnbanach.analyzer.entities.{AnalyzersData, Result, StateVariables}
import com.hahnbanach.analyzer.expressions._

/** Set the result into a variable
  *
  * Created by Angelo Leto on 19/10/2018.
  */

class ScoreToVariableOperator(child: List[Expression]) extends AbstractOperator(child: List[Expression]) {
  require(child.length <= 2, "ToVariableOperator can only have two Expression")
  override def toString: String = "scoreToVariable(" + child.mkString(", ") + ")"

  def add(e: Expression, level: Int = 0): AbstractOperator = {
    if (level === 0) new ScoreToVariableOperator(e :: child)
    else {
      child.headOption match {
        case Some(t) =>
          t match {
            case c: AbstractOperator => new ScoreToVariableOperator(c.add(e, level - 1) :: child.tail)
            case _ => throw OperatorException("ScoreToVariableOperator: trying to add to smt else than an operator")
          }
        case _ =>
          throw OperatorException("ScoreToVariableOperator: trying to add None instead of an operator")
      }
    }
  }


  def evaluate(query: String, data: AnalyzersData = AnalyzersData()): Result = {
    val res = child.headOption match {
      case Some(t) => t.evaluate(query = query, data = data)
      case _ =>
        throw OperatorException("ScoreToVariableOperator: requires an expression as second argument")
    }

    val variableName = child.tail.headOption match {
      case Some(t) if t.isInstanceOf[ParametersAtomic] =>
        t.asInstanceOf[ParametersAtomic].arguments.headOption.getOrElse(
          throw OperatorException("ScoreToVariableOperator: parameter not given")
        )
      case _ =>
        throw OperatorException("ScoreToVariableOperator: requires a ParameterAtomic expression as first argument")
    }

    Result(
      score = res.score,
      AnalyzersData(
        context = data.context,
        stateData = StateVariables(
          traversedStates = data.stateData.traversedStates,
          variables = data.stateData.variables ++
            res.data.stateData.variables.updated(variableName, res.score.toString)
        ),
        internal = (data.internal.getOrElse(Map.empty) ++
          res.data.internal.getOrElse(Map.empty)).some
      )
    )
  }
}
