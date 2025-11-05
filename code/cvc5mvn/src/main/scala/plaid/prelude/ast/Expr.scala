package plaid.prelude.ast

sealed trait Expr extends Node {

  /**
   * Apply a transformation f to this expression. If f does not return an
   * expression, transform applies f to the children of this expression,
   * recursively. If there are no children, the expression is returned as-is.
   */
  def transform(f: Expr => Option[Expr]): Expr = f(this).getOrElse(this match
    case AtExpr(e1, e2) => AtExpr(e1.transform(f), e2.transform(f))
    case ConcatExpr(e1, e2) => ConcatExpr(e1.transform(f), e2.transform(f))
    case FieldExpr(elements) => FieldExpr(elements.view.mapValues(_.transform(f)).toMap)
    case FieldSelectExpr(e, l) => FieldSelectExpr(e.transform(f), l)
    case CallExpr(fn, parms) => CallExpr(fn, parms.map(_.transform(f)))
    case LetExpr(y, e1, e2) => LetExpr(y, e1.transform(f), e2.transform(f))
    case MessageExpr(e) => MessageExpr(e.transform(f))
    case RandomExpr(e) => RandomExpr(e.transform(f))
    case MinusExpr(e) => MinusExpr(e.transform(f))
    case OTExpr(e1, i1, e2, e3) => OTExpr(e1.transform(f), i1.transform(f), e2.transform(f), e3.transform(f))
    case OTFourExpr(s1, s2, i1, e1, e2, e3, e4) => OTFourExpr(s1.transform(f), s2.transform(f), i1.transform(f), e1.transform(f), e2.transform(f), e3.transform(f), e4.transform(f))
    case PlusExpr(e1, e2) => PlusExpr(e1.transform(f), e2.transform(f))
    case PublicExpr(e) => PublicExpr(e.transform(f))
    case SecretExpr(e) => SecretExpr(e.transform(f))
    case TimesExpr(e1, e2) => TimesExpr(e1.transform(f), e2.transform(f))
    case other => f(other).getOrElse(other))

  /**
   * Expand let bindings, substitute function calls with function bodies,
   * reduce string concatenations, and replace identifiers in bindings for
   * arbitrary subexpressions.
   */
  def expand(ctx: List[ExprFunc] = List(), bindings: Map[Identifier, Expr] = Map()): Expr = transform {
    case id: Identifier => Some(bindings.getOrElse(id, id))
    case ConcatExpr(Str(s1), Str(s2)) => Some(Str(s1 + s2))
    case ConcatExpr(ConcatExpr(e1, Str(s1)), Str(s2)) => Some(ConcatExpr(e1, Str(s1 + s2)))
    case ConcatExpr(Str(s1), ConcatExpr(Str(s2), e2)) => Some(ConcatExpr(Str(s1 + s2), e2))
    case LetExpr(y, e1, e2) =>
      val b = bindings.updated(y, e1.expand(ctx, bindings))
      Some(e2.expand(ctx, b))
    case CallExpr(fn, parms) =>
      val f = ctx.resolve(fn)
      val formalParms = f.parms
      val actualParms = parms.map(_.expand(ctx, bindings))
      Some(f.body.expand(ctx, Map.from(formalParms.zip(actualParms))))
    case FieldSelectExpr(e, l) =>
      val field = e.expand(ctx, bindings).asInstanceOf[FieldExpr]
      Some(field.elements.getOrElse(l, throw Exception(s"Cannot find field $l")))
    case other => None
  }

  /**
   * Move party indexes directly around the memories they enclose.
   */
  def indexParties(partyIndex: Option[Expr] = None): Expr = transform {
    case AtExpr(e1, e2) => Some(e1.indexParties(Some(e2)))
    case e: RandomExpr => Some(AtExpr(e, partyIndex.get))
    case e: SecretExpr => Some(AtExpr(e, partyIndex.get))
    case e: MessageExpr => Some(AtExpr(e, partyIndex.get))
    case e: OutputExpr => Some(AtExpr(e, partyIndex.get))
    case other => None
  }
}

case class AtExpr(e1: Expr, e2: Expr) extends Expr
case class ConcatExpr(e1: Expr, e2: Expr) extends Expr
case class FieldExpr(elements: Map[Identifier, Expr]) extends Expr
case class FieldSelectExpr(e: Expr, l: Identifier) extends Expr
case class CallExpr(id: Identifier, parms: List[Expr]) extends Expr
case class LetExpr(y: Identifier, e1: Expr, e2: Expr) extends Expr
case class MessageExpr(e: Expr) extends Expr
case class RandomExpr(e: Expr) extends Expr
case class MinusExpr(e: Expr) extends Expr
case class Num(num: Int) extends Expr
case class OTExpr(e1: Expr, i1: Expr, e2: Expr, e3: Expr) extends Expr
case class OTFourExpr(s1: Expr, s2: Expr, i1: Expr, e1: Expr, e2: Expr, e3: Expr, e4: Expr) extends Expr
case class OutputExpr() extends Expr
case class PlusExpr(e1: Expr, e2: Expr) extends Expr
case class PublicExpr(e: Expr) extends Expr
case class SecretExpr(e: Expr) extends Expr
case class Str(str: String) extends Expr
case class TimesExpr(e1: Expr, e2: Expr) extends Expr
case class Identifier(name: String) extends Expr
