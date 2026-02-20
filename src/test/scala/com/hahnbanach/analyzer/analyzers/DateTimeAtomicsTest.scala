package com.hahnbanach.analyzer.analyzers

import com.hahnbanach.analyzer.atoms._
import com.hahnbanach.analyzer.entities.AnalyzersData
import com.hahnbanach.analyzer.util.Time
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DateTimeAtomicsTest extends AnyFlatSpec with Matchers {

  val restrictedArgs: Map[String, String] = Map.empty[String, String]

  // --- CheckHourAtomic ---

  "checkHour" should "return 1.0 when current hour satisfies GreaterOrEqual with hour 0" in {
    val analyzer = new DefaultAnalyzer("""checkHour("0", "GreaterOrEqual")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 1.0
  }

  it should "return 1.0 when current hour satisfies LessOrEqual with hour 23" in {
    val analyzer = new DefaultAnalyzer("""checkHour("23", "LessOrEqual")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 1.0
  }

  it should "return 0.0 when current hour is not Greater than 23" in {
    val analyzer = new DefaultAnalyzer("""checkHour("23", "Greater")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 0.0
  }

  it should "return 0.0 when current hour is not Less than 0" in {
    val analyzer = new DefaultAnalyzer("""checkHour("0", "Less")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 0.0
  }

  it should "use GreaterOrEqual as default operator" in {
    val analyzer = new DefaultAnalyzer("""checkHour("0")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 1.0
  }

  it should "support UTC timezone" in {
    val analyzer = new DefaultAnalyzer("""checkHour("0", "GreaterOrEqual", "UTC")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 1.0
  }

  it should "throw exception when no arguments provided" in {
    a [Exception] should be thrownBy {
      new CheckHourAtomic(List.empty, restrictedArgs)
    }
  }

  // --- CheckMinuteAtomic ---

  "checkMinute" should "return 1.0 when current minute satisfies GreaterOrEqual with 0" in {
    val analyzer = new DefaultAnalyzer("""checkMinute("0", "GreaterOrEqual")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 1.0
  }

  it should "return 1.0 when current minute satisfies LessOrEqual with 59" in {
    val analyzer = new DefaultAnalyzer("""checkMinute("59", "LessOrEqual")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 1.0
  }

  it should "return 0.0 when current minute is not Greater than 59" in {
    val analyzer = new DefaultAnalyzer("""checkMinute("59", "Greater")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 0.0
  }

  it should "throw exception when no arguments provided" in {
    a [Exception] should be thrownBy {
      new CheckMinuteAtomic(List.empty, restrictedArgs)
    }
  }

  // --- CheckDayOfWeekAtomic ---

  "checkDayOfWeek" should "return 1.0 when day satisfies GreaterOrEqual with 1 (Monday)" in {
    val analyzer = new DefaultAnalyzer("""checkDayOfWeek("1", "GreaterOrEqual")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 1.0
  }

  it should "return 1.0 when day satisfies LessOrEqual with 7 (Sunday)" in {
    val analyzer = new DefaultAnalyzer("""checkDayOfWeek("7", "LessOrEqual")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 1.0
  }

  it should "return 0.0 when day is not Greater than 7" in {
    val analyzer = new DefaultAnalyzer("""checkDayOfWeek("7", "Greater")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 0.0
  }

  it should "return 0.0 when day is not Less than 1" in {
    val analyzer = new DefaultAnalyzer("""checkDayOfWeek("1", "Less")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 0.0
  }

  it should "throw exception when no arguments provided" in {
    a [Exception] should be thrownBy {
      new CheckDayOfWeekAtomic(List.empty, restrictedArgs)
    }
  }

  // --- CheckDayOfMonthAtomic ---

  "checkDayOfMonth" should "return 1.0 when day satisfies GreaterOrEqual with 1" in {
    val analyzer = new DefaultAnalyzer("""checkDayOfMonth("1", "GreaterOrEqual")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 1.0
  }

  it should "return 1.0 when day satisfies LessOrEqual with 31" in {
    val analyzer = new DefaultAnalyzer("""checkDayOfMonth("31", "LessOrEqual")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 1.0
  }

  it should "return 0.0 when day is not Greater than 31" in {
    val analyzer = new DefaultAnalyzer("""checkDayOfMonth("31", "Greater")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 0.0
  }

  it should "throw exception when no arguments provided" in {
    a [Exception] should be thrownBy {
      new CheckDayOfMonthAtomic(List.empty, restrictedArgs)
    }
  }

  // --- CheckMonthAtomic ---

  "checkMonth" should "return 1.0 when month satisfies GreaterOrEqual with 1 (January)" in {
    val analyzer = new DefaultAnalyzer("""checkMonth("1", "GreaterOrEqual")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 1.0
  }

  it should "return 1.0 when month satisfies LessOrEqual with 12 (December)" in {
    val analyzer = new DefaultAnalyzer("""checkMonth("12", "LessOrEqual")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 1.0
  }

  it should "return 0.0 when month is not Greater than 12" in {
    val analyzer = new DefaultAnalyzer("""checkMonth("12", "Greater")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 0.0
  }

  it should "return 0.0 when month is not Less than 1" in {
    val analyzer = new DefaultAnalyzer("""checkMonth("1", "Less")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 0.0
  }

  it should "throw exception when no arguments provided" in {
    a [Exception] should be thrownBy {
      new CheckMonthAtomic(List.empty, restrictedArgs)
    }
  }

  // --- CheckMultipleDaysOfWeekAtomic ---

  "checkMultipleDaysOfWeek" should "return 1.0 when current day is in the list of all days" in {
    val analyzer = new DefaultAnalyzer("""checkMultipleDaysOfWeek("[1,2,3,4,5,6,7]", "CET")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 1.0
  }

  it should "return 0.0 when day list is empty (after parsing)" in {
    a [Exception] should be thrownBy {
      new CheckMultipleDaysOfWeekAtomic(List("[]", "CET"), restrictedArgs)
    }
  }

  it should "throw exception for invalid day number" in {
    a [Exception] should be thrownBy {
      new CheckMultipleDaysOfWeekAtomic(List("[0,8]", "CET"), restrictedArgs)
    }
  }

  it should "throw exception for invalid timezone" in {
    a [Exception] should be thrownBy {
      new CheckMultipleDaysOfWeekAtomic(List("[1,2,3]", "INVALID_TZ"), restrictedArgs)
    }
  }

  it should "throw exception when missing arguments" in {
    a [Exception] should be thrownBy {
      new CheckMultipleDaysOfWeekAtomic(List.empty, restrictedArgs)
    }
  }

  it should "throw exception when missing timezone argument" in {
    a [Exception] should be thrownBy {
      new CheckMultipleDaysOfWeekAtomic(List("[1,2,3]"), restrictedArgs)
    }
  }

  // --- CheckDateAtomic ---

  "checkDate" should "throw exception when no arguments provided" in {
    a [Exception] should be thrownBy {
      new CheckDateAtomic(List.empty, restrictedArgs)
    }
  }

  // --- TimeBetweenAtomic (additional tests) ---

  "timeBetween" should "return 1.0 for full day range 00:00 to 23:59" in {
    val analyzer = new DefaultAnalyzer("""timeBetween("00:00", "23:59", "CET")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 1.0
  }

  it should "return 1.0 for full day range with UTC timezone" in {
    val analyzer = new DefaultAnalyzer("""timeBetween("00:00", "23:59", "UTC")""", restrictedArgs)
    val result = analyzer.evaluate("test")
    result.score shouldBe 1.0
  }
}
