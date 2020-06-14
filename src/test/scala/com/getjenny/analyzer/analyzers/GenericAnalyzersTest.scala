package com.getjenny.analyzer.analyzers

/**
  * Created by Angelo Leto <angelo@getjenny.com> on 03/03/17.
  */


import org.scalatest._

class GenericAnalyzersTest extends FlatSpec with Matchers {

  val restrictedArgs = Map.empty[String, String]
  "A DefaultAnalyzer" should "parse a rule and evaluate the operations on a provided input text" in {
    val analyzerBayes = new DefaultAnalyzer("""disjunction( keyword("clever"), keyword("gentleman") )""", restrictedArgs)

    val gentleman = analyzerBayes.evaluate("ciao nice gentleman fool")
    val cleverGentlemanLong = analyzerBayes.evaluate("ciao clever fool gentleman")
    val cleverGentlemanShort = analyzerBayes.evaluate("ciao clever gentleman")

    //two is better than one
    cleverGentlemanLong.score should be > gentleman.score
    cleverGentlemanShort.score should be > gentleman.score

    //finding in short is better than finding in longer
    cleverGentlemanShort.score should be > cleverGentlemanLong.score
  }
  it should "throw a AnalyzerParsingException if parenthesis are not balanced" in {
    a[AnalyzerParsingException] should be thrownBy {
      new DefaultAnalyzer("""disjunction( keyword("clever")), keyword("gentleman") )""", restrictedArgs)
    }
  }
  it should "throw a AnalyzerCommandException if the command does not exists or is mispelled" in {
    a[AnalyzerCommandException] should be thrownBy {
      new DefaultAnalyzer("""fakeDisjunction( keyword("clever"), keyword("gentleman") )""", restrictedArgs)
    }
  }

  it should "be able to extract the first occurrence of an expression at level 0" in {
    val a1 = new DefaultAnalyzer("""disjunction(band(keyword("clever"), bor(keyword("gentleman"),keyword("gentleman2"))) )""", restrictedArgs)
    a1.firstOccurrenceOfOperator("DisjunctionOperator").get.getClass.getSimpleName should equal("""DisjunctionOperator""")
  }

  it should "be able to extract the first occurrence of an expression at deeper level and evaluate it" in {
    val a2 = new DefaultAnalyzer("""band(keyword("clever"), bor(keyword("gentleman"),keyword("gentleman2")))""", restrictedArgs)
    val firstOccurrenceOfBooleanOrOperator = a2.firstOccurrenceOfOperator("BooleanOrOperator").get
    firstOccurrenceOfBooleanOrOperator.getClass.getSimpleName should equal("""BooleanOrOperator""")

    firstOccurrenceOfBooleanOrOperator.evaluate("gentleman2").score shouldEqual (1.0)
    firstOccurrenceOfBooleanOrOperator.evaluate("Hihihihi").score shouldEqual (0.0)
  }

  it should "be able return None when looking for a first occurrence of an expression that is not present in the analyzer" in {
    val a2 = new DefaultAnalyzer("""band(keyword("clever"), bor(keyword("gentleman"),keyword("gentleman2")))""", restrictedArgs)
    val firstOccurrenceOfFakeOperator = a2.firstOccurrenceOfOperator("FakeOperator")
    firstOccurrenceOfFakeOperator.shouldEqual(None)
  }

}
