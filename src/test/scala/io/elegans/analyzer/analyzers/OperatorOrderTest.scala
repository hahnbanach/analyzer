package io.elegans.analyzer.analyzers

/**
  * Created by Angelo Leto <angelo.leto@elegans.io> on 30/07/20.
  */

import io.elegans.analyzer.entities.{AnalyzersDataInternal, DtHistoryItem, DtHistoryType, StateVariables}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class OperatorOrderTest extends AnyFlatSpec with Matchers {

  val restrictedArgs: Map[String, String] = Map.empty[String, String]
  val travStateId: String = "00dac6ee-80a1-4054-baac-747a80bb7738"
  val emailExtracted: String = "test1@example.com"
  val emailInQuery: String = "test2@example.com"
  val queryWithEmail: String = "my email is " + emailInQuery
  val queryNoEmail: String = "this query does not contain any email address!"
  val extractedVarKey = "aaa"
  val extractedVarValue = "bbb"
  val extractedEmailKey = "email_address.0"
  val extractedQueryKey = "customer_message.0"
  val data: AnalyzersDataInternal = AnalyzersDataInternal(
    stateData = StateVariables(
      traversedStates = Vector[DtHistoryItem](DtHistoryItem(state = travStateId, `type` = DtHistoryType.EXTERNAL)),
      variables = Map[String, String](extractedVarKey -> extractedVarValue, extractedEmailKey -> emailExtracted)
    )
  )
  val atomExtractEmail: String = """matchPatternRegex("[email_address](?:([a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+))")"""
  val atomExtractQuery: String = """matchPatternRegex("[customer_message](.*)")"""
  val atomLastTravStateTrue: String = """lastTravStateIs("""" + travStateId + """")"""
  val atomLastTravStateFalse: String = """lastTravStateIs("1234")"""
  def analyzerString(operator: String, operands: List[String]): String = operator + "(" + operands.mkString(",") + ")"
  val scoreSuccess: Double = 1.0d
  val scoreFailure: Double = 0.0d
  val scoreSuccessReinf1: Double = 1.1d
  val scoreSuccessReinf2: Double = scoreSuccessReinf1 * scoreSuccessReinf1
  val scoreSuccessReinf3: Double = scoreSuccessReinf2 * scoreSuccessReinf1

  val operatorBooleanAnd: String = "booleanAnd"
  val operatorBooleanOr: String = "booleanOr"
  val operatorBooleanNot: String = "booleanNot"
  val operatorConjunction: String = "conjunction"
  val operatorDisjunction: String = "disjunction"
  val operatorReinfConjunction: String = "reinfConjunction"
  val operatorMax: String = "max"
  val operatorBinarize: String = "binarize"

  operatorBooleanAnd should
    "trigger and extract email address when matching regex pattern" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorBooleanAnd, List(atomExtractEmail)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccess
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map.empty[String, Any]
  }
  it should "trigger and extract email address when matching regex pattern and lastTravStateIs is true" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorBooleanAnd,List(atomExtractEmail, atomLastTravStateTrue)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccess
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "trigger and extract email address when lastTravStateIs is true and matching regex pattern" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorBooleanAnd,List(atomLastTravStateTrue, atomExtractEmail)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccess
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "not trigger and not update email address when matching regex pattern and lastTravStateIs is false" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorBooleanAnd,List(atomExtractEmail, atomLastTravStateFalse)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreFailure
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailExtracted
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "not trigger and not update email address when lastTravStateIs is false and matching regex pattern" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorBooleanAnd,List(atomLastTravStateFalse, atomExtractEmail)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreFailure
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailExtracted
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "trigger and extract both email address and customer message" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorBooleanAnd,List(atomLastTravStateTrue, atomExtractEmail, atomExtractQuery)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccess
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.variables(extractedQueryKey) shouldBe queryWithEmail
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }

  operatorBooleanOr should
    "trigger and extract email address when matching regex pattern" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorBooleanOr, List(atomExtractEmail)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccess
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "trigger and extract email address when matching regex pattern and lastTravStateIs is false" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorBooleanOr,List(atomExtractEmail, atomLastTravStateFalse)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccess
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "trigger and extract email address when lastTravStateIs is false and matching regex pattern" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorBooleanOr,List(atomLastTravStateFalse, atomExtractEmail)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccess
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "trigger and extract both email address and customer message" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorBooleanOr,List(atomLastTravStateFalse, atomExtractEmail, atomExtractQuery)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccess
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.variables(extractedQueryKey) shouldBe queryWithEmail
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }

  operatorBooleanNot should
    "trigger and return previously extracted email when not matching regex pattern" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorBooleanNot, List(atomExtractEmail)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryNoEmail, data)
    analyzerValue.score shouldBe scoreSuccess
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailExtracted
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "not trigger and return previously extracted email when matching regex pattern" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorBooleanNot, List(atomExtractEmail)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreFailure
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailExtracted
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }

  operatorConjunction should
    "trigger and extract email address when matching regex pattern" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorConjunction, List(atomExtractEmail)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccess
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "trigger and extract email address when matching regex pattern and lastTravStateIs is true" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorConjunction,List(atomExtractEmail, atomLastTravStateTrue)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccess
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "trigger and extract email address when lastTravStateIs is true and matching regex pattern" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorConjunction,List(atomLastTravStateTrue, atomExtractEmail)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccess
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "not trigger and not update email address when matching regex pattern and lastTravStateIs is false" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorConjunction,List(atomExtractEmail, atomLastTravStateFalse)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreFailure
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailExtracted
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "not trigger and not update email address when lastTravStateIs is false and matching regex pattern" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorConjunction,List(atomLastTravStateFalse, atomExtractEmail)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreFailure
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailExtracted
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "trigger and extract both email address and customer message" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorConjunction,List(atomLastTravStateTrue, atomExtractEmail, atomExtractQuery)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccess
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.variables(extractedQueryKey) shouldBe queryWithEmail
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }

  operatorDisjunction should
    "trigger and extract email address when matching regex pattern" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorDisjunction, List(atomExtractEmail)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccess
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "trigger and extract email address when matching regex pattern and lastTravStateIs is false" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorDisjunction,List(atomExtractEmail, atomLastTravStateFalse)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccess
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "trigger and extract email address when lastTravStateIs is false and matching regex pattern" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorDisjunction,List(atomLastTravStateFalse, atomExtractEmail)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccess
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "trigger and extract both email address and customer message" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorDisjunction,List(atomLastTravStateFalse, atomExtractEmail, atomExtractQuery)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccess
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.variables(extractedQueryKey) shouldBe queryWithEmail
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }

  operatorReinfConjunction should
    "trigger and extract email address when matching regex pattern" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorReinfConjunction, List(atomExtractEmail)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccessReinf1
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "trigger and extract email address when matching regex pattern and lastTravStateIs is true" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorReinfConjunction,List(atomExtractEmail, atomLastTravStateTrue)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccessReinf2
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "trigger and extract email address when lastTravStateIs is true and matching regex pattern" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorReinfConjunction,List(atomLastTravStateTrue, atomExtractEmail)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccessReinf2
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "not trigger and not update email address when matching regex pattern and lastTravStateIs is false" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorReinfConjunction,List(atomExtractEmail, atomLastTravStateFalse)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreFailure
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailExtracted
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "not trigger and not update email address when lastTravStateIs is false and matching regex pattern" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorReinfConjunction,List(atomLastTravStateFalse, atomExtractEmail)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreFailure
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailExtracted
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "trigger and extract both email address and customer message" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorReinfConjunction,List(atomLastTravStateTrue, atomExtractEmail, atomExtractQuery)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccessReinf3
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.variables(extractedQueryKey) shouldBe queryWithEmail
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }

  operatorMax should
    "trigger and return variables passed as data" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorMax, List(atomLastTravStateTrue)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccess
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailExtracted
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "trigger and update email variable" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorMax, List(atomExtractEmail)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccess
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "trigger and update email variable in presence of multiple operands with lower score" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorMax, List(atomExtractEmail, atomLastTravStateFalse)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccess
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "trigger and update email variable in presence of multiple operands with lower score (reverse order)" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorMax, List(atomLastTravStateFalse, atomExtractEmail)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccess
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "trigger and update email variable in presence of multiple operands with score 1" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorMax, List(atomExtractEmail, atomLastTravStateTrue)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccess
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "trigger and update email variable in presence of multiple operands with score 1 (reverse order)" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorMax, List(atomLastTravStateTrue, atomExtractEmail)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccess
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "not trigger and not update email variable" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorMax, List(atomLastTravStateFalse)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreFailure
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailExtracted
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "trigger, update email variable and extract customer message" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorMax, List(atomExtractQuery, atomExtractEmail)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccess
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.variables(extractedQueryKey) shouldBe queryWithEmail
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "trigger and add to result only the variable from operand with higher score" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(
        operatorMax,
        List(
          atomExtractQuery,
          analyzerString(operatorReinfConjunction, List(atomExtractEmail))
        )
      ),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccessReinf1
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.variables.contains(extractedQueryKey) shouldBe false
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }

  operatorBinarize should
    "trigger and return variables passed as data" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorBinarize, List(atomLastTravStateTrue)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccess
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailExtracted
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "not trigger and return variables passed as data" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorBinarize, List(atomLastTravStateFalse)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreFailure
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailExtracted
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "trigger and update email address variable" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorBinarize, List(atomExtractEmail)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccess
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "not trigger and not update email address variable" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(operatorBinarize, List(atomExtractEmail)),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryNoEmail, data)
    analyzerValue.score shouldBe scoreFailure
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailExtracted
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
  it should "trigger, update email address variable, and return score 1" in {
    val analyzer = new DefaultAnalyzer(
      analyzerString(
        operatorBinarize,
        List(analyzerString(operatorReinfConjunction, List(atomExtractEmail)))
      ),
      restrictedArgs
    )
    val analyzerValue = analyzer.evaluate(queryWithEmail, data)
    analyzerValue.score shouldBe scoreSuccess
    analyzerValue.data.stateData.variables(extractedVarKey) shouldBe extractedVarValue
    analyzerValue.data.stateData.variables(extractedEmailKey) shouldBe emailInQuery
    analyzerValue.data.stateData.traversedStates.last.state shouldBe travStateId
    analyzerValue.data.data shouldBe Map()
  }
}
