package plaid.ast
import java.lang

case class SecretExpr(getE: PreludeExpression) extends MemoryExpr{
  override def prettyPrint(): String = "s[\"" + getE.prettyPrint() + "\"]"
}