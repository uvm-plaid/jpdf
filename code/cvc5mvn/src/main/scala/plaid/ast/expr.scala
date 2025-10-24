package plaid.ast

trait PreludeExpression extends Node
case class AtExpr(e1: PreludeExpression, e2: PreludeExpression) extends PreludeExpression
case class ConcatExpr(e1: PreludeExpression, e2: PreludeExpression) extends PreludeExpression
case class FieldExpr(elements: java.util.TreeMap[Identifier, PreludeExpression]) extends PreludeExpression
case class FieldSelectExpr(e: PreludeExpression, l: Identifier) extends PreludeExpression
case class FunctionCallExpr(fname: Identifier, parameters: java.util.List[PreludeExpression]) extends PreludeExpression, ConstraintExpr
case class LetExpr(y: Identifier, e1: PreludeExpression, e2: PreludeExpression) extends PreludeExpression
case class MinusExpr(e: PreludeExpression) extends PreludeExpression
case class Num(num: Int) extends PreludeExpression
case class OTExpr(e1: PreludeExpression, i1: PreludeExpression, e2: PreludeExpression, e3: PreludeExpression) extends PreludeExpression
case class OTFourExpr(s1: PreludeExpression, s2: PreludeExpression, i1: PreludeExpression, e1: PreludeExpression, e2: PreludeExpression, e3: PreludeExpression, e4: PreludeExpression) extends PreludeExpression
case class PlusExpr(e1: PreludeExpression, e2: PreludeExpression) extends PreludeExpression
case class Str(str: String) extends PreludeExpression
case class TimesExpr(e1: PreludeExpression, e2: PreludeExpression) extends PreludeExpression
