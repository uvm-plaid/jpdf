package plaid.ast
import scala.jdk.CollectionConverters._

case class Program(
                    commandFunctions: java.util.List[CommandFunction],
                    exprFunctions: java.util.List[ExprFunction],
                    constraintFunctions: java.util.List[ConstraintFunction],
                    precondition: ConstraintExpr,
                    postcondition: ConstraintExpr) extends Node{

  def resolveExprFunction(functionName : PreludeExpression):ExprFunction = {
    exprFunctions.asScala.find(x => x.fname == functionName).get
  }

  def resolveCommandFunction(functionName: PreludeExpression): CommandFunction = {
    commandFunctions.asScala.find(x => x.fname.equals(functionName)).get
  }


  def resolveConstraintFunction(functionName: PreludeExpression): ConstraintFunction = {
    constraintFunctions.asScala.find(x => x.id.equals(functionName)).get
  }



}