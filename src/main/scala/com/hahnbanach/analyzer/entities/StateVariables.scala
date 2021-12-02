package com.hahnbanach.analyzer.entities

case class StateVariables(
                           traversedStates: Vector[DtHistoryItem] = Vector.empty[DtHistoryItem],
                           variables: Map[String, String] = Map.empty[String, String]
                         )
