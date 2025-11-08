package plaid.prelude.ast

import org.junit.Assert.assertEquals
import org.junit.Test
import plaid.prelude.antlr.Loader
import plaid.prelude.logic.Unique

class TypeTest {

  @Test
  def freshString(): Unit =
    Unique.reset()
    assertEquals(Str("$1"), StringType().freshValue())
    assertEquals(Str("$2"), StringType().freshValue())
    assertEquals(Str("$3"), StringType().freshValue())

  @Test
  def freshParty(): Unit =
    Unique.reset()
    assertEquals(Num(-1), PartyIndexType().freshValue())
    assertEquals(Num(-2), PartyIndexType().freshValue())
    assertEquals(Num(-3), PartyIndexType().freshValue())

  @Test
  def freshRecord(): Unit =
    Unique.reset()
    val recordType = Loader.typeMarker("{ s : string ; i : cid}")
    val expected = FieldExpr(Map(
      Identifier("s") -> Str("$1"),
      Identifier("i") -> Num(-1)))
    assertEquals(expected, recordType.freshValue())

  @Test
  def freshNestedRecord(): Unit =
    Unique.reset()
    val recordType = Loader.typeMarker("{t: {s:string; t2:{i:cid}}}")
    val expected = FieldExpr(Map(Identifier("t") ->
      FieldExpr(Map(Identifier("s") -> Str("$1"), Identifier("t2") ->
        FieldExpr(Map(Identifier("i") -> Num(-1)))))))
    assertEquals(expected, recordType.freshValue())
}
