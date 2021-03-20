package io.elegans.analyzer.analyzers

/**
  * Created by mal on 20/02/2017.
  */

import io.elegans.analyzer.atoms.DefaultFactoryAtomic
import io.elegans.analyzer.operators._

abstract class DefaultAnalyzerEarlyInit(command: String,
                            restrictedArgs: Map[String, String],
                            override val atomicFactory: DefaultFactoryAtomic,
                            override val operatorFactory: DefaultFactoryOperator)
  extends DefaultParser(command: String, restrictedArgs: Map[String, String])

class DefaultAnalyzer(command: String,
                       restrictedArgs: Map[String, String]) extends
  DefaultAnalyzerEarlyInit(command,
     restrictedArgs, new DefaultFactoryAtomic, new DefaultFactoryOperator)