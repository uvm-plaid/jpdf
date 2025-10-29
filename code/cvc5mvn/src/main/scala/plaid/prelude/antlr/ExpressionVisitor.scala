package plaid.prelude.antlr

import plaid.prelude.PreludeBaseVisitor
import plaid.prelude.PreludeParser.*
import plaid.prelude.ast.*

import scala.jdk.CollectionConverters.*

object ExpressionVisitor extends PreludeBaseVisitor[Expr] {

  override def visitParenPExpr(ctx: ParenPExprContext): Expr =
    visit(ctx.expr())

  override def visitFunctionCallExpr(ctx: FunctionCallExprContext) = CallExpr(
    fn = Identifier(ctx.ident().getText),
    parms = ctx.expr().asScala.map(visit).toList)

  override def visitLetExpr(ctx: LetExprContext) = LetExpr(
    y = Identifier(ctx.ident().getText),
    e1 = visit(ctx.expr(0)),
    e2 = visit(ctx.expr(1)))

  override def visitAtExpr(ctx: AtExprContext) = AtExpr(
    e1 = visit(ctx.expr(0)),
    e2 = visit(ctx.expr(1)))

  override def visitFieldExpr(ctx: FieldExprContext) = FieldExpr(
    elements = new java.util.TreeMap(ctx.flddecl().asScala
      .map(x => Identifier(x.ident().getText) -> visit(x.expr()))
      .toMap
      .asJava))

  override def visitFieldSelectExpr(ctx: FieldSelectExprContext) = FieldSelectExpr(
    e = visit(ctx.expr()),
    l = Identifier(ctx.ident().getText))

  override def visitSecretExpr(ctx: SecretExprContext) = SecretExpr(
    e = visit(ctx.expr()))

  override def visitRandomExpr(ctx: RandomExprContext) = RandomExpr(
    e = visit(ctx.expr()))

  override def visitMessageExpr(ctx: MessageExprContext) = MessageExpr(
    e = visit(ctx.expr()))

  override def visitPublicExpr(ctx: PublicExprContext) = PublicExpr(
    e = visit(ctx.expr()))

  override def visitOutputExpr(ctx: OutputExprContext) = OutputExpr()

  override def visitPlusExpr(ctx: PlusExprContext) = PlusExpr(
    e1 = visit(ctx.expr(0)),
    e2 = visit(ctx.expr(1)))

  override def visitMinusExpr(ctx: MinusExprContext) = MinusExpr(
    e = visit(ctx.expr()))

  override def visitConcatExpr(ctx: ConcatExprContext) = ConcatExpr(
    e1 = visit(ctx.expr(0)),
    e2 = visit(ctx.expr(1)))

  override def visitTimesExpr(ctx: TimesExprContext) = TimesExpr(
    e1 = visit(ctx.expr(0)),
    e2 = visit(ctx.expr(1)))

  override def visitStr(ctx: StrContext) = Str(
    str = ctx.getText.replaceAll("\"", ""))

  override def visitNum(ctx: NumContext) = Num(
    num = ctx.getText.toInt)

  override def visitIdentExpr(ctx: IdentExprContext) = Identifier(
    name = ctx.getText)

  override def visitOTExpr(ctx: OTExprContext) = OTExpr(
    e1 = visit(ctx.expr(0)),
    i1 = visit(ctx.expr(1)),
    e2 = visit(ctx.expr(2)),
    e3 = visit(ctx.expr(3)))

  override def visitOTFourExpr(ctx: OTFourExprContext) = OTFourExpr(
    s1 = visit(ctx.expr(0)),
    s2 = visit(ctx.expr(1)),
    i1 = visit(ctx.expr(2)),
    e1 = visit(ctx.expr(3)),
    e2 = visit(ctx.expr(4)),
    e3 = visit(ctx.expr(5)),
    e4 = visit(ctx.expr(6)))
}
