package plaid.ast
import java.lang

case class Str(str: String) extends PreludeExpression{
  override def prettyPrint(): String = str
}
