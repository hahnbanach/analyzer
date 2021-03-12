package io.elegans.analyzer.operators

/**
  * Created by Angelo Leto <angelo.leto@elegans.io> on 03/03/17.
  */

import io.elegans.analyzer.expressions.Expression
import io.elegans.analyzer.interfaces._

class DefaultFactoryOperator extends OperatorFactoryTrait[List[Expression], AbstractOperator] {

  override val operations: Set[String] = Set(
    "or",
    "and",
    "conjunction",
    "disjunction",
    "bor",
    "band",
    "booleanor",
    "booleanand",
    "booleanOr",
    "booleanAnd",
    "booleanNot",
    "booleannot",
    "bnot",
    "maximum",
    "max",
    "reinfConjunction",
    "binarize",
    "eq",
    "lt",
    "gt",
    "lte",
    "gte"
  )

  override def get(name: String, argument: List[Expression]): AbstractOperator = name.filter(c => !c.isWhitespace ) match {
    case "booleanOr" | "booleanor" | "bor" => new BooleanOrOperator(argument)
    case "booleanAnd"| "booleanand"| "band" => new BooleanAndOperator(argument)
    case "booleanNot"| "booleannot"| "bnot" => new BooleanNotOperator(argument)
    case "conjunction" | "and" => new ConjunctionOperator(argument)
    case "disjunction" | "or" => new DisjunctionOperator(argument)
    case "maximum" | "max" => new MaxOperator(argument)
    case "binarize" => new BinarizeOperator(argument)
    case "eq" => new EqOperator(argument)
    case "lt" => new LtOperator(argument)
    case "gt" => new GtOperator(argument)
    case "lte" => new LteOperator(argument)
    case "gte" => new GteOperator(argument)
    case "reinfConjunction" => new ReinfConjunctionOperator(argument)
    case _ => throw OperatorNotFoundException("Operator \'" + name + "\' not found")
  }

}
