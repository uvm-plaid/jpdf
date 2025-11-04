package plaid.prelude.antlr

import plaid.prelude.PreludeBaseListener
import plaid.prelude.PreludeParser.{CmdFuncContext, ConstraintFuncContext, ExprFuncContext, IdentContext, TypedIdentContext}
import plaid.prelude.ast.*

import scala.jdk.CollectionConverters.*

// TODO We can just get these straight from the AST...

class FunctionListener extends PreludeBaseListener {

  val exprFunctions = scala.collection.mutable.ListBuffer[ExprFunc]()
  val commandFunctions = scala.collection.mutable.ListBuffer[CmdFunc]()
  val constraintFunctions = scala.collection.mutable.ListBuffer[ConstraintFunc]()

  private def toIdentifiers(contexts: java.util.List[IdentContext]): List[Identifier] =
    contexts.asScala.drop(1).map(ctx => Identifier(ctx.getText)).toList

  private def toTypedIdentifiers(contexts: java.util.List[TypedIdentContext]): List[TypedIdentifier] =
    contexts.asScala.map { ctx =>
      TypedIdentifier(Identifier(ctx.ident().getText), Loader.typeMarker(ctx.`type`()))
    }.toList

  override def enterExprFunc(ctx: ExprFuncContext): Unit = {
    exprFunctions += ExprFunc(
      Identifier(ctx.ident(0).getText), // function name
      toIdentifiers(ctx.ident()),           // parameters
      Loader.expression(ctx.expr())       // body
    )
  }

  override def enterCmdFunc(ctx: CmdFuncContext): Unit =
    commandFunctions += CmdFunc(
      Identifier(ctx.ident().getText),
      toTypedIdentifiers(ctx.typedIdent()),
      body = ctx.cmd().asScala.map(Loader.command).toList,
      precond = Option(ctx.pre()).map(x => Loader.constraint(x.constraint())),
      postcond = Option(ctx.post()).map(x => Loader.constraint(x.constraint())))

  override def enterConstraintFunc(ctx: ConstraintFuncContext): Unit = {
    constraintFunctions += ConstraintFunc(
      Identifier(ctx.ident(0).getText),
      toIdentifiers(ctx.ident()),
      Loader.constraint(ctx.constraint())
    )
  }
}
