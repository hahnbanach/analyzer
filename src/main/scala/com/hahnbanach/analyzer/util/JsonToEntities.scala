package com.hahnbanach.analyzer.util

/**
  * Created by angelo on 13/02/2019.
  */

import com.hahnbanach.analyzer.entities._
import com.hahnbanach.analyzer.serializers.JsonSupport._
import spray.json._

object JsonToEntities {
  def openingTime(json: String): OpeningTime = {
    json.parseJson.convertTo[OpeningTime]
  }
}
