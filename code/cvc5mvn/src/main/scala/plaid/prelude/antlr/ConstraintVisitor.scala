package plaid.prelude.antlr

import plaid.prelude.PreludeBaseVisitor
import plaid.prelude.PreludeParser.*
import plaid.prelude.ast.*

import scala.jdk.CollectionConverters.*

object ConstraintVisitor extends PreludeBaseVisitor[Constraint] {

  override def visitParenConstraintExpr(ctx: ParenConstraintExprContext): Constraint =
    visit(ctx.constraintExpr())

  override def visitAndConstraintExpr(ctx: AndConstraintExprContext) = AndConstraint(
    e1 = visit(ctx.constraintExpr(0)),
    e2 = visit(ctx.constraintExpr(1)))

  override def visitNotConstraintExpr(ctx: NotConstraintExprContext) = NotConstraint(
    e = visit(ctx.constraintExpr()))

  override def visitEqualConstraintExpr(ctx: EqualConstraintExprContext) = EqualConstraint(
    e1 = Loader.expression(ctx.expr(0)),
    e2 = Loader.expression(ctx.expr(1)))

  override def visitTrueConstraintExpr(ctx: TrueConstraintExprContext) = TrueConstraint()

  override def visitFunctionCallConstraintExpr(ctx: FunctionCallConstraintExprContext) = CallConstraint(
    fn = Identifier(ctx.ident().getText),
    parms = ctx.expr().asScala.map(Loader.expression).toList)
}
