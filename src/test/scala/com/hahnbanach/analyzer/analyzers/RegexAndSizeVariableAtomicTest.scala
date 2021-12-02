package com.hahnbanach.analyzer.analyzers

/**
  * Created by Angelo Leto <angelo.leto@hahnbanach.com> on 04/05/21.
  */

import com.hahnbanach.analyzer.entities.{AnalyzersData, DtHistoryItem, StateVariables}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RegexAndSizeVariableAtomicTest extends AnyFlatSpec with Matchers {

  val data: AnalyzersData = AnalyzersData(
    stateData = StateVariables(
      traversedStates = Vector.empty[DtHistoryItem],
      variables = Map[String, String](("variable0", "variable value value 0"), ("variable1", "0123456789"))
    )
  )

  val restrictedArgs = Map.empty[String, String]
  "regexVariable Atom" should "return 2.0d if the pattern is found twice" in {
    val analyzer = new DefaultAnalyzer("""regexVariableValue("variable0", "value")""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe 2.0d
  }
  it should "return 0.0d if the pattern is not matched" in {
    val analyzer = new DefaultAnalyzer("""regexVariableValue("variable0", "aaavalue")""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe 0.0d
  }
  it should "return 1.0d if the pattern is matched at the beginning of the string" in {
    val analyzer = new DefaultAnalyzer("""regexVariableValue("variable1", "^0123")""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe 1.0d
  }
  it should "return 0.0d if the pattern is not matched at the beginning of the string" in {
    val analyzer = new DefaultAnalyzer("""regexVariableValue("variable1", "0123$")""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe 0.0d
  }
  it should "return 0.0d if the variable doesn't exists" in {
    val analyzer = new DefaultAnalyzer("""regexVariableValue("variable100", "123$")""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe 0.0d
  }
  "variableSize Atom" should "return the correct size of the variable" in {
    val analyzer = new DefaultAnalyzer("""variableSize("variable1")""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe data.stateData.variables.getOrElse("variable1", "").length
  }
  it should "return 0.0d if the variable doesn't exists" in {
    val analyzer = new DefaultAnalyzer("""variableSize("variable1492")""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe 0.0d
  }
}
