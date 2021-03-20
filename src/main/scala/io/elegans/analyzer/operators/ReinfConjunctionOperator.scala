package io.elegans.analyzer.operators

import io.elegans.analyzer.entities.{AnalyzersDataInternal, Result, StateVariables}
import io.elegans.analyzer.expressions._

/**
  * Created by angelo on 18/01/2018.
  */

class ReinfConjunctionOperator(children: List[Expression]) extends AbstractOperator(children: List[Expression]) {
  override def toString: String = "ReinfConjunctionOperator(" + children.mkString(", ") + ")"
  def add(e: Expression, level: Int = 0): AbstractOperator = {

    level match {
      case 0 => new ReinfConjunctionOperator(e :: children)
      case _ =>
        if (children.isEmpty) {
          throw OperatorException("ReinfConjunctionOperator children list is empty")
        } else {
          children.headOption match {
            case Some(t) =>
              t match {
                case c: AbstractOperator => new ReinfConjunctionOperator(c.add(e, level - 1) :: children.tail)
                case _ => throw OperatorException("ReinfConjunctionOperator: trying to add to smt else than an operator")
              }
            case _ =>
              throw OperatorException("ReinfConjunctionOperator: trying to add None instead of an operator")
          }
        }
    }
  }

  val binThreshold: Double = 0.00000001d

  def evaluate(query: String, data: AnalyzersDataInternal = new AnalyzersDataInternal): Result = {
    def reinfConjunction(l: List[Expression]): Result = {
      val valHead = l.headOption match {
        case Some(arg) => arg.evaluate(query, data)
        case _ => throw OperatorException("ReinfConjunctionOperator: inner expression is empty")
      }
      if (l.tail.isEmpty) {
        Result(score = valHead.score * 1.1,
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
        val valTail = reinfConjunction(l.tail)
        val finalScore = (valHead.score * 1.1) * valTail.score
        if (finalScore < binThreshold) {
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
    reinfConjunction(children)
  }
}
