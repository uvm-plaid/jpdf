package plaid.ast
import scala.jdk.CollectionConverters._

// TODO Why is TypedIdentifier a Node?
// TODO Extension methods for Program

trait Node

case class CommandFunction(fname: Identifier, typedVariables: java.util.List[TypedIdentifier], c: Cmd, precond: Constraint, postcond: Constraint) extends Node
case class ConstraintFunction(id: Identifier, params: java.util.List[Identifier], body: Constraint) extends Node
case class ExprFunction(fname: Identifier, y: java.util.List[Identifier], e: Expr) extends Node
case class TypedIdentifier(y: Identifier, t: Type) extends Node

case class Program(
                    commandFunctions: java.util.List[CommandFunction],
                    exprFunctions: java.util.List[ExprFunction],
                    constraintFunctions: java.util.List[ConstraintFunction],
                    precondition: Constraint,
                    postcondition: Constraint) extends Node {

  def resolveExprFunction(functionName: Expr): ExprFunction = {
    exprFunctions.asScala.find(x => x.fname == functionName).get
  }

  def resolveCommandFunction(functionName: Expr): CommandFunction = {
    commandFunctions.asScala.find(x => x.fname.equals(functionName)).get
  }


  def resolveConstraintFunction(functionName: Expr): ConstraintFunction = {
    constraintFunctions.asScala.find(x => x.id.equals(functionName)).get
  }
}
