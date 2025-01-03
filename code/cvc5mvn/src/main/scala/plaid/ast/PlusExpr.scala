package plaid.ast
import java.lang

case class PlusExpr(e1: PreludeExpression, e2: PreludeExpression) extends PreludeExpression
