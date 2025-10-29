package plaid.antlr

import plaid.PreludeBaseVisitor
import plaid.PreludeParser.*
import plaid.ast.*

import scala.jdk.CollectionConverters.*

object ExpressionVisitor extends PreludeBaseVisitor[Expr] {

  override def visitParenPExpr(ctx: ParenPExprContext): Expr =
    visit(ctx.expr())

  override def visitFunctionCallExpr(ctx: FunctionCallExprContext): CallExpr =
    CallExpr(
      Identifier(ctx.ident().getText),
      ctx.expr().asScala.map(visit).toList
    )

  override def visitLetExpr(ctx: LetExprContext): LetExpr =
    LetExpr(
      Identifier(ctx.ident().getText),
      visit(ctx.expr(0)),
      visit(ctx.expr(1))
    )

  override def visitAtExpr(ctx: AtExprContext): AtExpr =
    AtExpr(visit(ctx.expr(0)), visit(ctx.expr(1)))

  override def visitFieldExpr(ctx: FieldExprContext): FieldExpr = {
    FieldExpr(new java.util.TreeMap(ctx.flddecl().asScala
      .map(x => Identifier(x.ident().getText) -> visit(x.expr()))
      .toMap
      .asJava))
  }

  override def visitFieldSelectExpr(ctx: FieldSelectExprContext): FieldSelectExpr =
    FieldSelectExpr(visit(ctx.expr()), Identifier(ctx.ident().getText))

  override def visitSecretExpr(ctx: SecretExprContext): SecretExpr =
    SecretExpr(visit(ctx.expr()))

  override def visitRandomExpr(ctx: RandomExprContext): RandomExpr =
    RandomExpr(visit(ctx.expr()))

  override def visitMessageExpr(ctx: MessageExprContext): MessageExpr =
    MessageExpr(visit(ctx.expr()))

  override def visitPublicExpr(ctx: PublicExprContext): PublicExpr =
    PublicExpr(visit(ctx.expr()))

  override def visitOutputExpr(ctx: OutputExprContext): OutputExpr =
    OutputExpr()

  override def visitPlusExpr(ctx: PlusExprContext): PlusExpr =
    PlusExpr(visit(ctx.expr(0)), visit(ctx.expr(1)))

  override def visitMinusExpr(ctx: MinusExprContext): MinusExpr =
    MinusExpr(visit(ctx.expr()))

  override def visitConcatExpr(ctx: ConcatExprContext): ConcatExpr =
    ConcatExpr(visit(ctx.expr(0)), visit(ctx.expr(1)))

  override def visitTimesExpr(ctx: TimesExprContext): TimesExpr =
    TimesExpr(visit(ctx.expr(0)), visit(ctx.expr(1)))

  override def visitStr(ctx: StrContext): Str =
    Str(ctx.getText.replaceAll("\"", ""))

  override def visitNum(ctx: NumContext): Num =
    Num(ctx.getText.toInt)

  override def visitIdentExpr(ctx: IdentExprContext): Identifier =
    Identifier(ctx.getText)

  override def visitOTExpr(ctx: OTExprContext): OTExpr =
    OTExpr(
      visit(ctx.expr(0)),
      visit(ctx.expr(1)),
      visit(ctx.expr(2)),
      visit(ctx.expr(3))
    )

  override def visitOTFourExpr(ctx: OTFourExprContext): Expr =
    OTFourExpr(
      visit(ctx.expr(0)),
      visit(ctx.expr(1)),
      visit(ctx.expr(2)),
      visit(ctx.expr(3)),
      visit(ctx.expr(4)),
      visit(ctx.expr(5)),
      visit(ctx.expr(6))
    )
}
