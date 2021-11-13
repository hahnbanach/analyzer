package com.hahnbanach.analyzer.analyzers

/**
  * Created by Angelo Leto <angelo.leto@hahnbanach.com> on 12/02/19.
  */

import com.hahnbanach.analyzer.entities.{AnalyzersDataInternal, StateVariables}
import com.hahnbanach.analyzer.util.Time
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class IfThenElseOperatorTest extends AnyFlatSpec with Matchers {
  val restrictedArgs = Map.empty[String, String]
  "ifThenElse" should "return 2.0 if the test return 1.0" in {
    val data = AnalyzersDataInternal(stateData = StateVariables())
    val analyzer = new DefaultAnalyzer(
    """ifThenElse(toDouble("1.0"), toDouble("2.0"), toDouble("3.0"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (2.0)
  }
  it should "return 3.0 testing if test success" in {
    val data = AnalyzersDataInternal(stateData = StateVariables())
    val analyzer = new DefaultAnalyzer(
      """ifThenElse(toDouble("0.0"), toDouble("2.0"), toDouble("3.0"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (3.0)
  }

  it should "return 100.0 if VARIABLE is equal to hello" in {
    val data = AnalyzersDataInternal(
      stateData = StateVariables(
        variables = Map[String, String]("VARIABLE" -> "hello")
      )
    )
    val analyzer = new DefaultAnalyzer("""ifThenElse(checkVariableValue("VARIABLE", "hello"), toDouble("100"), toDouble("200"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (100.0)
  }
  it should "return 100.0 if VARIABLE is not equal to hello" in {
    val data = AnalyzersDataInternal(
      stateData = StateVariables(
        variables = Map[String, String]("VARIABLE" -> "ciao")
      )
    )
    val analyzer = new DefaultAnalyzer("""ifThenElse(checkVariableValue("VARIABLE", "hello"), toDouble("100"), toDouble("200"))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (200.0)
  }
}

