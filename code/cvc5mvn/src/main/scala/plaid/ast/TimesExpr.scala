package plaid.ast
import java.lang


case class TimesExpr(getE1: PreludeExpression, getE2: PreludeExpression) extends PreludeExpression{
  override def prettyPrint(): String = "(" + getE1.prettyPrint() + "*" + getE2.prettyPrint() + ")"
}
