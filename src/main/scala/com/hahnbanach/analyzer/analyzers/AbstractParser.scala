package com.hahnbanach.analyzer.analyzers

import com.hahnbanach.analyzer.entities.{AnalyzersData, Result}

/**
  * Created by mal on 20/02/2017.
  */

abstract class AbstractParser(command: String) {
  def evaluate(sentence: String, data: AnalyzersData = AnalyzersData()): Result
}
