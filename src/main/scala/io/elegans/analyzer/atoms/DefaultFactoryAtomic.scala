package io.elegans.analyzer.atoms

/**
  * Created by mal on 20/02/2017.
  */

import io.elegans.analyzer.interfaces._

class DefaultFactoryAtomic extends AtomicFactoryTrait[List[String], AbstractAtomic, Map[String, String]] {

  override val operations: Set[String] = Set(
    "checkDate",
    "checkDayOfMonth",
    "checkDayOfWeek",
    "checkHour",
    "checkMinute",
    "checkMonth",
    "checkMultipleDaysOfWeek",
    "checkTimestamp",
    "checkTimestampVariable",
    "checkVariableValue",
    "copyVariable",
    "cosDistanceKeywords",
    "deleteVariables",
    "distance",
    "doubleNumberVariable",
    "entityExtractor",
    "existsVariable",
    "hasTravState",
    "hasTravStateInPosition",
    "hasTravStateInPositionRev",
    "isServiceOpen",
    "iterateOnVariables",
    "keyword",
    "lastTravStateIs",
    "matchDateDDMMYYYY",
    "matchPatternRegex",
    "modulusVariable",
    "prevTravStateIs",
    "regex",
    "regexVariableValue",
    "resetVariable",
    "setServiceOpening",
    "setVariable",
    "timeBetween",
    "toDouble",
    "vOneKeyword",
    "variableSize"
  )

  override def get(name: String, argument: List[String], restrictedArgs: Map[String, String]):
  AbstractAtomic = name.filter(c => !c.isWhitespace) match {
    case "checkDate" => new CheckDateAtomic(argument, restrictedArgs)
    case "checkDayOfMonth" => new CheckDayOfMonthAtomic(argument, restrictedArgs)
    case "checkDayOfWeek" => new CheckDayOfWeekAtomic(argument, restrictedArgs)
    case "checkHour" => new CheckHourAtomic(argument, restrictedArgs)
    case "checkMinute" => new CheckMinuteAtomic(argument, restrictedArgs)
    case "checkMonth" => new CheckMonthAtomic(argument, restrictedArgs)
    case "checkMultipleDaysOfWeek" => new CheckMultipleDaysOfWeekAtomic(argument, restrictedArgs)
    case "checkTimestamp" => new CheckTimestampAtomic(argument, restrictedArgs)
    case "checkTimestampVariable" => new CheckTimestampVariableAtomic(argument, restrictedArgs)
    case "checkVariableValue" => new CheckVariableValue(argument, restrictedArgs)
    case "copyVariable" => new CopyVariableAtomic(argument, restrictedArgs)
    case "deleteVariables" => new DeleteVariablesAtomic(argument, restrictedArgs)
    case "distance" | "cosDistanceKeywords" => new CosineDistanceAnalyzer(argument, restrictedArgs)
    case "doubleNumberVariable" => new DoubleNumberVariableAtomic(argument, restrictedArgs)
    case "existsVariable" => new ExistsVariableAtomic(argument, restrictedArgs)
    case "hasTravState" => new HasTravStateAtomic(argument, restrictedArgs)
    case "hasTravStateInPosition" => new HasTravStateInPositionAtomic(argument, restrictedArgs)
    case "hasTravStateInPositionRev" => new HasTravStateInPositionRevAtomic(argument, restrictedArgs)
    case "isServiceOpen" => new IsServiceOpenAtomic(argument, restrictedArgs)
    case "iterateOnVariables" => new IterateOnVariablesAtomic(argument, restrictedArgs)
    case "keyword" => new KeywordAtomic(argument, restrictedArgs)
    case "lastTravStateIs" => new LastTravStateIsAtomic(argument, restrictedArgs)
    case "matchDateDDMMYYYY" => new MatchDateDDMMYYYYAtomic(argument, restrictedArgs)
    case "matchPatternRegex" => new MatchPatternRegexAtomic(argument, restrictedArgs)
    case "modulusVariable" => new ModulusVariableAtomic(argument, restrictedArgs)
    case "prevTravStateIs" => new PrevTravStateIsAtomic(argument, restrictedArgs)
    case "regex" => new RegularExpressionAtomic(argument, restrictedArgs)
    case "regexVariableValue" => new RegexVariableValueAtomic(argument, restrictedArgs)
    case "resetVariable" => new ResetVariableAtomic(argument, restrictedArgs)
    case "setServiceOpening" => new SetServiceOpeningAtomic(argument, restrictedArgs)
    case "setVariable" => new SetVariableAtomic(argument, restrictedArgs)
    case "timeBetween" => new TimeBetweenAtomic(argument, restrictedArgs)
    case "toDouble" => new ToDoubleNumberAtomic(argument, restrictedArgs)
    case "vOneKeyword" => new KeywordAtomic(argument, restrictedArgs)
    case "variableSize" => new VariableSizeAtomic(argument, restrictedArgs)
    case _ => throw ExceptionAtomic("Atom \'" + name + "\' not found")
  }
}
