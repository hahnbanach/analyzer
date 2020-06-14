package com.getjenny.analyzer.analyzers

/**
  * Created by mal on 20/02/2017.
  */

import com.getjenny.analyzer.atoms._
import com.getjenny.analyzer.expressions.{AnalyzersDataInternal, Expression, Result}
import com.getjenny.analyzer.interfaces.{AtomicFactoryTrait, OperatorFactoryTrait}
import com.getjenny.analyzer.operators._

import scala.util.control.NonFatal
import scala.util.Try
import scalaz.Scalaz._

/**
  * All sentences with more than 22 characters and with keywords "password" and either "lost" or "forgot"
  *
  * and(regex(".{22,}"), and(or(keyword("forgot"), keyword("lost")), keyword("password")))
  *
  * but also:
  *
  * or(similar("lost password"), similar("forgot password"))
  *
  * In the latter case "or" is treated as disjunction of probabilities
  */
abstract class DefaultParser(command: String, restrictedArgs: Map[String, String]) extends AbstractParser(command: String) {

  val atomicFactory: AtomicFactoryTrait[List[String], AbstractAtomic, Map[String, String]]
  val operatorFactory: OperatorFactoryTrait[List[Expression], AbstractOperator]

  private[this] val operator = gobbleCommands(command)

  override def toString: String = operator.toString
  /** Read a sentence and produce a score (the higher, the more confident)
    */
  def evaluate(sentence: String, data: AnalyzersDataInternal = AnalyzersDataInternal()): Result = {
    val res = operator.evaluate(query = sentence, data = data)
    //if (res.score > 0) println("DEBUG: DefaultParser: '" + this + "' evaluated to " + res)
    res
  }

  def firstOccurrenceOfOperator(operatorName:String):Option[Expression] = {
    operator.findOccurrencesOfOperator(operatorName).headOption
  }
  /**Produces nested operators
    *
    * e.g.
    *
    * gobbleCommands("""boolean-and(regex(".{22,}"), boolean-and(boolean-or(keyword("forgot"), keyword("lost")), keyword("password")))""")
    *
    * will produce a boolean OR operator with inside a regex Expression (which can be evaluated),
    * a boolean AND etc
    *
    */
  def gobbleCommands(commands: String): AbstractOperator = {

    /** \( does not count, \\( does
      */
    def escapeChar(chars: List[Char], i: Int): Boolean = chars(i-1) === '\\' && chars(i-2) =/= '\\'

    @scala.annotation.tailrec
    def loop(chars: List[Char], index: Int, parenthesisBalance: List[Int], quoteBalance: Int,
             commandBuffer: String, argumentBuffer: String, arguments: List[String],
             commandTree: AbstractOperator):  AbstractOperator = {
      if (index >= chars.length && chars.nonEmpty) {
        if (quoteBalance < 0)
          throw AnalyzerParsingException("Parsing error: quotes are not balanced")
        else if (parenthesisBalance.sum =/= 0)
          throw AnalyzerParsingException("Parsing error: parenthesis are not balanced")
        else
          commandTree
      } else {
        val justOpenedParenthesis = chars(index) === '(' && !escapeChar(chars, index) && quoteBalance === 0
        val justClosedParenthesis = chars(index) === ')' && !escapeChar(chars, index) && quoteBalance === 0
        //println("DEBUG justOpenedParenthesis " + justOpenedParenthesis)
        //println("DEBUG justClosedParenthesis " + justClosedParenthesis)

        val newParenthesisBalance: List[Int] = {
          // if a parenthesis is inside double quotes does not count
          if (justOpenedParenthesis) 1 :: parenthesisBalance
          else if (justClosedParenthesis) -1 :: parenthesisBalance
          else parenthesisBalance
        }
        //println("DEBUG newParenthesisBalance " + newParenthesisBalance)

        // new_quote_balance > 0 if text in a quotation
        val justOpenedQuote = chars(index) === '"' && !escapeChar(chars, index) && quoteBalance === 0
        val justClosedQuote = chars(index) === '"' && !escapeChar(chars, index) && quoteBalance === 1
        val newQuoteBalance: Int = {
          if (justOpenedQuote) 1
          else if (justClosedQuote) 0
          else quoteBalance
        }

        if (newParenthesisBalance.sum < 0 || newQuoteBalance < 0)
          throw AnalyzerParsingException("Parsing error: quotes or parenthesis do not match")

        // Start reading the command
        // If not in quotation and have letter, add to command string accumulator
        // Then, if a parenthesis opens put the string in command
        val newCommandBuffer = if ((chars(index).isLetter || chars(index).isWhitespace) && newQuoteBalance === 0) {
          (commandBuffer + chars(index)).filter(c => !c.isWhitespace)
        } else if (justClosedQuote) ""
        else commandBuffer.filter(c => !c.isWhitespace)

        // Now read the argument of the command
        // If inside last parenthesis was opened and quotes add to argument
        val argumentAcc =
        if (newParenthesisBalance.headOption.getOrElse(0) === 1 && newQuoteBalance === 1 && !justOpenedQuote) {
          argumentBuffer + chars(index)
        } else {
          ""
        }

        if (justOpenedParenthesis && operatorFactory.operations(commandBuffer)) {
          // We have just read an operator.
          //println("DEBUG Adding the operator " + commandBuffer)
          val operator = Try(operatorFactory.get(commandBuffer, List())) recover {
            case e: NoSuchElementException =>
              throw AnalyzerCommandException("Operator does not exists(" + commandBuffer + ")", e)
            case NonFatal(e) =>
              throw AnalyzerCommandException("Unknown error with operator(" + commandBuffer + ")", e)
          }
          loop(chars, index + 1, newParenthesisBalance, newQuoteBalance, "", argumentAcc,
            arguments,
            commandTree.add(operator.get, newParenthesisBalance.sum - 1))
        } else if (!atomicFactory.operations(commandBuffer) && !operatorFactory.operations(commandBuffer) &&
          newParenthesisBalance.headOption.getOrElse(0) === 1 && justOpenedParenthesis) {
          throw AnalyzerCommandException("Atomic or Operator does not exists(" + commandBuffer + ")")
        } else if (atomicFactory.operations(commandBuffer) && newParenthesisBalance.head === 1 && !justClosedQuote && quoteBalance === 1) {
          // We are reading an atomic's argument...
          //println("DEBUG calling loop, without adding an atom, with this command buffer: " + commandBuffer + " and argumentAcc: " + argumentAcc)
          loop(chars, index + 1, newParenthesisBalance, newQuoteBalance, commandBuffer,
            argumentAcc, arguments, commandTree)
        } else if (atomicFactory.operations(commandBuffer) && justClosedParenthesis) {
          // We have read all the atomic's arguments, add the atomic to the tree
          //println("DEBUG Calling loop, adding the atom: " + commandBuffer + ", w/ arguments" + arguments)
          val atomic = Try(atomicFactory.get(commandBuffer, arguments, restrictedArgs)) recover {
            case e: NoSuchElementException =>
              throw AnalyzerCommandException("Atomic does not exists(" + commandBuffer + ")", e)
            case NonFatal(e) =>
              throw AnalyzerCommandException("Unknown error with Atomic(" + commandBuffer + ")", e)
          }
          loop(chars, index + 1, newParenthesisBalance, newQuoteBalance, "",
            "", List.empty[String], commandTree.add(atomic.get, newParenthesisBalance.sum))
        } else if (atomicFactory.operations(commandBuffer) && justClosedQuote && !justClosedParenthesis) {
          // We have read atomic's argument, add the argument to the list
          //println("DEBUG Calling loop, adding argument: " + commandBuffer + " <- " + argumentBuffer)
          loop(chars, index + 1, newParenthesisBalance, newQuoteBalance, commandBuffer,
            argumentAcc, arguments ::: List(argumentBuffer), commandTree)
        } else {
          //println("DEBUG going to return naked command tree... " + chars.length + " : " + newCommandBuffer)
          if (index < chars.length - 1) {
            loop(chars, index + 1, newParenthesisBalance, newQuoteBalance,
              newCommandBuffer, argumentAcc, arguments, commandTree)
          } else {
            if (newParenthesisBalance.sum === 0 && newQuoteBalance === 0) commandTree
            else throw AnalyzerParsingException("gobbleCommands: Parenthesis or quotes do not match")
          }
        }
      }
    }
    //adding 2 trailing spaces because we always make a check on char(i -2 )
    loop(chars = "  ".toList ::: commands.toList,
      index = 2,
      parenthesisBalance = List(0),
      quoteBalance = 0,
      commandBuffer = "",
      argumentBuffer = "",
      arguments = List.empty[String],
      commandTree = new ConjunctionOperator(List.empty[Expression])
    )

  }

} //end class
