package com.hahnbanach.analyzer.analyzers

/**
  * Created by Angelo Leto <angelo.leto@hahnbanach.com> on 03/03/17.
  */

import com.hahnbanach.analyzer.atoms.ExceptionAtomic
import com.hahnbanach.analyzer.entities.{AnalyzersData, StateVariables}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DoubleNumberVariableAtomicTest extends AnyFlatSpec with Matchers {

  val restrictedArgs = Map.empty[String, String]
  "doubleNumberVariable" should "return the numerical value of a variable" in {
    val data = AnalyzersData(
      stateData = StateVariables(
        variables = Map[String, String]("TEST_VARIABLE" -> "100.0")
      )
    )
    val analyzer = new DefaultAnalyzer("""doubleNumberVariable("TEST_VARIABLE", "1.0")""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (100.0)
  }
  it should "return the numerical value of a variable, even without default value" in {
    val data = AnalyzersData(
      stateData = StateVariables(
        variables = Map[String, String]("TEST_VARIABLE" -> "100.0")
      )
    )
    val analyzer = new DefaultAnalyzer("""doubleNumberVariable("TEST_VARIABLE")""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (100.0)
  }
  it should "return the default value if the variable does not exists" in {
    val data = AnalyzersData(
      stateData = StateVariables(
        variables = Map[String, String]("TEST_VARIABLE" -> "100.0")
      )
    )
    val analyzer = new DefaultAnalyzer("""doubleNumberVariable("TEST_VARIABLEABCDE", "2000.0")""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (2000.0)
  }
  it should "throw a ExceptionAtomic if the variable does not exists and no default value was passed" in {
    a [ExceptionAtomic] should be thrownBy {
      val data = AnalyzersData(
        stateData = StateVariables(
          variables = Map[String, String]("TEST_VARIABLE" -> "100.0")
        )
      )
      val analyzer = new DefaultAnalyzer("""doubleNumberVariable("TEST_VARIABLEAAAA")""", restrictedArgs)
      analyzer.evaluate("test query", data)
    }
  }
  it should "throw a ExceptionAtomic if the variable does not contains a numerical value" in {
    a [ExceptionAtomic] should be thrownBy {
      val data = AnalyzersData(
        stateData = StateVariables(
          variables = Map[String, String]("TEST_VARIABLE" -> "hello")
        )
      )
      val analyzer = new DefaultAnalyzer("""doubleNumberVariable("TEST_VARIABLE", "100.0")""", restrictedArgs)
      analyzer.evaluate("test query", data)
    }
  }
  it should "throw a ExceptionAtomic if the variable does not contains a numerical value, even without default value" in {
    a [ExceptionAtomic] should be thrownBy {
      val data = AnalyzersData(
        stateData = StateVariables(
          variables = Map[String, String]("TEST_VARIABLE" -> "hello")
        )
      )
      val analyzer = new DefaultAnalyzer("""doubleNumberVariable("TEST_VARIABLE")""", restrictedArgs)
      analyzer.evaluate("test query", data)
    }
  }
}
