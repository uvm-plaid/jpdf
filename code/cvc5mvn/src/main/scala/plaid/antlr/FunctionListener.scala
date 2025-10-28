package plaid.antlr

import plaid.PreludeBaseListener
import plaid.PreludeParser.{CommandFuncContext, ConstraintFuncContext, ExprFuncContext, IdentContext, TypedIdentContext}
import plaid.ast.*

import scala.jdk.CollectionConverters.*

class FunctionListener extends PreludeBaseListener {

  val exprFunctions = scala.collection.mutable.ListBuffer[ExprFunction]()
  val commandFunctions = scala.collection.mutable.ListBuffer[CommandFunction]()
  val constraintFunctions = scala.collection.mutable.ListBuffer[ConstraintFunction]()

  private def toIdentifiers(contexts: java.util.List[IdentContext]): List[Identifier] =
    contexts.asScala.drop(1).map(ctx => Identifier(ctx.getText)).toList

  private def toTypedIdentifiers(contexts: java.util.List[TypedIdentContext]): List[TypedIdentifier] =
    contexts.asScala.map { ctx =>
      TypedIdentifier(Identifier(ctx.ident().getText), Load.typeMarker(ctx.`type`()))
    }.toList

  override def enterExprFunc(ctx: ExprFuncContext): Unit = {
    exprFunctions += ExprFunction(
      Identifier(ctx.ident(0).getText), // function name
      toIdentifiers(ctx.ident()),           // parameters
      Load.expression(ctx.expr())       // body
    )
  }

  override def enterCommandFunc(ctx: CommandFuncContext): Unit = {
    val precondition =
      Option(ctx.precondsection()).map(_.constraintExpr()).map(Load.constraint).orNull
    val postcondition =
      Option(ctx.postcondsection()).map(_.constraintExpr()).map(Load.constraint).orNull

    commandFunctions += CommandFunction(
      Identifier(ctx.ident().getText),
      toTypedIdentifiers(ctx.typedIdent()),
      Load.command(ctx.command()),
      precondition,
      postcondition
    )
  }

  override def enterConstraintFunc(ctx: ConstraintFuncContext): Unit = {
    constraintFunctions += ConstraintFunction(
      Identifier(ctx.ident(0).getText),
      toIdentifiers(ctx.ident()),
      Load.constraint(ctx.constraintExpr())
    )
  }
}
