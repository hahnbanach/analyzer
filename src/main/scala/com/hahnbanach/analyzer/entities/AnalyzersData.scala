package com.hahnbanach.analyzer.entities

case class AnalyzersData(
                          context: Context = Context.empty(),
                          stateData: StateVariables = StateVariables(),
                          internal: Option[Map[String, Any]] = None
                        )
