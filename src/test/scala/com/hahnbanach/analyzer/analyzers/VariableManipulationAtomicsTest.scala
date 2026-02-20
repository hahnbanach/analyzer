package com.hahnbanach.analyzer.analyzers

import com.hahnbanach.analyzer.atoms.ExceptionAtomic
import com.hahnbanach.analyzer.entities.{AnalyzersData, StateVariables}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class VariableManipulationAtomicsTest extends AnyFlatSpec with Matchers {

  val restrictedArgs: Map[String, String] = Map.empty[String, String]

  // --- ExistsVariableAtomic ---

  "existsVariable" should "return 1.0 when variable exists" in {
    val data = AnalyzersData(
      stateData = StateVariables(variables = Map("myVar" -> "hello"))
    )
    val analyzer = new DefaultAnalyzer("""existsVariable("myVar")""", restrictedArgs)
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 1.0
  }

  it should "return 0.0 when variable does not exist" in {
    val data = AnalyzersData(
      stateData = StateVariables(variables = Map("otherVar" -> "hello"))
    )
    val analyzer = new DefaultAnalyzer("""existsVariable("myVar")""", restrictedArgs)
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 0.0
  }

  it should "return 0.0 when no variables exist" in {
    val data = AnalyzersData()
    val analyzer = new DefaultAnalyzer("""existsVariable("myVar")""", restrictedArgs)
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 0.0
  }

  // --- SetVariableAtomic ---

  "setVariable" should "set a new variable and return 1.0" in {
    val data = AnalyzersData()
    val analyzer = new DefaultAnalyzer("""setVariable("myVar", "myValue")""", restrictedArgs)
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 1.0
    result.data.stateData.variables("myVar") shouldBe "myValue"
  }

  it should "overwrite an existing variable" in {
    val data = AnalyzersData(
      stateData = StateVariables(variables = Map("myVar" -> "oldValue"))
    )
    val analyzer = new DefaultAnalyzer("""setVariable("myVar", "newValue")""", restrictedArgs)
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 1.0
    result.data.stateData.variables("myVar") shouldBe "newValue"
  }

  it should "preserve other existing variables" in {
    val data = AnalyzersData(
      stateData = StateVariables(variables = Map("other" -> "kept"))
    )
    val analyzer = new DefaultAnalyzer("""setVariable("myVar", "myValue")""", restrictedArgs)
    val result = analyzer.evaluate("test", data)
    result.data.stateData.variables("other") shouldBe "kept"
    result.data.stateData.variables("myVar") shouldBe "myValue"
  }

  // --- ResetVariableAtomic ---

  "resetVariable" should "remove a variable when called with one argument" in {
    val data = AnalyzersData(
      stateData = StateVariables(variables = Map("myVar" -> "value", "other" -> "kept"))
    )
    val atom = new com.hahnbanach.analyzer.atoms.ResetVariableAtomic(List("myVar"), restrictedArgs)
    val result = atom.evaluate("test", data)
    result.score shouldBe 1.0
    result.data.stateData.variables.contains("myVar") shouldBe false
    result.data.stateData.variables("other") shouldBe "kept"
  }

  it should "set a variable to new value when called with two arguments" in {
    val data = AnalyzersData(
      stateData = StateVariables(variables = Map("myVar" -> "oldValue"))
    )
    val analyzer = new DefaultAnalyzer("""resetVariable("myVar", "newValue")""", restrictedArgs)
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 1.0
    result.data.stateData.variables("myVar") shouldBe "newValue"
  }

  // --- UnescapedSetVariableAtomic ---

  "unescapedSetVariable" should "set a variable with backslashes removed" in {
    val data = AnalyzersData()
    val analyzer = new DefaultAnalyzer("""unescapedSetVariable("myVar", "hello\\world")""", restrictedArgs)
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 1.0
    result.data.stateData.variables("myVar") shouldBe "helloworld"
  }

  // --- CheckVariableValue ---

  "checkVariableValue" should "return 1.0 when variable matches value" in {
    val data = AnalyzersData(
      stateData = StateVariables(variables = Map("myVar" -> "expected"))
    )
    val analyzer = new DefaultAnalyzer("""checkVariableValue("myVar", "expected")""", restrictedArgs)
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 1.0
  }

  it should "return 0.0 when variable does not match value" in {
    val data = AnalyzersData(
      stateData = StateVariables(variables = Map("myVar" -> "other"))
    )
    val analyzer = new DefaultAnalyzer("""checkVariableValue("myVar", "expected")""", restrictedArgs)
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 0.0
  }

  it should "return 0.0 when variable does not exist" in {
    val data = AnalyzersData()
    val analyzer = new DefaultAnalyzer("""checkVariableValue("myVar", "expected")""", restrictedArgs)
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 0.0
  }

  // --- CompareVariablesValueAtomic ---

  "compareVariablesValue" should "return 1.0 when both variables have the same value" in {
    val data = AnalyzersData(
      stateData = StateVariables(variables = Map("var1" -> "same", "var2" -> "same"))
    )
    val analyzer = new DefaultAnalyzer("""compareVariablesValue("var1", "var2")""", restrictedArgs)
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 1.0
  }

  it should "return 0.0 when variables have different values" in {
    val data = AnalyzersData(
      stateData = StateVariables(variables = Map("var1" -> "value1", "var2" -> "value2"))
    )
    val analyzer = new DefaultAnalyzer("""compareVariablesValue("var1", "var2")""", restrictedArgs)
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 0.0
  }

  it should "return 0.0 when first variable does not exist" in {
    val data = AnalyzersData(
      stateData = StateVariables(variables = Map("var2" -> "value"))
    )
    val analyzer = new DefaultAnalyzer("""compareVariablesValue("var1", "var2")""", restrictedArgs)
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 0.0
  }

  it should "return 0.0 when second variable does not exist" in {
    val data = AnalyzersData(
      stateData = StateVariables(variables = Map("var1" -> "value"))
    )
    val analyzer = new DefaultAnalyzer("""compareVariablesValue("var1", "var2")""", restrictedArgs)
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 0.0
  }

  it should "return 0.0 when neither variable exists" in {
    val data = AnalyzersData()
    val analyzer = new DefaultAnalyzer("""compareVariablesValue("var1", "var2")""", restrictedArgs)
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 0.0
  }

  // --- CopyVariableAtomic ---

  "copyVariable" should "copy value from source to destination variable" in {
    val data = AnalyzersData(
      stateData = StateVariables(variables = Map("src" -> "copied_value"))
    )
    val analyzer = new DefaultAnalyzer("""copyVariable("sourceVarname=src", "destVarname=dst")""", restrictedArgs)
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 1.0
    result.data.stateData.variables("dst") shouldBe "copied_value"
    result.data.stateData.variables("src") shouldBe "copied_value"
  }

  it should "return 0.0 when source variable does not exist" in {
    val data = AnalyzersData()
    val analyzer = new DefaultAnalyzer("""copyVariable("sourceVarname=src", "destVarname=dst")""", restrictedArgs)
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 0.0
    result.data.stateData.variables.contains("dst") shouldBe false
  }

  // --- DeleteVariablesAtomic ---

  "deleteVariables" should "delete variables matching regex pattern" in {
    val data = AnalyzersData(
      stateData = StateVariables(variables = Map(
        "temp_a" -> "1", "temp_b" -> "2", "keep" -> "3"
      ))
    )
    val atom = new com.hahnbanach.analyzer.atoms.DeleteVariablesAtomic(List("regex=temp_.*"), restrictedArgs)
    val result = atom.evaluate("test", data)
    result.score shouldBe 1.0
    result.data.stateData.variables.contains("temp_a") shouldBe false
    result.data.stateData.variables.contains("temp_b") shouldBe false
    result.data.stateData.variables("keep") shouldBe "3"
  }

  it should "return 0.0 when no variables match" in {
    val data = AnalyzersData(
      stateData = StateVariables(variables = Map("keep1" -> "a", "keep2" -> "b"))
    )
    val analyzer = new DefaultAnalyzer("""deleteVariables("regex=temp_.*")""", restrictedArgs)
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 0.0
    result.data.stateData.variables.size shouldBe 2
  }

  // --- VariableBytesCountAtomic ---

  "variableBytesCount" should "return byte count of variable content" in {
    val data = AnalyzersData(
      stateData = StateVariables(variables = Map("myVar" -> "hello"))
    )
    val analyzer = new DefaultAnalyzer("""variableBytesCount("myVar")""", restrictedArgs)
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 5.0
  }

  it should "return 0.0 when variable does not exist" in {
    val data = AnalyzersData()
    val analyzer = new DefaultAnalyzer("""variableBytesCount("myVar")""", restrictedArgs)
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 0.0
  }

  it should "return correct byte count for empty string" in {
    val data = AnalyzersData(
      stateData = StateVariables(variables = Map("myVar" -> ""))
    )
    val analyzer = new DefaultAnalyzer("""variableBytesCount("myVar")""", restrictedArgs)
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 0.0
  }

  // --- ModulusVariableAtomic ---

  "modulusVariable" should "return modulus of variable value" in {
    val data = AnalyzersData(
      stateData = StateVariables(variables = Map("myVar" -> "10"))
    )
    val analyzer = new DefaultAnalyzer("""modulusVariable("myVar", "3", "0")""", restrictedArgs)
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 1.0
  }

  it should "return default value when variable does not exist" in {
    val data = AnalyzersData()
    val analyzer = new DefaultAnalyzer("""modulusVariable("myVar", "3", "99")""", restrictedArgs)
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 99.0
  }

  it should "return 0 when value is evenly divisible" in {
    val data = AnalyzersData(
      stateData = StateVariables(variables = Map("myVar" -> "9"))
    )
    val analyzer = new DefaultAnalyzer("""modulusVariable("myVar", "3", "0")""", restrictedArgs)
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 0.0
  }

  // --- IterateOnVariablesAtomic ---

  "iterateOnVariables" should "iterate over variables and set destination" in {
    val data = AnalyzersData(
      stateData = StateVariables(variables = Map(
        "ITEMS_0" -> "first", "ITEMS_1" -> "second", "ITEMS_2" -> "third"
      ))
    )
    val analyzer = new DefaultAnalyzer(
      """iterateOnVariables("countersPattern=COUNTER", "variablesPattern=ITEMS", "destinationVarname=CURRENT")""",
      restrictedArgs
    )
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 1.0
    result.data.stateData.variables("CURRENT") shouldBe "first"
    result.data.stateData.variables("COUNTER_CURSOR") shouldBe "1"
    result.data.stateData.variables("COUNTER_ENDIDX") shouldBe "3"
  }

  it should "return 0.0 when no matching variables exist" in {
    val data = AnalyzersData(
      stateData = StateVariables(variables = Map("unrelated" -> "value"))
    )
    val analyzer = new DefaultAnalyzer(
      """iterateOnVariables("countersPattern=COUNTER", "variablesPattern=ITEMS", "destinationVarname=CURRENT")""",
      restrictedArgs
    )
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 0.0
  }

  it should "continue iteration from cursor position" in {
    val data = AnalyzersData(
      stateData = StateVariables(variables = Map(
        "ITEMS_0" -> "first", "ITEMS_1" -> "second", "ITEMS_2" -> "third",
        "COUNTER_CURSOR" -> "1", "COUNTER_ENDIDX" -> "3"
      ))
    )
    val analyzer = new DefaultAnalyzer(
      """iterateOnVariables("countersPattern=COUNTER", "variablesPattern=ITEMS", "destinationVarname=CURRENT")""",
      restrictedArgs
    )
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 1.0
    result.data.stateData.variables("CURRENT") shouldBe "second"
    result.data.stateData.variables("COUNTER_CURSOR") shouldBe "2"
  }

  it should "stop iteration when cursor reaches end" in {
    val data = AnalyzersData(
      stateData = StateVariables(variables = Map(
        "ITEMS_0" -> "first", "ITEMS_1" -> "second",
        "COUNTER_CURSOR" -> "2", "COUNTER_ENDIDX" -> "2"
      ))
    )
    val analyzer = new DefaultAnalyzer(
      """iterateOnVariables("countersPattern=COUNTER", "variablesPattern=ITEMS", "destinationVarname=CURRENT")""",
      restrictedArgs
    )
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 0.0
  }

  it should "iterate in descending order" in {
    val data = AnalyzersData(
      stateData = StateVariables(variables = Map(
        "ITEMS_0" -> "first", "ITEMS_1" -> "second", "ITEMS_2" -> "third"
      ))
    )
    val analyzer = new DefaultAnalyzer(
      """iterateOnVariables("countersPattern=COUNTER", "variablesPattern=ITEMS", "destinationVarname=CURRENT", "order=desc")""",
      restrictedArgs
    )
    val result = analyzer.evaluate("test", data)
    result.score shouldBe 1.0
    result.data.stateData.variables("CURRENT") shouldBe "third"
  }

  // --- MatchDateDDMMYYYYAtomic ---

  "matchDateDDMMYYYY" should "extract a date from a query" in {
    val data = AnalyzersData()
    val analyzer = new DefaultAnalyzer("""matchDateDDMMYYYY("d_")""", restrictedArgs)
    val result = analyzer.evaluate("the date is 25/12/2020 in the message", data)
    result.score shouldBe 1.0
    result.data.stateData.variables("d_day.0") shouldBe "25"
    result.data.stateData.variables("d_month.0") shouldBe "12"
    result.data.stateData.variables("d_year.0") shouldBe "2020"
  }

  it should "extract date with dash separator" in {
    val data = AnalyzersData()
    val analyzer = new DefaultAnalyzer("""matchDateDDMMYYYY("d_")""", restrictedArgs)
    val result = analyzer.evaluate("date: 01-06-2019", data)
    result.score shouldBe 1.0
    result.data.stateData.variables("d_day.0") shouldBe "01"
    result.data.stateData.variables("d_month.0") shouldBe "06"
    result.data.stateData.variables("d_year.0") shouldBe "2019"
  }

  it should "extract date with dot separator" in {
    val data = AnalyzersData()
    val analyzer = new DefaultAnalyzer("""matchDateDDMMYYYY("d_")""", restrictedArgs)
    val result = analyzer.evaluate("date: 15.03.2021", data)
    result.score shouldBe 1.0
    result.data.stateData.variables("d_day.0") shouldBe "15"
    result.data.stateData.variables("d_month.0") shouldBe "03"
    result.data.stateData.variables("d_year.0") shouldBe "2021"
  }

  it should "return 0.0 when no date is found" in {
    val data = AnalyzersData()
    val analyzer = new DefaultAnalyzer("""matchDateDDMMYYYY("d_")""", restrictedArgs)
    val result = analyzer.evaluate("no date here", data)
    result.score shouldBe 0.0
  }
}
