package com.getjenny.analyzer.analyzers

/**
  * Created by Angelo Leto <angelo@getjenny.com> on 03/03/17.
  */

import com.getjenny.analyzer.expressions.AnalyzersDataInternal
import org.scalatest._
import com.getjenny.analyzer.atoms.ExceptionAtomic

class DoubleNumberVariableAtomicTest extends FlatSpec with Matchers {

  val restrictedArgs = Map.empty[String, String]
  "doubleNumberVariable" should "return the numerical value of a variable" in {
    val data = AnalyzersDataInternal(extractedVariables = Map[String, String]("TEST_VARIABLE" -> "100.0"))
    val analyzer = new DefaultAnalyzer("""doubleNumberVariable("TEST_VARIABLE", "1.0")""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (100.0)
  }
  it should "return the numerical value of a variable, even without default value" in {
    val data = AnalyzersDataInternal(extractedVariables = Map[String, String]("TEST_VARIABLE" -> "100.0"))
    val analyzer = new DefaultAnalyzer("""doubleNumberVariable("TEST_VARIABLE")""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (100.0)
  }
  it should "return the default value if the variable does not exists" in {
    val data = AnalyzersDataInternal(extractedVariables = Map[String, String]("TEST_VARIABLE" -> "100.0"))
    val analyzer = new DefaultAnalyzer("""doubleNumberVariable("TEST_VARIABLEABCDE", "2000.0")""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (2000.0)
  }
  it should "throw a ExceptionAtomic if the variable does not exists and no default value was passed" in {
    a [ExceptionAtomic] should be thrownBy {
      val data = AnalyzersDataInternal(extractedVariables = Map[String, String]("TEST_VARIABLE" -> "100.0"))
      val analyzer = new DefaultAnalyzer("""doubleNumberVariable("TEST_VARIABLEAAAA")""", restrictedArgs)
      analyzer.evaluate("test query", data)
    }
  }
  it should "throw a ExceptionAtomic if the variable does not contains a numerical value" in {
    a [ExceptionAtomic] should be thrownBy {
      val data = AnalyzersDataInternal(extractedVariables = Map[String, String]("TEST_VARIABLE" -> "hello"))
      val analyzer = new DefaultAnalyzer("""doubleNumberVariable("TEST_VARIABLE", "100.0")""", restrictedArgs)
      analyzer.evaluate("test query", data)
    }
  }
  it should "throw a ExceptionAtomic if the variable does not contains a numerical value, even without default value" in {
    a [ExceptionAtomic] should be thrownBy {
      val data = AnalyzersDataInternal(extractedVariables = Map[String, String]("TEST_VARIABLE" -> "hello"))
      val analyzer = new DefaultAnalyzer("""doubleNumberVariable("TEST_VARIABLE")""", restrictedArgs)
      analyzer.evaluate("test query", data)
    }
  }
}
