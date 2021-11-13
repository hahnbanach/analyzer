package com.hahnbanach.analyzer.analyzers

/**
  * Created by mal on 20/02/2017.
  */

import com.hahnbanach.analyzer.atoms.DefaultFactoryAtomic
import com.hahnbanach.analyzer.operators._

abstract class DefaultAnalyzerEarlyInit(command: String,
                            restrictedArgs: Map[String, String],
                            override val atomicFactory: DefaultFactoryAtomic,
                            override val operatorFactory: DefaultFactoryOperator)
  extends DefaultParser(command: String, restrictedArgs: Map[String, String])

class DefaultAnalyzer(command: String,
                       restrictedArgs: Map[String, String]) extends
  DefaultAnalyzerEarlyInit(command,
     restrictedArgs, new DefaultFactoryAtomic, new DefaultFactoryOperator)
