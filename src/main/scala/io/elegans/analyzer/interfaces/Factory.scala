package io.elegans.analyzer.interfaces
import io.elegans.analyzer.atoms.AbstractAtomic
import io.elegans.analyzer.operators.AbstractOperator

/**
  * Created by Angelo Leto <angelo.leto@elegans.io> on 07/06/21.
  */

case class InstanceNotFoundException(message: String = "", cause: Throwable = None.orNull)
  extends Exception(message, cause)

trait OperatorFactoryTrait[T] {
  val instanceName: String = "Operator"
  val functionsMap: Map[String, T => AbstractOperator]
  val operations: Set[String]

  def get(name: String, argument: T): AbstractOperator = {
    functionsMap.get(name) match {
      case Some(v) => v(argument)
      case _ => throw InstanceNotFoundException(s"$instanceName \'" + name + "\' not found")
    }
  }
}

trait AtomicFactoryTrait[T, Z] {
  val instanceName: String = "Atom"
  val functionsMap: Map[String, (T, Z) => AbstractAtomic]
  val operations: Set[String]

  def get(name: String, argument: T, restrictedArgs: Z): AbstractAtomic = {
    functionsMap.get(name) match {
      case Some(v) => v(argument, restrictedArgs)
      case _ => throw InstanceNotFoundException(s"$instanceName \'" + name + "\' not found")
    }
  }
}
