package plaid.prelude.antlr

import plaid.prelude.PreludeBaseVisitor
import plaid.prelude.PreludeParser.*
import plaid.prelude.ast.*

import scala.jdk.CollectionConverters.*

object ConstraintVisitor extends PreludeBaseVisitor[Constraint] {

  override def visitParenConstraint(ctx: ParenConstraintContext): Constraint =
    visit(ctx.constraint())

  override def visitAndConstraint(ctx: AndConstraintContext) = AndConstraint(
    e1 = visit(ctx.constraint(0)),
    e2 = visit(ctx.constraint(1)))

  override def visitNotConstraint(ctx: NotConstraintContext) = NotConstraint(
    e = visit(ctx.constraint()))

  override def visitEqualConstraint(ctx: EqualConstraintContext) = EqualConstraint(
    e1 = Loader.expression(ctx.expr(0)),
    e2 = Loader.expression(ctx.expr(1)))

  override def visitTrueConstraint(ctx: TrueConstraintContext) = TrueConstraint()

  override def visitFunctionCallConstraint(ctx: FunctionCallConstraintContext) = CallConstraint(
    fn = Identifier(ctx.ident().getText),
    parms = ctx.expr().asScala.map(Loader.expression).toList)
}
