package com.hahnbanach.analyzer.analyzers

import com.hahnbanach.analyzer.entities.{AnalyzersData, DtHistoryItem, DtHistoryType, StateVariables}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class StateHistoryAtomicsTest extends AnyFlatSpec with Matchers {

  val restrictedArgs: Map[String, String] = Map.empty[String, String]

  val stateA = "state_a"
  val stateB = "state_b"
  val stateC = "state_c"

  val dataWithHistory: AnalyzersData = AnalyzersData(
    stateData = StateVariables(
      traversedStates = Vector(
        DtHistoryItem(state = stateA, `type` = DtHistoryType.EXTERNAL),
        DtHistoryItem(state = stateB, `type` = DtHistoryType.EXTERNAL),
        DtHistoryItem(state = stateC, `type` = DtHistoryType.EXTERNAL)
      )
    )
  )

  val dataEmpty: AnalyzersData = AnalyzersData()

  // --- HasTravStateAtomic ---

  "hasTravState" should "return 1.0 when state exists in history" in {
    val analyzer = new DefaultAnalyzer(s"""hasTravState("$stateA")""", restrictedArgs)
    val result = analyzer.evaluate("test", dataWithHistory)
    result.score shouldBe 1.0
  }

  it should "return 1.0 for middle state in history" in {
    val analyzer = new DefaultAnalyzer(s"""hasTravState("$stateB")""", restrictedArgs)
    val result = analyzer.evaluate("test", dataWithHistory)
    result.score shouldBe 1.0
  }

  it should "return 1.0 for last state in history" in {
    val analyzer = new DefaultAnalyzer(s"""hasTravState("$stateC")""", restrictedArgs)
    val result = analyzer.evaluate("test", dataWithHistory)
    result.score shouldBe 1.0
  }

  it should "return 0.0 when state does not exist in history" in {
    val analyzer = new DefaultAnalyzer("""hasTravState("nonexistent")""", restrictedArgs)
    val result = analyzer.evaluate("test", dataWithHistory)
    result.score shouldBe 0.0
  }

  it should "return 0.0 when history is empty" in {
    val analyzer = new DefaultAnalyzer(s"""hasTravState("$stateA")""", restrictedArgs)
    val result = analyzer.evaluate("test", dataEmpty)
    result.score shouldBe 0.0
  }

  // --- LastTravStateIsAtomic ---

  "lastTravStateIs" should "return 1.0 when last state matches" in {
    val analyzer = new DefaultAnalyzer(s"""lastTravStateIs("$stateC")""", restrictedArgs)
    val result = analyzer.evaluate("test", dataWithHistory)
    result.score shouldBe 1.0
  }

  it should "return 0.0 when last state does not match" in {
    val analyzer = new DefaultAnalyzer(s"""lastTravStateIs("$stateA")""", restrictedArgs)
    val result = analyzer.evaluate("test", dataWithHistory)
    result.score shouldBe 0.0
  }

  it should "return 0.0 when history is empty" in {
    val analyzer = new DefaultAnalyzer(s"""lastTravStateIs("$stateA")""", restrictedArgs)
    val result = analyzer.evaluate("test", dataEmpty)
    result.score shouldBe 0.0
  }

  // --- PrevTravStateIsAtomic ---

  "prevTravStateIs" should "return 1.0 when penultimate state matches" in {
    val analyzer = new DefaultAnalyzer(s"""prevTravStateIs("$stateB")""", restrictedArgs)
    val result = analyzer.evaluate("test", dataWithHistory)
    result.score shouldBe 1.0
  }

  it should "return 0.0 when penultimate state does not match" in {
    val analyzer = new DefaultAnalyzer(s"""prevTravStateIs("$stateC")""", restrictedArgs)
    val result = analyzer.evaluate("test", dataWithHistory)
    result.score shouldBe 0.0
  }

  it should "return 0.0 when history has only one state" in {
    val singleStateData = AnalyzersData(
      stateData = StateVariables(
        traversedStates = Vector(DtHistoryItem(state = stateA, `type` = DtHistoryType.EXTERNAL))
      )
    )
    val analyzer = new DefaultAnalyzer(s"""prevTravStateIs("$stateA")""", restrictedArgs)
    val result = analyzer.evaluate("test", singleStateData)
    result.score shouldBe 0.0
  }

  it should "return 0.0 when history is empty" in {
    val analyzer = new DefaultAnalyzer(s"""prevTravStateIs("$stateA")""", restrictedArgs)
    val result = analyzer.evaluate("test", dataEmpty)
    result.score shouldBe 0.0
  }

  // --- HasTravStateInPositionAtomic ---

  "hasTravStateInPosition" should "return 1.0 when state matches at position 1 (first)" in {
    val analyzer = new DefaultAnalyzer(s"""hasTravStateInPosition("$stateA", "1")""", restrictedArgs)
    val result = analyzer.evaluate("test", dataWithHistory)
    result.score shouldBe 1.0
  }

  it should "return 1.0 when state matches at position 2 (second)" in {
    val analyzer = new DefaultAnalyzer(s"""hasTravStateInPosition("$stateB", "2")""", restrictedArgs)
    val result = analyzer.evaluate("test", dataWithHistory)
    result.score shouldBe 1.0
  }

  it should "return 1.0 when state matches at position 3 (third)" in {
    val analyzer = new DefaultAnalyzer(s"""hasTravStateInPosition("$stateC", "3")""", restrictedArgs)
    val result = analyzer.evaluate("test", dataWithHistory)
    result.score shouldBe 1.0
  }

  it should "return 0.0 when state does not match at position" in {
    val analyzer = new DefaultAnalyzer(s"""hasTravStateInPosition("$stateA", "2")""", restrictedArgs)
    val result = analyzer.evaluate("test", dataWithHistory)
    result.score shouldBe 0.0
  }

  it should "return 0.0 when position is out of bounds" in {
    val analyzer = new DefaultAnalyzer(s"""hasTravStateInPosition("$stateA", "10")""", restrictedArgs)
    val result = analyzer.evaluate("test", dataWithHistory)
    result.score shouldBe 0.0
  }

  it should "return 0.0 when history is empty" in {
    val analyzer = new DefaultAnalyzer(s"""hasTravStateInPosition("$stateA", "1")""", restrictedArgs)
    val result = analyzer.evaluate("test", dataEmpty)
    result.score shouldBe 0.0
  }

  // --- HasTravStateInPositionRevAtomic ---

  "hasTravStateInPositionRev" should "return 1.0 when last state matches at reverse position 0" in {
    val analyzer = new DefaultAnalyzer(s"""hasTravStateInPositionRev("$stateC", "0")""", restrictedArgs)
    val result = analyzer.evaluate("test", dataWithHistory)
    result.score shouldBe 1.0
  }

  it should "return 1.0 when second-to-last state matches at reverse position 1" in {
    val analyzer = new DefaultAnalyzer(s"""hasTravStateInPositionRev("$stateB", "1")""", restrictedArgs)
    val result = analyzer.evaluate("test", dataWithHistory)
    result.score shouldBe 1.0
  }

  it should "return 1.0 when first state matches at reverse position 2" in {
    val analyzer = new DefaultAnalyzer(s"""hasTravStateInPositionRev("$stateA", "2")""", restrictedArgs)
    val result = analyzer.evaluate("test", dataWithHistory)
    result.score shouldBe 1.0
  }

  it should "return 0.0 when state does not match at reverse position" in {
    val analyzer = new DefaultAnalyzer(s"""hasTravStateInPositionRev("$stateA", "0")""", restrictedArgs)
    val result = analyzer.evaluate("test", dataWithHistory)
    result.score shouldBe 0.0
  }

  it should "return 0.0 when reverse position is out of bounds" in {
    val analyzer = new DefaultAnalyzer(s"""hasTravStateInPositionRev("$stateA", "10")""", restrictedArgs)
    val result = analyzer.evaluate("test", dataWithHistory)
    result.score shouldBe 0.0
  }

  it should "return 0.0 when history is empty" in {
    val analyzer = new DefaultAnalyzer(s"""hasTravStateInPositionRev("$stateA", "0")""", restrictedArgs)
    val result = analyzer.evaluate("test", dataEmpty)
    result.score shouldBe 0.0
  }

  // --- CountStateInHistoryAtomic ---

  "countStateInHistory" should "count occurrences of a state in history" in {
    val dataRepeated = AnalyzersData(
      stateData = StateVariables(
        traversedStates = Vector(
          DtHistoryItem(state = stateA, `type` = DtHistoryType.EXTERNAL),
          DtHistoryItem(state = stateB, `type` = DtHistoryType.EXTERNAL),
          DtHistoryItem(state = stateA, `type` = DtHistoryType.EXTERNAL),
          DtHistoryItem(state = stateC, `type` = DtHistoryType.EXTERNAL),
          DtHistoryItem(state = stateA, `type` = DtHistoryType.EXTERNAL)
        )
      )
    )
    val analyzer = new DefaultAnalyzer(s"""countStateInHistory("state", "$stateA")""", restrictedArgs)
    val result = analyzer.evaluate("test", dataRepeated)
    result.score shouldBe 3.0
  }

  it should "return 0 when state is not found in history" in {
    val analyzer = new DefaultAnalyzer("""countStateInHistory("state", "nonexistent")""", restrictedArgs)
    val result = analyzer.evaluate("test", dataWithHistory)
    result.score shouldBe 0.0
  }

  it should "apply floor value" in {
    val analyzer = new DefaultAnalyzer(
      s"""countStateInHistory("state", "$stateA", "floor", "10")""",
      restrictedArgs
    )
    val result = analyzer.evaluate("test", dataWithHistory)
    result.score shouldBe 11.0
  }

  it should "limit count with maxContext" in {
    val dataRepeated = AnalyzersData(
      stateData = StateVariables(
        traversedStates = Vector(
          DtHistoryItem(state = stateA, `type` = DtHistoryType.EXTERNAL),
          DtHistoryItem(state = stateA, `type` = DtHistoryType.EXTERNAL),
          DtHistoryItem(state = stateA, `type` = DtHistoryType.EXTERNAL),
          DtHistoryItem(state = stateB, `type` = DtHistoryType.EXTERNAL),
          DtHistoryItem(state = stateA, `type` = DtHistoryType.EXTERNAL)
        )
      )
    )
    val analyzer = new DefaultAnalyzer(
      s"""countStateInHistory("state", "$stateA", "maxContext", "2")""",
      restrictedArgs
    )
    val result = analyzer.evaluate("test", dataRepeated)
    result.score shouldBe 1.0
  }

  it should "return 0 when history is empty" in {
    val analyzer = new DefaultAnalyzer(s"""countStateInHistory("state", "$stateA")""", restrictedArgs)
    val result = analyzer.evaluate("test", dataEmpty)
    result.score shouldBe 0.0
  }
}
