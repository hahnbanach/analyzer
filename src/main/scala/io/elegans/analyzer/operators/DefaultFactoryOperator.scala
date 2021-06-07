package io.elegans.analyzer.operators

/**
  * Created by Angelo Leto <angelo.leto@elegans.io> on 03/03/17.
  */

import io.elegans.analyzer.expressions.Expression
import io.elegans.analyzer.interfaces._

class DefaultFactoryOperator extends OperatorFactoryTrait[List[Expression]] {
  val functionsMap: Map[String, List[Expression] => AbstractOperator] = Map(
    ("booleanOr", (argument: List[Expression]) => new BooleanOrOperator(argument)),
    ("booleanor", (argument: List[Expression]) => new BooleanOrOperator(argument)),
    ("bor", (argument: List[Expression]) => new BooleanOrOperator(argument)),
    ("booleanAnd", (argument: List[Expression]) => new BooleanAndOperator(argument)),
    ("booleanand", (argument: List[Expression]) => new BooleanAndOperator(argument)),
    ("band", (argument: List[Expression]) => new BooleanAndOperator(argument)),
    ("booleanNot", (argument: List[Expression]) => new BooleanNotOperator(argument)),
    ("booleannot", (argument: List[Expression]) => new BooleanNotOperator(argument)),
    ("bnot", (argument: List[Expression]) => new BooleanNotOperator(argument)),
    ("conjunction", (argument: List[Expression]) => new ConjunctionOperator(argument)),
    ("and", (argument: List[Expression]) => new ConjunctionOperator(argument)),
    ("disjunction", (argument: List[Expression]) => new DisjunctionOperator(argument)),
    ("or", (argument: List[Expression]) => new DisjunctionOperator(argument)),
    ("maximum", (argument: List[Expression]) => new MaxOperator(argument)),
    ("max", (argument: List[Expression]) => new MaxOperator(argument)),
    ("binarize", (argument: List[Expression]) => new BinarizeOperator(argument)),
    ("eq", (argument: List[Expression]) => new EqOperator(argument)),
    ("lt", (argument: List[Expression]) => new LtOperator(argument)),
    ("gt", (argument: List[Expression]) => new GtOperator(argument)),
    ("lte", (argument: List[Expression]) => new LteOperator(argument)),
    ("gte", (argument: List[Expression]) => new GteOperator(argument))
  )

  val operations: Set[String] = functionsMap.keys.toSet
}
