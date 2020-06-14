package com.getjenny.analyzer.analyzers

/**
  * Created by Angelo Leto <angelo@getjenny.com> on 12/02/19.
  */

import com.getjenny.analyzer.expressions.AnalyzersDataInternal
import com.getjenny.analyzer.util.Time
import org.scalatest._

class CheckTimestampAtomicTest extends FlatSpec with Matchers {

  val restrictedArgs = Map.empty[String, String]
  "checkTimestampVariableAtom" should "return 1.0 testing if current timestamp > 1549990000" in {
    val data = AnalyzersDataInternal(extractedVariables = Map[String, String]("TIMESTAMP" -> "1549990000"))
    val analyzer = new DefaultAnalyzer("""checkTimestampVariable("TIMESTAMP", "Greater")""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (1.0)
  }
  it should "return 0.0 testing if current timestamp > 9999999999" in {
    val data = AnalyzersDataInternal(extractedVariables = Map[String, String]("TIMESTAMP" -> "9999999999"))
    val analyzer = new DefaultAnalyzer("""checkTimestampVariable("TIMESTAMP", "Greater")""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (0.0)
  }
  it should "return 1.0 testing if current timestamp < 9999999999" in {
    val data = AnalyzersDataInternal(extractedVariables = Map[String, String]("TIMESTAMP" -> "9999999999"))
    val analyzer = new DefaultAnalyzer("""checkTimestampVariable("TIMESTAMP", "Less")""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (1.0)
  }
  it should "return 1.0 testing if current timestamp >= currentTimeStamp- 10" in {
    val data = AnalyzersDataInternal()
    val currTimestampMinus10s: Long = Time.timestampEpoc - 10;
    val query = """checkTimestamp("""" + currTimestampMinus10s.toString() + """","GreaterOrEqual")""";
    val analyzer = new DefaultAnalyzer(query, restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (1.0)
  }
  it should "return 0.0 testing if current timestamp >= currentTimeStamp + 10" in {
    val data = AnalyzersDataInternal()
    val currTimestampPlus10s: Long = Time.timestampEpoc + 10;
    val query = """checkTimestamp("""" + currTimestampPlus10s.toString() + """","GreaterOrEqual")""";
    val analyzer = new DefaultAnalyzer(query, restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (0.0)
  }
}

