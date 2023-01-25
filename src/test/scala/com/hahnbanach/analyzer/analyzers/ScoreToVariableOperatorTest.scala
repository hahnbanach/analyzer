package com.hahnbanach.analyzer.analyzers

import com.hahnbanach.analyzer.entities.{AnalyzersData, DtHistoryItem, StateVariables}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ScoreToVariableOperatorTest extends AnyFlatSpec with Matchers {

  val data: AnalyzersData = AnalyzersData(
    stateData = StateVariables(
      traversedStates = Vector.empty[DtHistoryItem],
      variables = Map[String, String](
      )
    )
  )

  val restrictedArgs = Map.empty[String, String]

  "scoreToVariable" should s"return 4.0 and check that VARIABLE1 exists and is 4.0" in {
    val analyzer = new DefaultAnalyzer("""scoreToVariable(parameters("VARIABLE1"), ceil(toDouble("3.801")))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe 4.0d
    analyzerValue.data.stateData.variables.getOrElse("VARIABLE1", "0.0").toDouble shouldBe 4.0d
  }
  it should s"return 3.0d" in {
    val analyzer = new DefaultAnalyzer(s"""scoreToVariable(parameters("VARIABLE1"), floor(toDouble("3.801")))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe 3.0d
    analyzerValue.data.stateData.variables.getOrElse("VARIABLE1", "0.0").toDouble shouldBe 3.0d
  }
  it should s"return 6.0d" in {
    val analyzer = new DefaultAnalyzer(s"""scoreToVariable(parameters("VARIABLE2"), floor(toDouble("6.801")))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe 6.0d
    analyzerValue.data.stateData.variables.getOrElse("VARIABLE2", "0.0").toDouble shouldBe 6.0d
  }
}
