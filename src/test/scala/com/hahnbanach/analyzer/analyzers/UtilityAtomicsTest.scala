package com.hahnbanach.analyzer.analyzers

import com.hahnbanach.analyzer.atoms._
import com.hahnbanach.analyzer.entities.{AnalyzersData, StateVariables}
import com.hahnbanach.analyzer.util.{ComparisonOperators, Time, VectorUtils}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class UtilityAtomicsTest extends AnyFlatSpec with Matchers {

  val restrictedArgs: Map[String, String] = Map.empty[String, String]

  // --- ToDoubleNumberAtomic ---

  "toDouble" should "return the double value as score" in {
    val analyzer = new DefaultAnalyzer("""toDouble("3.14")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 3.14
  }

  it should "return negative values" in {
    val analyzer = new DefaultAnalyzer("""toDouble("-2.5")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe -2.5
  }

  it should "return zero" in {
    val analyzer = new DefaultAnalyzer("""toDouble("0")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 0.0
  }

  it should "return integer values as doubles" in {
    val analyzer = new DefaultAnalyzer("""toDouble("42")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 42.0
  }

  it should "throw exception when no arguments provided" in {
    a [Exception] should be thrownBy {
      new ToDoubleNumberAtomic(List.empty, restrictedArgs)
    }
  }

  // --- ParametersAtomic ---

  "parameters" should "return count of arguments as score" in {
    val analyzer = new DefaultAnalyzer("""parameters("a", "b", "c")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 3.0
  }

  it should "return 1.0 for single argument" in {
    val analyzer = new DefaultAnalyzer("""parameters("single")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 1.0
  }

  // --- RegularExpressionAtomic ---

  "regex" should "count occurrences of pattern in query" in {
    val analyzer = new DefaultAnalyzer("""regex("[0-9]+")""", restrictedArgs)
    val result = analyzer.evaluate("I have 3 cats and 5 dogs")
    result.score shouldBe 2.0
  }

  it should "return 0 when pattern does not match" in {
    val analyzer = new DefaultAnalyzer("""regex("[0-9]+")""", restrictedArgs)
    val result = analyzer.evaluate("no numbers here")
    result.score shouldBe 0.0
  }

  it should "count single occurrence" in {
    val analyzer = new DefaultAnalyzer("""regex("hello")""", restrictedArgs)
    val result = analyzer.evaluate("hello world")
    result.score shouldBe 1.0
  }

  it should "count multiple occurrences" in {
    val analyzer = new DefaultAnalyzer("""regex("ab")""", restrictedArgs)
    val result = analyzer.evaluate("ab cd ab ef ab")
    result.score shouldBe 3.0
  }

  it should "throw exception when no arguments provided" in {
    a [Exception] should be thrownBy {
      new RegularExpressionAtomic(List.empty, restrictedArgs)
    }
  }

  // --- ChecksumAtomic ---

  "checksum" should "calculate md5 checksum" in {
    val data = AnalyzersData(
      stateData = StateVariables(variables = Map("myInput" -> "hello"))
    )
    val analyzer = new DefaultAnalyzer(
      """checksum("input", "hello", "checksum", "md5", "outName", "RESULT", "outPattern", "local.")""",
      restrictedArgs
    )
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 1.0
    result.data.stateData.variables.contains("local.RESULT") shouldBe true
    result.data.stateData.variables("local.RESULT").length should be > 0
  }

  it should "calculate sha256 checksum" in {
    val analyzer = new DefaultAnalyzer(
      """checksum("input", "test", "checksum", "sha256", "outName", "HASH", "outPattern", "out.")""",
      restrictedArgs
    )
    val result = analyzer.evaluate("test")
    result.score shouldBe 1.0
    result.data.stateData.variables.contains("out.HASH") shouldBe true
    result.data.stateData.variables("out.HASH").length should be > 0
  }

  it should "calculate sha1 checksum" in {
    val analyzer = new DefaultAnalyzer(
      """checksum("input", "data", "checksum", "sha1", "outName", "RESULT", "outPattern", "local.")""",
      restrictedArgs
    )
    val result = analyzer.evaluate("test")
    result.score shouldBe 1.0
    result.data.stateData.variables.contains("local.RESULT") shouldBe true
  }

  it should "calculate sha512 checksum" in {
    val analyzer = new DefaultAnalyzer(
      """checksum("input", "data", "checksum", "sha512", "outName", "RESULT", "outPattern", "local.")""",
      restrictedArgs
    )
    val result = analyzer.evaluate("test")
    result.score shouldBe 1.0
    result.data.stateData.variables.contains("local.RESULT") shouldBe true
  }

  it should "calculate crc32 checksum" in {
    val analyzer = new DefaultAnalyzer(
      """checksum("input", "data", "checksum", "crc32", "outName", "RESULT", "outPattern", "local.")""",
      restrictedArgs
    )
    val result = analyzer.evaluate("test")
    result.score shouldBe 1.0
    result.data.stateData.variables.contains("local.RESULT") shouldBe true
  }

  it should "generate uuid" in {
    val analyzer = new DefaultAnalyzer(
      """checksum("input", "data", "checksum", "uuid", "outName", "RESULT", "outPattern", "local.")""",
      restrictedArgs
    )
    val result = analyzer.evaluate("test")
    result.score shouldBe 1.0
    result.data.stateData.variables.contains("local.RESULT") shouldBe true
    result.data.stateData.variables("local.RESULT") should include("-")
  }

  it should "produce same md5 for same input" in {
    val analyzer = new DefaultAnalyzer(
      """checksum("input", "deterministic", "checksum", "md5", "outName", "RESULT", "outPattern", "local.")""",
      restrictedArgs
    )
    val result1 = analyzer.evaluate("test")
    val result2 = analyzer.evaluate("test")
    result1.data.stateData.variables("local.RESULT") shouldBe result2.data.stateData.variables("local.RESULT")
  }

  // --- CosineDistanceAtomic ---

  "cosDistanceKeywords" should "return high score for matching keywords" in {
    val analyzer = new DefaultAnalyzer("""cosDistanceKeywords("hello", "world")""", restrictedArgs)
    val result = analyzer.evaluate("hello world")
    result.score should be > 0.0
    result.score should be <= 1.0
  }

  it should "return lower score for non-matching keywords" in {
    val analyzer = new DefaultAnalyzer("""cosDistanceKeywords("hello", "world")""", restrictedArgs)
    val result = analyzer.evaluate("foo bar baz")
    result.score should be < 1.0
  }

  // --- ComparisonOperators utility ---

  "ComparisonOperators" should "compare Long values with LessOrEqual" in {
    ComparisonOperators.compare(5L, 10L, "LessOrEqual") shouldBe true
    ComparisonOperators.compare(10L, 10L, "LessOrEqual") shouldBe true
    ComparisonOperators.compare(11L, 10L, "LessOrEqual") shouldBe false
  }

  it should "compare Long values with Less" in {
    ComparisonOperators.compare(5L, 10L, "Less") shouldBe true
    ComparisonOperators.compare(10L, 10L, "Less") shouldBe false
  }

  it should "compare Long values with Greater" in {
    ComparisonOperators.compare(15L, 10L, "Greater") shouldBe true
    ComparisonOperators.compare(10L, 10L, "Greater") shouldBe false
  }

  it should "compare Long values with GreaterOrEqual" in {
    ComparisonOperators.compare(15L, 10L, "GreaterOrEqual") shouldBe true
    ComparisonOperators.compare(10L, 10L, "GreaterOrEqual") shouldBe true
    ComparisonOperators.compare(5L, 10L, "GreaterOrEqual") shouldBe false
  }

  it should "compare Long values with Equal" in {
    ComparisonOperators.compare(10L, 10L, "Equal") shouldBe true
    ComparisonOperators.compare(5L, 10L, "Equal") shouldBe false
  }

  it should "throw exception for invalid operator on Long" in {
    a [ExceptionAtomic] should be thrownBy {
      ComparisonOperators.compare(10L, 10L, "InvalidOp")
    }
  }

  it should "compare Double values with all operators" in {
    ComparisonOperators.compare(1.0, 2.0, "Less") shouldBe true
    ComparisonOperators.compare(2.0, 1.0, "Greater") shouldBe true
    ComparisonOperators.compare(1.0, 1.0, "Equal") shouldBe true
    ComparisonOperators.compare(1.0, 2.0, "LessOrEqual") shouldBe true
    ComparisonOperators.compare(2.0, 1.0, "GreaterOrEqual") shouldBe true
  }

  it should "throw exception for invalid operator on Double" in {
    a [ExceptionAtomic] should be thrownBy {
      ComparisonOperators.compare(1.0, 1.0, "InvalidOp")
    }
  }

  it should "compare Int values with all operators" in {
    ComparisonOperators.compare(1, 2, "Less") shouldBe true
    ComparisonOperators.compare(2, 1, "Greater") shouldBe true
    ComparisonOperators.compare(1, 1, "Equal") shouldBe true
    ComparisonOperators.compare(1, 2, "LessOrEqual") shouldBe true
    ComparisonOperators.compare(2, 1, "GreaterOrEqual") shouldBe true
  }

  it should "throw exception for invalid operator on Int" in {
    a [ExceptionAtomic] should be thrownBy {
      ComparisonOperators.compare(1, 1, "InvalidOp")
    }
  }

  // --- Time utility ---

  "Time" should "return a valid timestamp in seconds" in {
    val ts = Time.timestampEpoc
    ts should be > 0L
  }

  it should "return a valid timestamp in milliseconds" in {
    val ts = Time.timestampMillis
    ts should be > 0L
    ts should be > Time.timestampEpoc
  }

  it should "return hour between 0 and 23" in {
    val h = Time.hour("UTC")
    h should be >= 0
    h should be <= 23
  }

  it should "return minutes between 0 and 59" in {
    val m = Time.minutes("UTC")
    m should be >= 0
    m should be <= 59
  }

  it should "return day of week between 1 and 7" in {
    val d = Time.dayOfWeekInt("UTC")
    d should be >= 1
    d should be <= 7
  }

  it should "return day of month between 1 and 31" in {
    val d = Time.dayOfMonth("UTC")
    d should be >= 1
    d should be <= 31
  }

  it should "return month between 1 and 12" in {
    val m = Time.monthInt("UTC")
    m should be >= 1
    m should be <= 12
  }

  it should "convert day of week string to int" in {
    Time.dayOfWeekToInt("MONDAY") shouldBe 1
    Time.dayOfWeekToInt("TUESDAY") shouldBe 2
    Time.dayOfWeekToInt("WEDNESDAY") shouldBe 3
    Time.dayOfWeekToInt("THURSDAY") shouldBe 4
    Time.dayOfWeekToInt("FRIDAY") shouldBe 5
    Time.dayOfWeekToInt("SATURDAY") shouldBe 6
    Time.dayOfWeekToInt("SUNDAY") shouldBe 7
    Time.dayOfWeekToInt("INVALID") shouldBe 0
  }

  it should "return valid day of week string" in {
    val validDays = Set("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY")
    validDays should contain(Time.dayOfWeekString("UTC"))
  }

  it should "return valid month string" in {
    val validMonths = Set("JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
      "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER")
    validMonths should contain(Time.monthString("UTC"))
  }

  // --- VectorUtils ---

  "VectorUtils" should "calculate cosine distance between identical vectors" in {
    val v = Vector(1.0, 2.0, 3.0)
    VectorUtils.cosineDist(v, v) shouldBe 0.0 +- 0.0001
  }

  it should "calculate cosine distance between orthogonal vectors" in {
    val u = Vector(1.0, 0.0)
    val v = Vector(0.0, 1.0)
    VectorUtils.cosineDist(u, v) shouldBe 0.5 +- 0.0001
  }

  it should "calculate cosine distance between opposite vectors" in {
    val u = Vector(1.0, 0.0)
    val v = Vector(-1.0, 0.0)
    VectorUtils.cosineDist(u, v) shouldBe 1.0 +- 0.0001
  }

  it should "throw exception for vectors of different lengths" in {
    val u = Vector(1.0, 2.0)
    val v = Vector(1.0, 2.0, 3.0)
    an [IllegalArgumentException] should be thrownBy {
      VectorUtils.cosineDist(u, v)
    }
  }

  it should "calculate euclidean distance between identical vectors" in {
    val v = Vector(1.0, 2.0, 3.0)
    VectorUtils.euclideanDist(v, v) shouldBe 0.0 +- 0.0001
  }

  it should "calculate euclidean distance correctly" in {
    val u = Vector(0.0, 0.0)
    val v = Vector(3.0, 4.0)
    VectorUtils.euclideanDist(u, v) shouldBe 5.0 +- 0.0001
  }

  it should "throw exception for euclidean distance with different length vectors" in {
    val u = Vector(1.0, 2.0)
    val v = Vector(1.0, 2.0, 3.0)
    an [IllegalArgumentException] should be thrownBy {
      VectorUtils.euclideanDist(u, v)
    }
  }

  it should "calculate mean of array of arrays" in {
    val vectors = Vector(
      Vector(2.0, 4.0),
      Vector(4.0, 6.0)
    )
    val mean = VectorUtils.meanArrayOfArrays(vectors)
    mean(0) shouldBe 3.0 +- 0.0001
    mean(1) shouldBe 5.0 +- 0.0001
  }

  it should "calculate sum of array of arrays" in {
    val vectors = Vector(
      Vector(1.0, 2.0),
      Vector(3.0, 4.0)
    )
    val sum = VectorUtils.sumArrayOfArrays(vectors)
    sum(0) shouldBe 4.0 +- 0.0001
    sum(1) shouldBe 6.0 +- 0.0001
  }

  // --- KeywordAtomic (additional tests) ---

  "keyword" should "return frequency ratio for matching keyword" in {
    val analyzer = new DefaultAnalyzer("""keyword("hello")""", restrictedArgs)
    val result = analyzer.evaluate("hello world")
    result.score shouldBe 0.5
  }

  it should "return 0 for non-matching keyword" in {
    val analyzer = new DefaultAnalyzer("""keyword("missing")""", restrictedArgs)
    val result = analyzer.evaluate("hello world")
    result.score shouldBe 0.0
  }

  it should "not match different case" in {
    val k = new KeywordAtomic(List("hello"), restrictedArgs)
    k.evaluate("Hello there").score shouldBe 0.0
  }

  it should "match exact case" in {
    val k = new KeywordAtomic(List("hello"), restrictedArgs)
    k.evaluate("hello there").score should be > 0.0
  }

  // --- Combined operators with new atomics ---

  "booleanAnd with setVariable" should "set a variable and return it in result" in {
    val data = AnalyzersData()
    val analyzer = new DefaultAnalyzer(
      """booleanAnd(setVariable("testVar", "testVal"))""",
      restrictedArgs
    )
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 1.0
    result.data.stateData.variables("testVar") shouldBe "testVal"
  }

  "ifThenElse with checkVariableValue" should "take then branch when variable matches" in {
    val data = AnalyzersData(
      stateData = StateVariables(variables = Map("status" -> "active"))
    )
    val analyzer = new DefaultAnalyzer(
      """ifThenElse(checkVariableValue("status", "active"), toDouble("1"), toDouble("0"))""",
      restrictedArgs
    )
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 1.0
  }

  it should "take else branch when variable does not match" in {
    val data = AnalyzersData(
      stateData = StateVariables(variables = Map("status" -> "inactive"))
    )
    val analyzer = new DefaultAnalyzer(
      """ifThenElse(checkVariableValue("status", "active"), toDouble("1"), toDouble("0"))""",
      restrictedArgs
    )
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 0.0
  }
}
