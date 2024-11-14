package plaid.ast
import java.{lang, util}
import scala.jdk.CollectionConverters._

case class Program(getCommandFunctions: java.util.List[CommandFunction], getExprFunctions: java.util.List[ExprFunction]) extends Node{
  override def children(): lang.Iterable[Node] = new util.ArrayList[Node]()

  override def prettyPrint(): String = throw new UnsupportedOperationException()

  def resolveExprFunction(functionName : PreludeExpression):ExprFunction = {
    getExprFunctions.asScala.find(x => x.getFname == functionName).get
  }

  def resolveCommandFunction(functionName: PreludeExpression): CommandFunction = {
    getCommandFunctions.asScala.find(x => x.getFname.equals(functionName)).get
  }
}