package plaid.ast
import java.lang

case class Num(num: Int) extends PreludeExpression{
  override def prettyPrint() : String = num.toString
}
