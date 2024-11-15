package plaid.ast
import java.lang

case class Str(getStr: String) extends PreludeExpression{
  override def prettyPrint(): String = getStr
}
