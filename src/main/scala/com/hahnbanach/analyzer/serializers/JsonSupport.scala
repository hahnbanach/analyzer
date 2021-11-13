package com.hahnbanach.analyzer.serializers

/**
  * Created by angelo on 13/02/2019.
  */

import com.hahnbanach.analyzer.entities._
import spray.json._

object JsonSupport extends DefaultJsonProtocol {
  implicit val colorFormat = jsonFormat6(OpeningTime)
}



