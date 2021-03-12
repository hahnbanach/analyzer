package io.elegans.analyzer.analyzers

/**
  * Created by Angelo Leto <angelo.leto@elegans.io> on 03/03/17.
  */

import io.elegans.analyzer.entities.AnalyzersDataInternal
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class NumericalComparisonAtomicTest extends AnyFlatSpec with Matchers {
  val restrictedArgs = Map.empty[String, String]
  "comparisonAtom" should "return 0.0 testing if 1.0 >= 2.0" in {
    val data = AnalyzersDataInternal()
    val analyzer = new DefaultAnalyzer("""gte(toDouble("1.0"), toDouble("2.0"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (0.0)
  }
  it should "return 1.0 testing if 2.0 >= 2.0" in {
    val data = AnalyzersDataInternal()
    val analyzer = new DefaultAnalyzer("""gte(toDouble("2.0"), toDouble("2.0"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (1.0)
  }
  it should "return 1.0 testing if 1.0 < 2.0" in {
    val data = AnalyzersDataInternal()
    val analyzer = new DefaultAnalyzer("""lt(toDouble("1.0"), toDouble("2.0"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (1.0)
  }
  it should "return 1.0 testing if 2.0 <= 2.0" in {
    val data = AnalyzersDataInternal()
    val analyzer = new DefaultAnalyzer("""lte(toDouble("2.0"), toDouble("2.0"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (1.0)
  }
  it should "return 0.0 testing if 2.0 < 2.0" in {
    val data = AnalyzersDataInternal()
    val analyzer = new DefaultAnalyzer("""lt(toDouble("2.0"), toDouble("2.0"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (0.0)
  }
  it should "return 1.0 testing if 2.0 == 2.0" in {
    val data = AnalyzersDataInternal()
    val analyzer = new DefaultAnalyzer("""eq(toDouble("2.0"), toDouble("2.0"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (1.0)
  }
  it should "return 0.0 testing if 2.0 > 3.0" in {
    val data = AnalyzersDataInternal()
    val analyzer = new DefaultAnalyzer("""gt(toDouble("2.0"), toDouble("3.0"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (0.0)
  }
  it should "return 1.0 testing if 3.0 > 2.0" in {
    val data = AnalyzersDataInternal()
    val analyzer = new DefaultAnalyzer("""gt(toDouble("3.0"), toDouble("2.0"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (1.0)
  }
}
