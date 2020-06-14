package com.getjenny.analyzer.analyzers

/**
  * Created by mal on 20/02/2017.
  */

import com.getjenny.analyzer.operators._
import com.getjenny.analyzer.atoms._

class DefaultAnalyzer(command: String, restrictedArgs: Map[String, String])
  extends {
    override val atomicFactory = new DefaultFactoryAtomic
    override val operatorFactory = new DefaultFactoryOperator
  } with DefaultParser(command: String, restrictedArgs: Map[String, String])
