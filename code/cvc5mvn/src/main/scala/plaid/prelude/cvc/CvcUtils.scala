package plaid.prelude.cvc

import plaid.prelude.ast.*

object CvcUtils {

  def getCvcName(expr: Expr, partyIndex: Integer): String = expr match {
    case mem: MessageExpr => s"m_${toString(mem.e)}_$partyIndex"
    case _: OutputExpr    => s"o_$partyIndex"
    case mem: PublicExpr  => s"p_${toString(mem.e)}"
    case mem: RandomExpr  => s"r_${toString(mem.e)}_$partyIndex"
    case mem: SecretExpr  => s"s_${toString(mem.e)}_$partyIndex"
    case other            => throw new IllegalArgumentException(s"Must be memory, found ${other.getClass.getName}")
  }

  def toInt(expr: Expr): Int = expr match {
    case num: Num => num.num
    case other    => throw new IllegalArgumentException(s"Must be number, found ${other.getClass.getName}")
  }

  def toString(expr: Expr): String = expr match {
    case str: Str => str.str
    case other    => throw new IllegalArgumentException(s"Must be string, found ${other.getClass.getName}")
  }

}
