package com.getjenny.analyzer.analyzers

/**
  * Created by Angelo Leto <angelo@getjenny.com> on 14/02/19.
  */

import com.getjenny.analyzer.expressions.AnalyzersDataInternal
import org.scalatest._
import com.getjenny.analyzer.atoms.ExceptionAtomic

class MultiStateOpeningTimeTest extends FlatSpec with Matchers {

  val data = AnalyzersDataInternal(extractedVariables = Map[String, String]("GJ_SERVICEOPEN_SERVICE1" ->
    """{
      |  "openTime": "00:00",
      |  "closeTime": "23:59",
      |  "timezone": "CET",
      |  "months": [1,2,3,4,5,6,7,8,9,10,11,12],
      |  "days": [],
      |  "weekDays": [1,2,3,4,5,6,7]
      |}""".stripMargin
  ))

  val restrictedArgs = Map.empty[String, String]
  "setServiceOpening" should "create and check the service opening variable for the service" in {
    val analyzerServiceOpening = new DefaultAnalyzer("""setServiceOpening()""", restrictedArgs)
    val analyzerServiceOpeningValue = analyzerServiceOpening.evaluate("test query", data)
    analyzerServiceOpeningValue.score should be (0.0)

    val analyzerIsOpen = new DefaultAnalyzer("""isServiceOpen("SERVICE1")""", restrictedArgs)
    val analyzerIsOpenValue = analyzerIsOpen.evaluate("test query", analyzerServiceOpeningValue.data)
    analyzerIsOpenValue.score should be (1.0)
  }
}
