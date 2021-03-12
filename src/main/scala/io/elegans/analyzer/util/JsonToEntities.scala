package io.elegans.analyzer.util

/**
  * Created by angelo on 13/02/2019.
  */

import io.elegans.analyzer.entities._
import io.elegans.analyzer.serializers.JsonSupport._
import spray.json._

object JsonToEntities {
  def openingTime(json: String): OpeningTime = {
    json.parseJson.convertTo[OpeningTime]
  }
}
