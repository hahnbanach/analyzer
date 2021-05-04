package io.elegans.analyzer.atoms

import cats.implicits._
import io.elegans.analyzer.entities.{AnalyzersDataInternal, Result}

class HasTravStateInPositionRevAtomic(arguments: List[String], restrictedArgs: Map[String, String]) extends AbstractAtomic{
  val state: String = arguments.headOption match {
    case Some(t) => t
    case _ =>
      throw ExceptionAtomic("hasTravStateInPositionRev: missing first argument")
  }
  val position: Int = arguments.lift(1) match {
    case Some(t) => t.toInt
    case _ =>
      throw ExceptionAtomic("hasTravStateInPositionRev: missing second argument")
  }

  val isEvaluateNormalized: Boolean = true

  override def toString: String = "hasTravStateInPosition(\"" + state + "\", \"" + position + "\")"

  def evaluate(query: String, data: AnalyzersDataInternal = AnalyzersDataInternal()): Result = {
    data.stateData.traversedStates.reverse.lift(position-1) match {
      case Some(state) => if (state.state === this.state) Result(1) else Result(0)
      case _ => Result(0)
    }
  }
}
