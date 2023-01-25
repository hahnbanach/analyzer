package com.hahnbanach.analyzer.operators

/**
  * Created by Angelo Leto <angelo.leto@elegans.io> on 03/03/17.
  */

import com.hahnbanach.analyzer.expressions.Expression
import com.hahnbanach.analyzer.interfaces._

class DefaultFactoryOperator extends OperatorFactoryTrait[List[Expression]] {
  val functionsMap: Map[String, List[Expression] => AbstractOperator] = Map(
    ("and", (argument: List[Expression]) => new ConjunctionOperator(argument)),
    ("band", (argument: List[Expression]) => new BooleanAndOperator(argument)),
    ("binarize", (argument: List[Expression]) => new BinarizeOperator(argument)),
    ("bnot", (argument: List[Expression]) => new BooleanNotOperator(argument)),
    ("booleanAnd", (argument: List[Expression]) => new BooleanAndOperator(argument)),
    ("booleanNot", (argument: List[Expression]) => new BooleanNotOperator(argument)),
    ("booleanOr", (argument: List[Expression]) => new BooleanOrOperator(argument)),
    ("booleanand", (argument: List[Expression]) => new BooleanAndOperator(argument)),
    ("booleannot", (argument: List[Expression]) => new BooleanNotOperator(argument)),
    ("booleanor", (argument: List[Expression]) => new BooleanOrOperator(argument)),
    ("bor", (argument: List[Expression]) => new BooleanOrOperator(argument)),
    ("ceil", (argument: List[Expression]) => new CeilOperator(argument)),
    ("conjunction", (argument: List[Expression]) => new ConjunctionOperator(argument)),
    ("disjunction", (argument: List[Expression]) => new DisjunctionOperator(argument)),
    ("division", (argument: List[Expression]) => new DivisionOperator(argument)),
    ("eq", (argument: List[Expression]) => new EqOperator(argument)),
    ("floor", (argument: List[Expression]) => new FloorOperator(argument)),
    ("gt", (argument: List[Expression]) => new GtOperator(argument)),
    ("gte", (argument: List[Expression]) => new GteOperator(argument)),
    ("ifThenElse", (argument: List[Expression]) => new IfThenElseOperator(argument)),
    ("lt", (argument: List[Expression]) => new LtOperator(argument)),
    ("lte", (argument: List[Expression]) => new LteOperator(argument)),
    ("max", (argument: List[Expression]) => new MaxOperator(argument)),
    ("maximum", (argument: List[Expression]) => new MaxOperator(argument)),
    ("neutral", (argument: List[Expression]) => new NeutralOperator(argument)),
    ("or", (argument: List[Expression]) => new DisjunctionOperator(argument)),
    ("product", (argument: List[Expression]) => new ProductOperator(argument)),
    ("sum", (argument: List[Expression]) => new SumOperator(argument))
  )

  val operations: Set[String] = functionsMap.keys.toSet
}
