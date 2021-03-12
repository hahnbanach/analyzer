package io.elegans.analyzer.util

/**
  * Created by Angelo Leto <angelo.leto@elegans.io> on 23/06/17.
  */

abstract class PatternExtraction(declaration: String) {
  def evaluate(input: String): Map[String, String]
}

