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

}