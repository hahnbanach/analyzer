package io.elegans.analyzer.analyzers

/**
  * Created by mal on 20/02/2017.
  */

import io.elegans.analyzer.operators._
import io.elegans.analyzer.atoms._

class DefaultAnalyzer(command: String, restrictedArgs: Map[String, String])
  extends {
    override val atomicFactory = new DefaultFactoryAtomic
    override val operatorFactory = new DefaultFactoryOperator
  } with DefaultParser(command: String, restrictedArgs: Map[String, String])
