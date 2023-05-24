package com.hahnbanach.analyzer.entities

case class Context(
                    indexName: String,
                    stateName: String,
                    conversationId: String
                  )

object Context {
  def empty(): Context = {
    Context(
      indexName = "undefined",
      stateName = "undefined",
      conversationId = "undefined"
    )
  }
}
