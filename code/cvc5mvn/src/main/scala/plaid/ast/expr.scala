package plaid.ast

// TODO FunctionCallExpr, Identifier maybe should be separate for constraints?

sealed trait Expr extends Node
case class AtExpr(e1: Expr, e2: Expr) extends Expr
case class ConcatExpr(e1: Expr, e2: Expr) extends Expr
case class FieldExpr(elements: java.util.TreeMap[Identifier, Expr]) extends Expr
case class FieldSelectExpr(e: Expr, l: Identifier) extends Expr
case class FunctionCall(fname: Identifier, parameters: java.util.List[Expr]) extends Expr, Constraint
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

case class Identifier(name: String) extends Expr, Constraint, java.lang.Comparable[Identifier] {
  override def compareTo(other: Identifier): Int = name.compareTo(other.name)
}
