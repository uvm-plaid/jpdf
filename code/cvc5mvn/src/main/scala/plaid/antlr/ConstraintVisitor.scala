package plaid.antlr

import plaid.PreludeBaseVisitor
import plaid.PreludeParser.*
import plaid.ast.*

import scala.jdk.CollectionConverters.*

object ConstraintVisitor extends PreludeBaseVisitor[Constraint] {

  override def visitParenConstraintExpr(ctx: ParenConstraintExprContext): Constraint =
    visit(ctx.constraintExpr())

  override def visitAndConstraintExpr(ctx: AndConstraintExprContext): AndConstraint =
    AndConstraint(visit(ctx.constraintExpr(0)), visit(ctx.constraintExpr(1)))

  override def visitNotConstraintExpr(ctx: NotConstraintExprContext): NotConstraint =
    NotConstraint(visit(ctx.constraintExpr()))

  override def visitEqualConstraintExpr(ctx: EqualConstraintExprContext): EqualConstraint =
    EqualConstraint(
      Loader.expression(ctx.expr(0)),
      Loader.expression(ctx.expr(1))
    )

  override def visitTrueConstraintExpr(ctx: TrueConstraintExprContext): TrueConstraint =
    TrueConstraint()

  override def visitFunctionCallConstraintExpr(ctx: FunctionCallConstraintExprContext): CallConstraint =
    CallConstraint(
      Identifier(ctx.ident().getText),
      ctx.expr().asScala.map(Loader.expression).toList
    )
}
