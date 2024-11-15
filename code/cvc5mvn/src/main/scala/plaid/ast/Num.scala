package plaid.ast
import java.lang

case class Num(getNum: Int) extends PreludeExpression{
  override def prettyPrint() : String = getNum.toString
}
