package com.hahnbanach.analyzer.entities

case class AnalyzersData(
                          context: Context = Context(),
                          stateData: StateVariables = StateVariables(),
                          internal: Option[Map[String, Any]] = None
                        )
