package com.search.preproccessing

import com.search.engine.InvertedIndex
import com.search.preproccessing.OperatorType.OperatorType

import scala.collection.mutable.ListBuffer

object OperatorType extends Enumeration {
  type OperatorType = Value;
  val and, or, not, nop = Value
}

class QueryParser(query: String) {

  private var current: Int = 0;
  val doc = new Doc(-1, query)
  private val tokens: List[String] = doc.cleanedLemmas(true)

  def isBinaryOperator(token: String) = token.equals("&") || token.equals("|")

  def parse = {
    parseBinary
  }

  def getBinOperator(): OperatorType = {
    if (current >= tokens.size)
      return OperatorType.nop
    if (tokens(current) == "&") {
      current += 1
      OperatorType.and
    }
    else if (tokens(current) == "|") {
      current += 1
      OperatorType.or
    }
    else
      OperatorType.and
  }

  def parseUnary : Expression = {
    if (current >= tokens.size || tokens(current) == "-rrb-")
      return null

    if (tokens(current) == "-lrb-") {
      current += 1 // open brackets
      var left = parseBinary()
      while (tokens(current) != "-rrb-" && current < tokens.size) {
        val operator = getBinOperator()
        val right = parseBinary()
        left = if (right != null)
          new BinExpression(left, operator, right)
        else left
      }
      current += 1 // close brackets
      return left
    }

    if (tokens(current) == "!") {
      current += 1
      val ex = new NotExpression(parseUnary)
      return ex
    }
    val expr = new UnExpression(tokens(current))
    current += 1
    expr
  }

  private def parseBinary(): Expression = {
    // keep in mind ')'
    val unExpr = parseUnary
    if (unExpr == null)
      return null
    val operator = getBinOperator()
    val right = parseBinary()
    if (right == null)
      unExpr
    else new BinExpression(unExpr, operator, right)

  }
}

trait Expression {
  def evalueateQuery(index: InvertedIndex): List[Int]
}

class UnExpression(token: String) extends Expression {
  override def evalueateQuery(index: InvertedIndex): List[Int] = {
    index.getTermPostings(token).toList
  }
}

class NotExpression(inner: Expression) extends Expression {
  override def evalueateQuery(index: InvertedIndex): List[Int] = {
    index.applyNot(inner.evalueateQuery(index))
  }
}

class BinExpression(left: Expression, operator: OperatorType, right: Expression) extends Expression {

  override def evalueateQuery(index: InvertedIndex): List[Int] = operator match {
    case OperatorType.nop => left.evalueateQuery(index)
    case OperatorType.or => index.mergePostingsWithOr(left.evalueateQuery(index), right.evalueateQuery(index))
    case OperatorType.and => index.mergePostingsWithAnd(left.evalueateQuery(index), right.evalueateQuery(index))
    case _ => throw new UnsupportedOperationException()
  }
}

// token = str
// un_expr = [!] '(' bin_expr ')' | '!' token
// bin_expr = un_expr [[operator] bin_expr]
// operator = '&' | '|'
