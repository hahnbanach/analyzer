package com.getjenny.analyzer.atoms

/**
  * Created by mal on 20/02/2017.
  */

import com.getjenny.analyzer.interfaces._

class DefaultFactoryAtomic extends AtomicFactoryTrait[List[String], AbstractAtomic, Map[String, String]] {

  override val operations: Set[String] = Set("keyword",
    "similar",
    "synonym",
    "regex",
    "matchPatternRegex",
    "matchDateDDMMYYYY",
    "existsVariable",
    "cosDistanceKeywords",
    "distance",
    "checkTimestamp",
    "checkDayOfWeek",
    "checkDayOfMonth",
    "checkMonth",
    "checkHour",
    "checkMinute",
    "toDouble",
    "doubleNumberVariable",
    "checkTimestampVariable",
    "isServiceOpen",
    "setServiceOpening"
  )

  override def get(name: String, argument: List[String], restrictedArgs: Map[String, String]):
  AbstractAtomic = name.filter(c => !c.isWhitespace ) match {
    case "keyword" => new KeywordAtomic(argument, restrictedArgs)
    case "regex" => new RegularExpressionAtomic(argument, restrictedArgs)
    case "matchPatternRegex" => new MatchPatternRegexAtomic(argument, restrictedArgs)
    case "matchDateDDMMYYYY" => new MatchDateDDMMYYYYAtomic(argument, restrictedArgs)
    case "existsVariable" => new ExistsVariableAtomic(argument, restrictedArgs)
    case "distance" | "cosDistanceKeywords" => new CosineDistanceAnalyzer(argument, restrictedArgs)
    case "checkTimestamp" => new CheckTimestampAtomic(argument, restrictedArgs)
    case "checkDayOfWeek" => new CheckDayOfWeekAtomic(argument, restrictedArgs)
    case "checkDayOfMonth" => new CheckDayOfMonthAtomic(argument, restrictedArgs)
    case "checkMonth" => new CheckMonthAtomic(argument, restrictedArgs)
    case "checkHour" => new CheckHourAtomic(argument, restrictedArgs)
    case "checkMinute" => new CheckMinuteAtomic(argument, restrictedArgs)
    case "toDouble" => new ToDoubleNumberAtomic(argument, restrictedArgs)
    case "doubleNumberVariable" => new DoubleNumberVariableAtomic(argument, restrictedArgs)
    case "checkTimestampVariable" => new CheckTimestampVariableAtomic(argument, restrictedArgs)
    case "isServiceOpen" => new IsServiceOpenAtomic(argument, restrictedArgs)
    case "setServiceOpening" => new SetServiceOpeningAtomic(argument, restrictedArgs)
    case _ => throw ExceptionAtomic("Atom \'" + name + "\' not found")
  }
}
