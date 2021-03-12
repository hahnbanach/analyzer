package io.elegans.analyzer.atoms

/**
  * Created by Angelo Leto <angelo.leto@elegans.io> on 03/03/17.
  */

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class AtomsTest extends AnyFlatSpec with Matchers {

  val restrictedArgs = Map.empty[String, String]

  "An AtomicKeyword" should "support a floating point or a boolean value" in {
    val k = new KeywordAtomic(List("ciao"), restrictedArgs)
    k.evaluate("ciao, stupid moron").score should be (1.0/3)
    k.matches("ciao stupid moron").score should be (1)
  }

  "A timeBetween atomic" should "always give true if opening and closing time are 00:00 and 23:59" in {
    val k = new TimeBetweenAtomic(List("00:00", "23:59", "CET"), restrictedArgs)
    k.evaluate("").score should be (1.0)
    k.matches("").score should be (1.0)
  }

}