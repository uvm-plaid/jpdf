package plaid.prelude.antlr

import plaid.prelude.PreludeBaseVisitor
import plaid.prelude.PreludeParser.*
import plaid.prelude.ast.*

import scala.jdk.CollectionConverters.*

object CommandVisitor extends PreludeBaseVisitor[Cmd] {

  override def visitFunctionCallCmd(ctx: FunctionCallCmdContext) = CallCmd(
    id = Identifier(ctx.ident().getText),
    parms = ctx.expr().asScala.map(Loader.expression).toList)

  override def visitAssignCmd(ctx: AssignCmdContext) = AssignCmd(
    e1 = Loader.expression(ctx.expr(0)),
    e2 = Loader.expression(ctx.expr(1)))

  override def visitAssertCmd(ctx: AssertCmdContext) = AssertCmd(
    e1 = Loader.expression(ctx.expr(0)),
    e2 = Loader.expression(ctx.expr(1)),
    e3 = Loader.expression(ctx.expr(2)))

  override def visitLetCmd(ctx: LetCmdContext) = LetCmd(
    y = Identifier(ctx.ident().getText),
    e = Loader.expression(ctx.expr()),
    c = ctx.cmd().asScala.map(Loader.command).toList)
}
