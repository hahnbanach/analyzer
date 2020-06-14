package com.getjenny.analyzer.analyzers

/**
  * Created by Angelo Leto <angelo@getjenny.com> on 03/03/17.
  */

import com.getjenny.analyzer.expressions.AnalyzersDataInternal
import org.scalatest._

class NumericalComparisonAtomicTest extends FlatSpec with Matchers {

  val restrictedArgs = Map.empty[String, String]
  "comparisonAtom" should "return 0.0 testing if 1.0 >= 2.0" in {
    val data = AnalyzersDataInternal(extractedVariables = Map.empty[String, String])
    val analyzer = new DefaultAnalyzer("""gte(toDouble("1.0"), toDouble("2.0"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (0.0)
  }
  it should "return 1.0 testing if 2.0 >= 2.0" in {
    val data = AnalyzersDataInternal(extractedVariables = Map.empty[String, String])
    val analyzer = new DefaultAnalyzer("""gte(toDouble("2.0"), toDouble("2.0"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (1.0)
  }
  it should "return 1.0 testing if 1.0 < 2.0" in {
    val data = AnalyzersDataInternal(extractedVariables = Map.empty[String, String])
    val analyzer = new DefaultAnalyzer("""lt(toDouble("1.0"), toDouble("2.0"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (1.0)
  }
  it should "return 1.0 testing if 2.0 <= 2.0" in {
    val data = AnalyzersDataInternal(extractedVariables = Map.empty[String, String])
    val analyzer = new DefaultAnalyzer("""lte(toDouble("2.0"), toDouble("2.0"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (1.0)
  }
  it should "return 0.0 testing if 2.0 < 2.0" in {
    val data = AnalyzersDataInternal(extractedVariables = Map.empty[String, String])
    val analyzer = new DefaultAnalyzer("""lt(toDouble("2.0"), toDouble("2.0"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (0.0)
  }
  it should "return 1.0 testing if 2.0 == 2.0" in {
    val data = AnalyzersDataInternal(extractedVariables = Map.empty[String, String])
    val analyzer = new DefaultAnalyzer("""eq(toDouble("2.0"), toDouble("2.0"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (1.0)
  }
  it should "return 0.0 testing if 2.0 > 3.0" in {
    val data = AnalyzersDataInternal(extractedVariables = Map.empty[String, String])
    val analyzer = new DefaultAnalyzer("""gt(toDouble("2.0"), toDouble("3.0"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (0.0)
  }
  it should "return 1.0 testing if 3.0 > 2.0" in {
    val data = AnalyzersDataInternal(extractedVariables = Map.empty[String, String])
    val analyzer = new DefaultAnalyzer("""gt(toDouble("3.0"), toDouble("2.0"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (1.0)
  }
}
