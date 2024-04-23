package com.hahnbanach.analyzer.atoms

import cats.implicits._

import scala.util.matching.Regex

case class BaseAtomicException(message: String = "", cause: Throwable = None.orNull)
  extends Exception(message, cause)

trait KeyValueArgsBaseAtomic {
  val arguments: List[String]
  val factoryName: String
  val argumentsDescription: Map[String, (String, Boolean, Option[String])]

  def usage: String = s"""$factoryName: accept a sequence of <param name>, <param value>
                         | Hence the number of arguments must be even.
                         | Accepted arguments:
                         | ${argumentsDescription.map{case(k, v) => s"\t$k (${if(v._2) s"Optional with default(${v._3})" else "Mandatory"}): ${v._1}"}.mkString("\r")}
                         | """.stripMargin

  private val varNameRegex: Regex =
    new Regex(regex = "^(?:(VAR):)?([0-9A-Za-z-_\\.]+)$",
      groupNames = List("isVar", "name").toIndexedSeq*
    )

  private def argsComponents(input: String): (String, Boolean) = {
    varNameRegex.findFirstMatchIn(input) match {
      case Some(m) =>
        (m.group("name"), Option(m.group("isVar")).nonEmpty)
      case _ =>
        //TODO: improve exception handling on DefaultParse.scala row 140
        throw BaseAtomicException(s"$factoryName: wrong argument specification: $input")
    }
  }

  def argMap: Map[String, (Boolean, String)] = if(arguments.length % 2 != 0) {
    throw BaseAtomicException(usage)
  } else {
    arguments.sliding(2,2).map(l => {
      val head = l.head
      val last = l.last
      val argsComps = argsComponents(head)
      val varName = argsComps._1
      val isVariable = argsComps._2
      if(! argumentsDescription.contains(varName)) {
        throw BaseAtomicException(s"$factoryName: argument not supported: Name($varName) isVariable($isVariable)")
      }
      (varName, (isVariable, last))
    }).toMap
  }

  def getInputVariable(variableName: String, mandatory: Boolean,
                       default: Option[String]): Map[String, String] => Option[String] = {
    def input(varMap: Map[String, String]): Option[String] = {
      (varMap.get(variableName), mandatory, default) match {
        case (Some(v), _, _) => v.some
        case (None, true, Some(_)) => default
        case (None, false, _) => None
        case (_, _, _) => throw BaseAtomicException(s"$factoryName: variable $variableName does not exists or no default value provided")
      }
    }
    input
  }

  def parameter(argName: String): Map[String, String] => String = {
    optParameter(argName).map(_.getOrElse(
      throw BaseAtomicException(s"Missing mandatory argument in $factoryName: $argName")
    ))
  }

  def optParameter(argName: String): Map[String, String] => Option[String] = {
    argumentsDescription.get(argName) match {
      case Some(argDescription) =>
        argMap.get (argName) match {
          case Some (p) =>
            if (p._1) { // is variable name
              getInputVariable(p._2, argDescription._2, argDescription._3)
            } else { // is static argument
              (_: Map[String, String]) => p._2.some // is the value
            }
          case _ =>
            (_: Map[String, String]) =>
              argDescription match {
                case (_, true, Some(v)) => v.some
                case (_, false, v) => v
                case (_, _, _) => throw BaseAtomicException(s"Missing mandatory argument in $factoryName: $argName")
              }
        }
      case _ => throw BaseAtomicException(s"Argument not supported in $factoryName: $argName")
    }
  }
}
