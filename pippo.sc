import com.getjenny.analyzer.analyzers.DefaultAnalyzer

print("Sun pippo")

val restrictedArgs = Map.empty[String, String]

val anal = new DefaultAnalyzer("""and ( keywordTest ("ciao"), or ( testKeyword ("stupid"), keyword ("idiot")  )  )""", restrictedArgs)


