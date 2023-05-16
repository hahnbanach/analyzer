package com.hahnbanach.analyzer.analyzers

/**
  * Created by Angelo Leto <angelo.leto@hahnbanach.com> on 12/02/19.
  */

import com.hahnbanach.analyzer.entities.{AnalyzersData, StateVariables}
import com.hahnbanach.analyzer.util.Time
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CheckTimestampAtomicTest extends AnyFlatSpec with Matchers {

  val restrictedArgs = Map.empty[String, String]
  "checkTimestampVariableAtom" should "return 1.0 testing if current timestamp > 1549990000" in {
    val data = AnalyzersData(
      stateData = StateVariables(
        variables = Map[String, String]("TIMESTAMP" -> "1549990000")
      )
    )
    val analyzer = new DefaultAnalyzer("""checkTimestampVariable("TIMESTAMP", "Greater")""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (1.0)
  }
  it should "return 0.0 testing if current timestamp > 9999999999" in {
    val data = AnalyzersData(
      stateData = StateVariables(
        variables = Map[String, String]("TIMESTAMP" -> "9999999999")
      )
    )
    val analyzer = new DefaultAnalyzer("""checkTimestampVariable("TIMESTAMP", "Greater")""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (0.0)
  }
  it should "return 1.0 testing if current timestamp < 9999999999" in {
    val data = AnalyzersData(
      stateData = StateVariables(
        variables = Map[String, String]("TIMESTAMP" -> "9999999999")
      )
    )
    val analyzer = new DefaultAnalyzer("""checkTimestampVariable("TIMESTAMP", "Less")""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (1.0)
  }
  it should "return 1.0 testing if current timestamp >= currentTimeStamp- 10" in {
    val data = AnalyzersData()
    val currTimestampMinus10s: Long = Time.timestampEpoc - 10
    val query = """checkTimestamp("""" + currTimestampMinus10s.toString + """","GreaterOrEqual")"""
    val analyzer = new DefaultAnalyzer(query, restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (1.0)
  }
  it should "return 0.0 testing if current timestamp >= currentTimeStamp + 10" in {
    val data = AnalyzersData()
    val currTimestampPlus10s: Long = Time.timestampEpoc + 10
    val query = """checkTimestamp("""" + currTimestampPlus10s.toString + """","GreaterOrEqual")"""
    val analyzer = new DefaultAnalyzer(query, restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (0.0)
  }
  it should "return 1.0 and set a variable with the timestamp" in {
    val data = AnalyzersData()
    val analyzer = new DefaultAnalyzer("""tsNow("outName", "NOW_TS", "outPattern", "test."))""", restrictedArgs)
    val analyzerRes = analyzer.evaluate("test query", data)
    analyzerRes.score should be(1.0)
    analyzerRes.data.stateData.variables.contains("test.NOW_TS")
  }
}

