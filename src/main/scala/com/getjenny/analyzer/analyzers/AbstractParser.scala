package io.elegans.analyzer.analyzers

import io.elegans.analyzer.entities.{AnalyzersDataInternal, Result}

/**
  * Created by mal on 20/02/2017.
  */

abstract class AbstractParser(command: String) {
  def evaluate(sentence: String, data: AnalyzersDataInternal = AnalyzersDataInternal()): Result
}
