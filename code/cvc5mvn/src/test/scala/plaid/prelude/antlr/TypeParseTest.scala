package plaid.prelude.antlr

import org.junit.Assert.assertEquals
import org.junit.Test
import plaid.prelude.ast.*

class TypeParseTest {

  /** Parses client id type */
  @Test
  def partyIndexType(): Unit =
    assertEquals(PartyIndexType(), Loader.typeMarker("cid"))

  /** Parses string type */
  @Test
  def stringType(): Unit =
    assertEquals(StringType(), Loader.typeMarker("string"))

  /** Parses record type */
  @Test
  def recordType(): Unit =
    val expected = RecordType(Map(
      Identifier("s") -> StringType(),
      Identifier("i") -> PartyIndexType()))
    assertEquals(expected, Loader.typeMarker("{s: string; i: cid}"))

  /** Parses nested record type */
  @Test
  def nestedRecordType(): Unit =
    val nestedMap = Map(Identifier("i") -> PartyIndexType())
    val map = Map(Identifier("s") -> RecordType(nestedMap))
    val expected = RecordType(map)
    assertEquals(expected, Loader.typeMarker("{s: {i: cid}}"))
}
