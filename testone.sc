import com.getjenny.analyzer.analyzers.DefaultAnalyzer
import com.getjenny.analyzer.atoms.KeywordAtomic

val restrictedArgs = Map.empty[String, String]
//val k = new KeywordAtomic(List("ciao"), restrictedArgs)
//print("""EVALUATE ciao, stupid moron: """ + k.evaluate("ciao, stupid moron").score)  // =0.333 ?
//print("""EVALUATE ciao, stupid moron: """ + k.matches("ciao stupid moron").score)  // =1?

val anal = new DefaultAnalyzer("""and (keyword("ciao"), or(keyword("stupid"), keyword("idiot")))""", restrictedArgs)
print("""EVALUATE anal 1: """ + anal.evaluate("ciao stupid moron").score)
print("""EVALUATE anal 0: """ + anal.evaluate("stupid  idiot").score)

// either "stupido" or "idiota"
//val analBayes = new DefaultAnalyzer("""disjunction( keyword("stupid"), keyword("idiot") )""", restrictedArgs)
//
//val idiot = analBayes.evaluate("ciao nice idiot moron").score
//val stupid_idiot_long = analBayes.evaluate("ciao stupid moron idiot").score
//val stupid_idiot_short = analBayes.evaluate("ciao stupid idiot").score
//
////two is better than one
//assert(stupid_idiot_long > idiot)
//assert(stupid_idiot_short > idiot)
//
////finding in short is better than finding in longer
//assert(stupid_idiot_short > stupid_idiot_long)
