package plaid.ast
import scala.jdk.CollectionConverters._

case class Program(
                    commandFunctions: java.util.List[CommandFunction],
                    exprFunctions: java.util.List[ExprFunction],
                    constraintFunctions: java.util.List[ConstraintFunction],
                    precondition: Constraint,
                    postcondition: Constraint) extends Node{

  def resolveExprFunction(functionName : Expr):ExprFunction = {
    exprFunctions.asScala.find(x => x.fname == functionName).get
  }

  def resolveCommandFunction(functionName: Expr): CommandFunction = {
    commandFunctions.asScala.find(x => x.fname.equals(functionName)).get
  }


  def resolveConstraintFunction(functionName: Expr): ConstraintFunction = {
    constraintFunctions.asScala.find(x => x.id.equals(functionName)).get
  }



}