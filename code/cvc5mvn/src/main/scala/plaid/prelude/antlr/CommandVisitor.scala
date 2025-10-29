package plaid.prelude.antlr

import plaid.prelude.PreludeBaseVisitor
import plaid.prelude.PreludeParser.*
import plaid.prelude.ast.*

import scala.jdk.CollectionConverters.*

object CommandVisitor extends PreludeBaseVisitor[Cmd] {

  override def visitFunctionCallCommand(ctx: FunctionCallCommandContext) = CallCmd(
    fn = Identifier(ctx.ident().getText),
    parms = ctx.expr().asScala.map(Loader.expression).toList)

  override def visitAssignCommand(ctx: AssignCommandContext) = AssignCmd(
    e1 = Loader.expression(ctx.expr(0)),
    e2 = Loader.expression(ctx.expr(1)))

  override def visitAssertCommand(ctx: AssertCommandContext) = AssertCmd(
    e1 = Loader.expression(ctx.expr(0)),
    e2 = Loader.expression(ctx.expr(1)),
    e3 = Loader.expression(ctx.expr(2)))

  override def visitCommandList(ctx: CommandListContext) = ListCmd(
    c1 = visit(ctx.command(0)),
    c2 = visit(ctx.command(1)))

  override def visitLetCommand(ctx: LetCommandContext) = LetCmd(
    y = Identifier(ctx.ident().getText),
    e = Loader.expression(ctx.expr()),
    c = Loader.command(ctx.command()))
}
