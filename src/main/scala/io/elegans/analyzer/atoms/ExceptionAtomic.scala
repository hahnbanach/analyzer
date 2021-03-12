package io.elegans.analyzer.atoms

/**
  * Created by Angelo Leto <angelo.leto@elegans.io> on 03/03/17.
  */

case class ExceptionAtomic(message: String = "", cause: Throwable = None.orNull)
  extends Exception(message, cause)
