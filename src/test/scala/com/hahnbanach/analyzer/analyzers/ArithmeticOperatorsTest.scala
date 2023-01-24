package com.hahnbanach.analyzer.analyzers

import com.hahnbanach.analyzer.entities.{AnalyzersData, DtHistoryItem, StateVariables}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ArithmeticOperatorsTest extends AnyFlatSpec with Matchers {

  val data: AnalyzersData = AnalyzersData(
    stateData = StateVariables(
      traversedStates = Vector.empty[DtHistoryItem],
      variables = Map[String, String](("NUMBER0", "10"),
        ("NUMBER1", "1"),
        ("NUMBER2", "2"),
        ("NUMBER3", "3"),
        ("NUMBER4", "4"),
        ("NUMBER5", "5")
      )
    )
  )

  val restrictedArgs = Map.empty[String, String]

  "division" should s"return 4.0 since 8/2 = 4" in {
    val analyzer = new DefaultAnalyzer("""division(toDouble("8.0"), doubleNumberVariable("NUMBER2", "0"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe 4.0d
  }
  it should s"return 0.0d since 0 / 18.012 = 0" in {
    val analyzer = new DefaultAnalyzer("""division(doubleNumberVariable("NUMBER_NOT_EXISTS", "0"), toDouble("18.012"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe 0.0d
  }
  it should s"return 0.0d since we define 18.012 / 0 = 0 (NaN is not supported as a score)" in {
    val analyzer = new DefaultAnalyzer("""division(toDouble("18.012"), doubleNumberVariable("NUMBER_NOT_EXISTS", "0"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe 0.0d
  }
  it should s"return 3 since 0.75 / 0.25" in {
    val analyzer = new DefaultAnalyzer("""division(toDouble("0.75"), toDouble("0.25"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe 3.0d
  }

  "product" should s"return 64.0d since 8 * 2 * 4 = 64" in {
    val analyzer = new DefaultAnalyzer(s"""product(toDouble("8.0"), doubleNumberVariable("NUMBER2", "0"), toDouble("4.0"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe 64.0d
  }
  it should s"return 0.0d since 8 * 2 * 4 * 0 = 0" in {
    val analyzer = new DefaultAnalyzer(s"""product(doubleNumberVariable("NUMBER_NOT_EXISTS", "0"), toDouble("8.0"), doubleNumberVariable("NUMBER2", "0"), toDouble("4.0"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe 0.0d
  }

  "sum" should s"return 60.0d since 38 + 12 + 10 = 60" in {
    val analyzer = new DefaultAnalyzer(s"""sum(toDouble("38.0"), toDouble("12"), toDouble("10.0"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe 60.0d
  }
  it should s"return -40.0d since 38 + 12 + 10 - 100 = -40" in {
    val analyzer = new DefaultAnalyzer(s"""sum(toDouble("38.0"), toDouble("12"), toDouble("10.0"), toDouble("-100"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe -40.0d
  }

}
