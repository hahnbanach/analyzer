package io.elegans.analyzer.analyzers

import io.elegans.analyzer.entities.{AnalyzersDataInternal, DtHistoryItem, StateVariables}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DoubleComparisonOperatorsTests extends AnyFlatSpec with Matchers {
  val data: AnalyzersDataInternal = AnalyzersDataInternal(
    stateData = StateVariables(
      traversedStates = Vector.empty[DtHistoryItem],
      variables = Map[String, String](("NUMBER0", "10"),
        ("NUMBER1", "19"),
        ("NUMBER2", "20"),
        ("NUMBER3", "21"),
        ("NUMBER4", "100")
      )
    )
  )

  val refN = 21.0
  val restrictedArgs = Map.empty[String, String]
  "comparison operators" should s"return 1.0d since 10 < ${refN}" in {
    val analyzer = new DefaultAnalyzer(s"""band(gt(doubleNumberVariable("NUMBER0", "0"), toDouble("0")), lt(doubleNumberVariable("NUMBER0", "0"), toDouble("${refN}")))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe 1.0d
  }
  it should s"return 1.0d since 19 < ${refN}" in {
    val analyzer = new DefaultAnalyzer(s"""band(gt(doubleNumberVariable("NUMBER1", "0"), toDouble("0")), lt(doubleNumberVariable("NUMBER1", "0"), toDouble("${refN}")))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe 1.0d
  }
  it should s"return 1.0d since 20 < ${refN}" in {
    val analyzer = new DefaultAnalyzer(s"""band(gt(doubleNumberVariable("NUMBER2", "0"), toDouble("0")), lt(doubleNumberVariable("NUMBER2", "0"), toDouble("${refN}")))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe 1.0d
  }
  it should s"return 0.0d since 21 = ${refN}" in {
    val analyzer = new DefaultAnalyzer(s"""band(gt(doubleNumberVariable("NUMBER3", "0"), toDouble("0")), lt(doubleNumberVariable("NUMBER3", "0"), toDouble("${refN}")))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe 0.0d
  }
  it should s"return 0.0d since 100 > ${refN}" in {
    val analyzer = new DefaultAnalyzer(s"""band(gt(doubleNumberVariable("NUMBER4", "0"), toDouble("0")), lt(doubleNumberVariable("NUMBER4", "0"), toDouble("${refN}")))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe 0.0d
  }
  it should s"return 1.0d since ${refN} > 10" in {
    val analyzer = new DefaultAnalyzer(s"""gt(toDouble("${refN}"), doubleNumberVariable("NUMBER0"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe 1.0d
  }
  it should s"return 1.0d since 21 >= ${refN}" in {
    val analyzer = new DefaultAnalyzer(s"""gte(doubleNumberVariable("NUMBER3", "0"), toDouble("${refN}"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe 1.0d
  }
  it should s"return 1.0d since 21 <= ${refN}" in {
    val analyzer = new DefaultAnalyzer(s"""lte(doubleNumberVariable("NUMBER3", "0"), toDouble("${refN}"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe 1.0d
  }
  it should s"return 1.0d since 21 = ${refN}" in {
    val analyzer = new DefaultAnalyzer(s"""eq(doubleNumberVariable("NUMBER3", "0"), toDouble("${refN}"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe 1.0d
  }
}
