package io.elegans.analyzer.analyzers

/**
  * Created by Angelo Leto <angelo.leto@elegans.io> on 03/03/17.
  */

import io.elegans.analyzer.entities.{AnalyzersDataInternal, StateVariables}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ComplexAnalyzersExpressionsTests extends AnyFlatSpec with Matchers {

  val restrictedArgs = Map.empty[String, String]
  "ComplexAnalyzersExpressionsTests" should "return true if the current hour is between opening and closing hour" in {
    val data = AnalyzersDataInternal(
      stateData = StateVariables(
        variables = Map[String, String]("OPEN_HOUR" -> "8", "CLOSE_HOUR" -> "18", "CURRENT_HOUR" -> "17")
      )
    )
    val analyzer = new DefaultAnalyzer("""band(gte(doubleNumberVariable("CURRENT_HOUR"),doubleNumberVariable("OPEN_HOUR")), lte(doubleNumberVariable("CURRENT_HOUR"), doubleNumberVariable("CLOSE_HOUR")))""", restrictedArgs)
    val analyzerValue = analyzer.evaluate("test query", data)
    analyzerValue.score should be (1.0)
  }
}
