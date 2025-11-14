package plaid.prelude.cvc

import plaid.prelude.ast.*

object CvcUtils {

  def getCvcName(expr: Expr, partyIndex: Integer): String = expr match
    case MessageExpr(e) => s"m_${toString(e)}_$partyIndex"
    case PublicExpr(e) => s"p_${toString(e)}"
    case RandomExpr(e) => s"r_${toString(e)}_$partyIndex"
    case SecretExpr(e) => s"s_${toString(e)}_$partyIndex"
    case _: OutputExpr => s"o_$partyIndex"
    case other => throw Exception(s"Must be memory, found ${other.getClass.getName}")

  def toInt(expr: Expr): Int = expr match
    case Num(num) => num
    case other => throw Exception(s"Must be number, found ${other.getClass.getName}")

  def toString(expr: Expr): String = expr match
    case Str(str) => str
    case other => throw Exception(s"Must be string, found ${other.getClass.getName}")
}
