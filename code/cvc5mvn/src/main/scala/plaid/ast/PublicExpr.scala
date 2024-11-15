package plaid.ast
import java.lang

case class PublicExpr(getE: PreludeExpression) extends MemoryExpr{
  override def prettyPrint(): String = "p[\"" + getE.toString + "\"]"
}
