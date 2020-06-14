package com.getjenny.analyzer.expressions

case class AnalyzersData(
                          traversedStates: Vector[String] = Vector.empty[String],
                          extractedVariables: Map[String, String] = Map.empty[String, String]
               )

case class Context(
                    indexName: String = "",
                    stateName: String = ""
                  )

case class AnalyzersDataInternal(
                                  context: Context = Context(),
                                  traversedStates: Vector[String] = Vector.empty[String],
                                  extractedVariables: Map[String, String] = Map.empty[String, String],
                                  data: Map[String, Any] = Map.empty[String, Any]
                        )
