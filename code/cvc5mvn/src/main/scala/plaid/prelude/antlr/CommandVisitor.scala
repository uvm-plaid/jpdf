package plaid.prelude.antlr

import plaid.prelude.PreludeBaseVisitor
import plaid.prelude.PreludeParser.*
import plaid.prelude.ast.*

import scala.jdk.CollectionConverters.*

object CommandVisitor extends PreludeBaseVisitor[Cmd] {

  override def visitFunctionCallCommand(ctx: FunctionCallCommandContext): CallCmd =
    CallCmd(
      Identifier(ctx.ident().getText),
      ctx.expr().asScala.map(Loader.expression).toList
    )

  override def visitAssignCommand(ctx: AssignCommandContext): AssignCmd =
    AssignCmd(
      Loader.expression(ctx.expr(0)),
      Loader.expression(ctx.expr(1))
    )

  override def visitAssertCommand(ctx: AssertCommandContext): AssertCmd =
    AssertCmd(
      Loader.expression(ctx.expr(0)),
      Loader.expression(ctx.expr(1)),
      Loader.expression(ctx.expr(2))
    )

  override def visitCommandList(ctx: CommandListContext): ListCmd =
    ListCmd(
      visit(ctx.command(0)),
      visit(ctx.command(1))
    )

  override def visitLetCommand(ctx: LetCommandContext): LetCmd =
    LetCmd(
      Identifier(ctx.ident().getText),
      Loader.expression(ctx.expr()),
      Loader.command(ctx.command())
    )
}
