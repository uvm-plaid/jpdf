package plaid.antlr

import plaid.PreludeBaseVisitor
import plaid.PreludeParser.*
import plaid.ast.*

import scala.jdk.CollectionConverters.*

object CommandVisitor extends PreludeBaseVisitor[Cmd] {

  override def visitFunctionCallCommand(ctx: FunctionCallCommandContext): CallCmd =
    CallCmd(
      Identifier(ctx.ident().getText),
      ctx.expr().asScala.map(Loader.toExpression).toList.asJava
    )

  override def visitAssignCommand(ctx: AssignCommandContext): AssignCmd =
    AssignCmd(
      Loader.toExpression(ctx.expr(0)),
      Loader.toExpression(ctx.expr(1))
    )

  override def visitAssertCommand(ctx: AssertCommandContext): AssertCmd =
    AssertCmd(
      Loader.toExpression(ctx.expr(0)),
      Loader.toExpression(ctx.expr(1)),
      Loader.toExpression(ctx.expr(2))
    )

  override def visitCommandList(ctx: CommandListContext): ListCmd =
    ListCmd(
      visit(ctx.command(0)),
      visit(ctx.command(1))
    )

  override def visitLetCommand(ctx: LetCommandContext): LetCmd =
    LetCmd(
      Identifier(ctx.ident().getText),
      Loader.toExpression(ctx.expr()),
      Loader.toCommand(ctx.command())
    )
}
