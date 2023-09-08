package com.hahnbanach.analyzer.atoms

import cats.implicits._
import com.hahnbanach.analyzer.entities.{AnalyzersData, Result}
import com.roundeights.hasher.Implicits._
import io.jvm.uuid.UUID

/**
 * Created by angelo on 08/09/23.
 */

/** Build a json from string variables
 *
 * @param arguments see description below
 */
class ChecksumAtomic(val arguments: List[String], restrictedArgs: Map[String, String]) extends AbstractAtomic with KeyValueArgsBaseAtomic {
  override def toString: String = "checksum"
  override val factoryName: String = "checksum"
  val isEvaluateNormalized: Boolean = true

  override val argumentsDescription: Map[String, (String, Boolean, Option[String])] = Map(
    //name -> explanation, isOptional, default value (if provided then arg is optional)
    "input" -> (
      "the input string",
      true,
      None),
    "checksum" -> (
      "one of: md5, crc32, bcrypt, sha1, sha256, sha512, UUID",
      false,
      "md5".some),
    "outName" -> (
      "the output variable name",
      false,
      "CHECKSUM".some),
    "outPattern" -> (
      "a prefix for the output variables, default is \"local.\"",
      false,
      "local.".some
    )
  )

  /** Sum or Sub Long values
   *
   * @param query the user query
   * @param data the dictionary of variables
   * @return Result 1.0 if no error occurred
   *
   */
  def evaluate(query: String, data: AnalyzersData = AnalyzersData()): Result = {
    val input = parameter("input")(data.stateData.variables)
    val checksumType = parameter("checksum")(data.stateData.variables)
    val outName = parameter("outName")(data.stateData.variables)
    val pattern = parameter("outPattern")(data.stateData.variables)

    val checksum = checksumType match {
      case "crc32" =>
        input.crc32.hex
      case "bcrypt" =>
        input.bcrypt.hex
      case "md5" =>
        input.md5.hex
      case "sha1" =>
        input.sha1.hex
      case "sha256" =>
        input.sha256.hex
      case "sha512" =>
        input.sha512.hex
      case "uuid" =>
        UUID.nameUUIDFromBytes(input.getBytes("UTF-8")).toString
    }

    Result(
      score = 1.0d,
      data = data.copy(
        stateData = data.stateData.copy(
          variables =
            data.stateData.variables.updated(s"$pattern$outName", checksum)
        )
      )
    )
  }
}

