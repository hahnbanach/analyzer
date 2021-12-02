package com.hahnbanach.analyzer.atoms

import cats.implicits._
import com.hahnbanach.analyzer.entities.{AnalyzersData, Result}

import scala.util.matching.Regex

/**
 * Created by angelo.leto@elegans.io on 25/11/20.
 */

/** iterates over a set of variables and store the value into another destination variable.
 * @param arguments: the list of arguments
 *  countersPattern: the pattern for the cursor and the end of iteration
 *    - The cursor will be called <countersPattern>_CURSOR
 *    - the value to end the iteration will be called: <countersPattern>_ENDIDX
 *    - If the counter variable exists and reached the limit then the iteration doesn't continue
 *    - If the counter variable doesn't exists is initialized counting the variables through pattern
 *    - if the variable exists and didn't reach the limit the iteration continue
 *    - In order to eventually reset the iteration after reaching the limit is enough to delete the cursorVarname
 *  variablesPattern: the prefix of the variables to iterate, it must have the format: <pattern>_<positiveinteger>
 *    - The atom doesn't work if there are holes in the sequence
 *    - that the pattern matches the regular expression: "<variablesPattern>_[0-9]+".
 *    - For example if the variables are called like PATTERN_0,..,PATTERN_10 the pattern is "PATTERN" without `_`
 *    - The default value for it is "A__TEMP__.VARIABLES_ITERATION_VALUE" which means that
 *      the result of the iteration will be stored on a variable like: A__TEMP__.VARIABLES_ITERATION_VALUE=<value>
 *  destinationVarname: the destination variable which will contain the value under the cursor
 *    - If the iteration return 0 as a score, the variable will not exists in the variable map
 *  order: specify of the iteration order is from the bottom -> init or viceversa
 *    - The admitted values are: "desc" or "asc" (default)
 * @param restrictedArgs: the list of variables not exposed to the analyzers interface
 */
class IterateOnVariablesAtomic(val arguments: List[String], restrictedArgs: Map[String, String]) extends AbstractAtomic {
  override def toString: String = "IterateOnVariablesAtomic"
  val isEvaluateNormalized: Boolean = true
  private[this] val usage: String =
    "countersPattern=<variables prefix>, variablesPattern=<variables prefix>, destinationVarname=<dest. variable name>," +
      "[order=\"asc\"|\"desc\"]"

  private[this] val parameters: Map[String, String] =
    arguments.map(_.split("=", 2))
      .filter(_.length === 2).map {
      case Array(v1,v2) => (v1,v2)
      case _ => throw ExceptionAtomic(s"$toString: bad parameter: $arguments")
    }.toMap

  private[this] val countersPattern: String = parameters.get("countersPattern") match {
    case Some(t) => t
    case _ => throw ExceptionAtomic(s"$toString: `countersPattern` argument is mandatory ($usage)")
  }

  private[this] val destinationVarname: String = parameters.get("destinationVarname") match {
    case Some(t) => t
    case _ => "A__TEMP__.VARIABLES_ITERATION_VALUE"
  }

  private[this] val variablesPattern: String = parameters.get("variablesPattern") match {
    case Some(t) => t
    case _ => throw ExceptionAtomic(s"$toString: `variablesPattern` argument is mandatory ($usage)")
  }

  private[this] val counterIncrement: Int = parameters.get("order") match {
    case Some(t) => t match {
      case "desc" => -1
      case _ => 1
    }
    case _ => 1
  }

  private[this] val variableNamePatternRegex: Regex = "^([A-Za-z-._0-9]+)_([0-9]+)$".r

  private[this] val cursorVarname = s"${countersPattern}_CURSOR"
  private[this] val endIdxVarname = s"${countersPattern}_ENDIDX"
  /** evaluate the atom and set the destination variable
   *
   * @param query the user query
   * @param data the dictionary of variables
   * @return Result with 1.0 if the variable exists score = 0.0 otherwise
   */
  def evaluate(query: String, data: AnalyzersData = AnalyzersData()): Result = {
    val runtimeVariables: Map[String, String] = data.stateData.variables
    //fetch the counter value and if not initialized, initialize it
    val (currCounterValue, endIteration) =
      (runtimeVariables.get(cursorVarname), runtimeVariables.get(endIdxVarname)) match {
        case (Some(cursorVarname), Some(endIdxVarname)) =>
          (cursorVarname.toInt, endIdxVarname.toInt)
        case (_, _) =>
          //count the variables and initialize the endIteration variable
          val selectedvariablesCount = runtimeVariables
            .filter(variablesPair => variablesPair._1.matches(s"${variablesPattern}_[0-9]+"))
            .map(value => {
              val variableNamePatternRegex(_, orderNumber) = value._1
              (value, orderNumber.toInt)
            }).size
          if(selectedvariablesCount > 0) {
            if(counterIncrement === 1) {
              (0, selectedvariablesCount)
            } else {
              (selectedvariablesCount - 1, -1)
            }
          } else {
            (-1, -1) // prevent iteration
          }
      }

    val (score: Double, updatedVariableMap: Map[String, String]) = {
      if(currCounterValue > -1 &&
        ((counterIncrement === -1 && currCounterValue > endIteration) ||
          (counterIncrement === 1 && currCounterValue < endIteration))
      ) {
        val variableKey = s"${variablesPattern}_$currCounterValue"
        val newCounterValue = currCounterValue + counterIncrement
        runtimeVariables.get(variableKey) match {
          case Some(variableValue) =>
            (1.0d,
              runtimeVariables + (destinationVarname -> variableValue) +
                (cursorVarname -> newCounterValue.toString) +
                (endIdxVarname -> endIteration.toString)
            )
          case _ =>
            (0.0d, runtimeVariables - destinationVarname - cursorVarname - endIdxVarname)
        }
      } else {
        (0.0d, runtimeVariables - destinationVarname - cursorVarname - endIdxVarname)
      }
    }

    Result(score = score,
      data = data.copy(
        stateData = data.stateData.copy(variables = updatedVariableMap)
      )
    )
  }
}
