package io.elegans.analyzer.operators

/**
  * Created by Angelo Leto <angelo.leto@elegans.io> on 03/03/17.
  */

case class OperatorException(message: String = "", cause: Throwable = None.orNull)
  extends Exception(message, cause)

case class OperatorNotFoundException(message: String = "", cause: Throwable = None.orNull)
  extends Exception(message, cause)
