package plaid.ast
import java.lang

case class RandomExpr(getE: PreludeExpression) extends MemoryExpr{
  override def prettyPrint(): String = "r[\"" + getE.prettyPrint() + "\"]"
}
