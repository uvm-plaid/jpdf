package plaid.antlr

import plaid.PreludeBaseVisitor
import plaid.PreludeParser.*
import plaid.ast.*

import scala.jdk.CollectionConverters.*

object CommandVisitor extends PreludeBaseVisitor[Cmd] {

  override def visitFunctionCallCommand(ctx: FunctionCallCommandContext): CallCmd =
    CallCmd(
      Identifier(ctx.ident().getText),
      ctx.expr().asScala.map(Load.expression).toList
    )

  override def visitAssignCommand(ctx: AssignCommandContext): AssignCmd =
    AssignCmd(
      Load.expression(ctx.expr(0)),
      Load.expression(ctx.expr(1))
    )

  override def visitAssertCommand(ctx: AssertCommandContext): AssertCmd =
    AssertCmd(
      Load.expression(ctx.expr(0)),
      Load.expression(ctx.expr(1)),
      Load.expression(ctx.expr(2))
    )

  override def visitCommandList(ctx: CommandListContext): ListCmd =
    ListCmd(
      visit(ctx.command(0)),
      visit(ctx.command(1))
    )

  override def visitLetCommand(ctx: LetCommandContext): LetCmd =
    LetCmd(
      Identifier(ctx.ident().getText),
      Load.expression(ctx.expr()),
      Load.command(ctx.command())
    )
}
