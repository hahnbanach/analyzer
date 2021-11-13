package com.hahnbanach.analyzer.atoms

import scala.util.matching.Regex

case class BaseAtomicException(message: String = "", cause: Throwable = None.orNull)
  extends Exception(message, cause)

//TODO: move into the analyzer project
trait KeyValueArgsBaseAtomic {
  val arguments: List[String]
  val factoryName: String
  val argumentsDescription: Map[String, (String, Option[String])]

  def usage: String = s"""$factoryName: accept a sequence of <param name>, <param value>
                         | Hence the number of arguments must be even.
                         | Accepted arguments:
                         | ${argumentsDescription.map{case(k, v) => s"\t$k (${if(v._2.nonEmpty) "Optional" else "Mandatory"}): ${v._1}"}.mkString("\r")}
                         | """.stripMargin

  private[this] val varNameRegex: Regex =
    new Regex(regex = "^(?:(VAR):)?([0-9A-Za-z-_\\.]+)$",
      groupNames = List("isVar", "name").toIndexedSeq: _*
    )

  private[this] def argsComponents(input: String): (String, Boolean) = {
    varNameRegex.findFirstMatchIn(input) match {
      case Some(m) =>
        (m.group("name"), Option(m.group("isVar")).nonEmpty)
      case _ =>
        //TODO: improve exception handling on DefaultParse.scala row 140
        throw BaseAtomicException(s"$factoryName: wrong argument specification: $input")
    }
  }

  val argMap: Map[String, (Boolean, String)] = if(arguments.length % 2 != 0){
    throw BaseAtomicException(usage)
  } else {
    arguments.sliding(2,2).map(l => {
      val head = l.head
      val last = l.last
      val argsComps = argsComponents(head)
      val varName = argsComps._1
      val isVariable = argsComps._2
      (varName, (isVariable, last))
    }).toMap
  }

  def getInputVariable(variableName: String, default: Option[String]): Map[String, String] => String = {
    def input(varMap: Map[String, String]): String = {
      (varMap.get(variableName), default) match {
        case (Some(v), _) => v
        case (None, Some(d)) => d
        case (_, _) => throw BaseAtomicException(s"$factoryName: variable $variableName does not exists or no default value provided")
      }
    }
    input
  }

  def parameter(argName: String): Map[String, String] => String = {
    argumentsDescription.get(argName) match {
      case Some(argDescription) =>
        argMap.get (argName) match {
          case Some (p) =>
            if (p._1) { // is variable name
              getInputVariable(p._2, argDescription._2)
            } else { // is static argument
              (_: Map[String, String]) => p._2 // is the value
            }
          case _ =>
            (_: Map[String, String]) =>
              argDescription._2
                .getOrElse(throw BaseAtomicException(s"Missing mandatory argument in $factoryName: $argName"))
        }
      case _ => throw BaseAtomicException(s"Argument not supported in $factoryName: $argName")
    }
  }
}
