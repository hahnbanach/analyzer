package com.hahnbanach.analyzer.operators

import com.hahnbanach.analyzer.entities.{AnalyzersDataInternal, Result, StateVariables}
import com.hahnbanach.analyzer.expressions._

/**
  * Created by mal on 21/02/2017.
  */

class ConjunctionOperator(children: List[Expression]) extends AbstractOperator(children: List[Expression]) {
  override def toString: String = "ConjunctionOperator(" + children.mkString(", ") + ")"
  def add(e: Expression, level: Int = 0): AbstractOperator = {
    level match {
      case 0 => new ConjunctionOperator(e :: children)
      case _ =>
        if (children.isEmpty) {
          throw OperatorException("ConjunctionOperator: children list is empty")
        } else {
          children.headOption match {
            case Some(t) =>
              t match {
                case c: AbstractOperator => new ConjunctionOperator(c.add(e, level - 1) :: children.tail)
                case _ => throw OperatorException("ConjunctionOperator: trying to add to smt else than an operator")
              }
            case _ => throw OperatorException("ConjunctionOperator: trying to add None instead of an operator")
          }
        }
    }
  }

  val binThreshold: Double = 0.00000001d

  def evaluate(query: String, data: AnalyzersDataInternal = AnalyzersDataInternal()): Result = {
    def conjunction(l: List[Expression]): Result = {
      val valHead = l.headOption match {
        case Some(arg) => arg.evaluate(query, data)
        case _ => throw OperatorException("ConjunctionOperator: inner expression is empty")
      }
      if (l.tail.isEmpty) {
        Result(score = valHead.score,
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
        val valTail = conjunction(l.tail)
        val finalScore = valHead.score * valTail.score
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
    conjunction(children)
  }
}
