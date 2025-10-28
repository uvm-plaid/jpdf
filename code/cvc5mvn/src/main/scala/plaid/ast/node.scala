package plaid.ast
import scala.jdk.CollectionConverters._

// TODO Why is TypedIdentifier a Node?
// TODO Extension methods for Program

trait Node

case class CommandFunction(fname: Identifier, typedVariables: List[TypedIdentifier], c: Cmd, precond: Constraint, postcond: Constraint) extends Node
case class ConstraintFunction(id: Identifier, params: List[Identifier], body: Constraint) extends Node
case class ExprFunction(fname: Identifier, y: List[Identifier], e: Expr) extends Node
case class TypedIdentifier(y: Identifier, t: Type) extends Node

case class Program(
                    commandFunctions: List[CommandFunction],
                    exprFunctions: List[ExprFunction],
                    constraintFunctions: List[ConstraintFunction]) extends Node {

  def resolveExprFunction(functionName: Expr): ExprFunction = {
    exprFunctions.find(x => x.fname == functionName).get
  }

  def resolveCommandFunction(functionName: Expr): CommandFunction = {
    commandFunctions.find(x => x.fname.equals(functionName)).get
  }


  def resolveConstraintFunction(functionName: Expr): ConstraintFunction = {
    constraintFunctions.find(x => x.id.equals(functionName)).get
  }
}
