package plaid.prelude.ast

import scala.jdk.CollectionConverters._

// TODO Extension methods for Program

sealed trait Node

case class CommandFunction(fname: Identifier, typedVariables: List[TypedIdentifier], c: Cmd, precond: Constraint, postcond: Constraint) extends Node
case class ConstraintFunction(id: Identifier, params: List[Identifier], body: Constraint) extends Node
case class ExprFunction(fname: Identifier, y: List[Identifier], e: Expr) extends Node
case class TypedIdentifier(y: Identifier, t: Type) extends Node

case class Program(
                    commandFunctions: List[CommandFunction],
                    exprFunctions: List[ExprFunction],
                    constraintFunctions: List[ConstraintFunction]) extends Node {

  def resolveExprFunction(functionName: Expr): ExprFunction = {
    exprFunctions.find(x => x.fname == functionName).get
  }

  def resolveCommandFunction(functionName: Expr): CommandFunction = {
    commandFunctions.find(x => x.fname.equals(functionName)).get
  }


  def resolveConstraintFunction(functionName: Expr): ConstraintFunction = {
    constraintFunctions.find(x => x.id.equals(functionName)).get
  }
}

sealed trait Cmd extends Node
case class AssertCmd(e1: Expr, e2: Expr, e3: Expr) extends Cmd
case class AssignCmd(e1: Expr, e2: Expr) extends Cmd
case class ListCmd(c1: Cmd, c2: Cmd) extends Cmd
case class CallCmd(fn: Identifier, parms: List[Expr]) extends Cmd
case class LetCmd(y: Identifier, e: Expr, c: Cmd) extends Cmd

sealed trait Constraint extends Node
case class AndConstraint(e1: Constraint, e2: Constraint) extends Constraint
case class CallConstraint(fn: Identifier, parms: List[Expr]) extends Constraint
case class EqualConstraint(e1: Expr, e2: Expr) extends Constraint
case class NotConstraint(e: Constraint) extends Constraint
case class TrueConstraint() extends Constraint

sealed trait Expr extends Node
case class AtExpr(e1: Expr, e2: Expr) extends Expr
case class ConcatExpr(e1: Expr, e2: Expr) extends Expr
case class FieldExpr(elements: java.util.TreeMap[Identifier, Expr]) extends Expr
case class FieldSelectExpr(e: Expr, l: Identifier) extends Expr
case class CallExpr(fn: Identifier, parms: List[Expr]) extends Expr
case class LetExpr(y: Identifier, e1: Expr, e2: Expr) extends Expr
case class MessageExpr(e: Expr) extends Expr
case class RandomExpr(e: Expr) extends Expr
case class MinusExpr(e: Expr) extends Expr
case class Num(num: Int) extends Expr
case class OTExpr(e1: Expr, i1: Expr, e2: Expr, e3: Expr) extends Expr
case class OTFourExpr(s1: Expr, s2: Expr, i1: Expr, e1: Expr, e2: Expr, e3: Expr, e4: Expr) extends Expr
case class OutputExpr() extends Expr
case class PlusExpr(e1: Expr, e2: Expr) extends Expr
case class PublicExpr(e: Expr) extends Expr
case class SecretExpr(e: Expr) extends Expr
case class Str(str: String) extends Expr
case class TimesExpr(e1: Expr, e2: Expr) extends Expr
case class Identifier(name: String) extends Expr, java.lang.Comparable[Identifier] {
  override def compareTo(other: Identifier): Int = name.compareTo(other.name)
}

// TODO Do we need PartyIndexType?

sealed trait Type extends Node
case class PartyIndexType() extends Type
case class RecordType(elements: java.util.TreeMap[Identifier, Type]) extends Type
case class StringType() extends Type
