package com.hahnbanach.analyzer.analyzers

import com.hahnbanach.analyzer.entities.{AnalyzersData, DtHistoryItem, DtHistoryType, StateVariables}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CountStatesInHistoryTest extends AnyFlatSpec with Matchers {
  val data: AnalyzersData = AnalyzersData(
    stateData = StateVariables(
      traversedStates = Vector[DtHistoryItem](
        DtHistoryItem(state = "S10", `type` = DtHistoryType.EXTERNAL),
        DtHistoryItem(state = "S11", `type` = DtHistoryType.EXTERNAL),
        DtHistoryItem(state = "S1", `type` = DtHistoryType.EXTERNAL),
        DtHistoryItem(state = "S1", `type` = DtHistoryType.EXTERNAL),
        DtHistoryItem(state = "S1", `type` = DtHistoryType.EXTERNAL)
      ),
      variables = Map[String, String]()
    )
  )

  val restrictedArgs = Map.empty[String, String]
  "The count atom" should s"return 1.0d Counting S10" in {
    val analyzer = new DefaultAnalyzer(s"""countStateInHistory("state", "S10")""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe 1.0d
  }
  it should s"return 3.0d Counting S1" in {
    val analyzer = new DefaultAnalyzer(s"""countStateInHistory("state", "S1")""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("", data)
    analyzerValue.score shouldBe 3.0d
  }
  it should "return throw an exception if using a non expected parameter" in {
    a [AnalyzerCommandException] should be thrownBy {
      val analyzer = new DefaultAnalyzer(s"""countStateInHistory("state", "S1", "pippo", "pippo")""", restrictedArgs)
      val analyzerValue = analyzer.evaluate("", data)
    }
  }
}
