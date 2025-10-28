package plaid.antlr

import plaid.PreludeBaseListener
import plaid.PreludeParser.{CommandFuncContext, ConstraintFuncContext, ExprFuncContext, IdentContext, TypedIdentContext}
import plaid.ast.*

import scala.jdk.CollectionConverters.*

class FunctionListener extends PreludeBaseListener {

  private val exprFunctions = scala.collection.mutable.ListBuffer[ExprFunction]()
  private val commandFunctions = scala.collection.mutable.ListBuffer[CommandFunction]()
  private val constraintFunctions = scala.collection.mutable.ListBuffer[ConstraintFunction]()

  def getExprFunctions: java.util.List[ExprFunction] = exprFunctions.toList.asJava
  def getCommandFunctions: java.util.List[CommandFunction] = commandFunctions.toList.asJava
  def getConstraintFunctions: java.util.List[ConstraintFunction] = constraintFunctions.toList.asJava

  private def toIdentifiers(contexts: java.util.List[IdentContext]): List[Identifier] =
    contexts.asScala.drop(1).map(ctx => Identifier(ctx.getText)).toList

  private def toTypedIdentifiers(contexts: java.util.List[TypedIdentContext]): List[TypedIdentifier] =
    contexts.asScala.map { ctx =>
      TypedIdentifier(Identifier(ctx.ident().getText), Loader.toType(ctx.`type`()))
    }.toList

  override def enterExprFunc(ctx: ExprFuncContext): Unit = {
    exprFunctions += ExprFunction(
      Identifier(ctx.ident(0).getText), // function name
      toIdentifiers(ctx.ident()).asJava,           // parameters
      Loader.toExpression(ctx.expr())       // body
    )
  }

  override def enterCommandFunc(ctx: CommandFuncContext): Unit = {
    val precondition =
      Option(ctx.precondsection()).map(_.constraintExpr()).map(Loader.toConstraintExpression).orNull
    val postcondition =
      Option(ctx.postcondsection()).map(_.constraintExpr()).map(Loader.toConstraintExpression).orNull

    commandFunctions += CommandFunction(
      Identifier(ctx.ident().getText),
      toTypedIdentifiers(ctx.typedIdent()).asJava,
      Loader.toCommand(ctx.command()),
      precondition,
      postcondition
    )
  }

  override def enterConstraintFunc(ctx: ConstraintFuncContext): Unit = {
    constraintFunctions += ConstraintFunction(
      Identifier(ctx.ident(0).getText),
      toIdentifiers(ctx.ident()).asJava,
      Loader.toConstraintExpression(ctx.constraintExpr())
    )
  }
}
