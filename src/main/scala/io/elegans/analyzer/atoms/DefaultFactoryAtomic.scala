package io.elegans.analyzer.atoms

/**
  * Created by mal on 20/02/2017.
  */

import io.elegans.analyzer.interfaces._

class DefaultFactoryAtomic extends AtomicFactoryTrait[List[String], Map[String, String]] {
  val functionsMap: Map[String, (List[String], Map[String, String]) => AbstractAtomic] = Map(
    ("checkDate", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new CheckDateAtomic(argument, restrictedArgs)),
    ("checkDayOfMonth", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new CheckDayOfMonthAtomic(argument, restrictedArgs)),
    ("checkDayOfWeek", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new CheckDayOfWeekAtomic(argument, restrictedArgs)),
    ("checkHour", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new CheckHourAtomic(argument, restrictedArgs)),
    ("checkMinute", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new CheckMinuteAtomic(argument, restrictedArgs)),
    ("checkMonth", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new CheckMonthAtomic(argument, restrictedArgs)),
    ("checkMultipleDaysOfWeek", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new CheckMultipleDaysOfWeekAtomic(argument, restrictedArgs)),
    ("checkTimestamp", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new CheckTimestampAtomic(argument, restrictedArgs)),
    ("checkTimestampVariable", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new CheckTimestampVariableAtomic(argument, restrictedArgs)),
    ("checkVariableValue", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new CheckVariableValue(argument, restrictedArgs)),
    ("copyVariable", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new CopyVariableAtomic(argument, restrictedArgs)),
    ("deleteVariables", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new DeleteVariablesAtomic(argument, restrictedArgs)),
    ("distance", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new CosineDistanceAnalyzer(argument, restrictedArgs)),
    ("cosDistanceKeywords", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new CosineDistanceAnalyzer(argument, restrictedArgs)),
    ("doubleNumberVariable", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new DoubleNumberVariableAtomic(argument, restrictedArgs)),
    ("existsVariable", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new ExistsVariableAtomic(argument, restrictedArgs)),
    ("hasTravState", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new HasTravStateAtomic(argument, restrictedArgs)),
    ("hasTravStateInPosition", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new HasTravStateInPositionAtomic(argument, restrictedArgs)),
    ("hasTravStateInPositionRev", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new HasTravStateInPositionRevAtomic(argument, restrictedArgs)),
    ("isServiceOpen", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new IsServiceOpenAtomic(argument, restrictedArgs)),
    ("iterateOnVariables", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new IterateOnVariablesAtomic(argument, restrictedArgs)),
    ("keyword", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new KeywordAtomic(argument, restrictedArgs)),
    ("lastTravStateIs", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new LastTravStateIsAtomic(argument, restrictedArgs)),
    ("matchDateDDMMYYYY", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new MatchDateDDMMYYYYAtomic(argument, restrictedArgs)),
    ("matchPatternRegex", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new MatchPatternRegexAtomic(argument, restrictedArgs)),
    ("modulusVariable", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new ModulusVariableAtomic(argument, restrictedArgs)),
    ("prevTravStateIs", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new PrevTravStateIsAtomic(argument, restrictedArgs)),
    ("regex", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new RegularExpressionAtomic(argument, restrictedArgs)),
    ("regexVariableValue", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new RegexVariableValueAtomic(argument, restrictedArgs)),
    ("resetVariable", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new ResetVariableAtomic(argument, restrictedArgs)),
    ("setServiceOpening", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new SetServiceOpeningAtomic(argument, restrictedArgs)),
    ("setVariable", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new SetVariableAtomic(argument, restrictedArgs)),
    ("timeBetween", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new TimeBetweenAtomic(argument, restrictedArgs)),
    ("toDouble", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new ToDoubleNumberAtomic(argument, restrictedArgs)),
    ("vOneKeyword", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new KeywordAtomic(argument, restrictedArgs)),
    ("variableSize", (argument: List[String], restrictedArgs: Map[String, String]) =>
      new VariableSizeAtomic(argument, restrictedArgs))
  )

  val operations: Set[String] = functionsMap.keys.toSet
}
