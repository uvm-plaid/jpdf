package plaid.antlr

import org.junit.Test
import plaid.ast._
import scala.jdk.CollectionConverters._
import scala.collection.immutable.TreeMap
import org.junit.Assert.assertEquals

class TypeVisitorTest {

  private def ast(src: String): Type = Loader.toType(src)

  /** Parses client id type */
  @Test
  def partyIndexType(): Unit = {
    assertEquals(PartyIndexType(), ast("cid"))
  }

  /** Parses string type */
  @Test
  def stringType(): Unit = {
    assertEquals(StringType(), ast("string"))
  }

  /** Parses record type */
  @Test
  def recordType(): Unit = {
    val map: TreeMap[Identifier, Type] = TreeMap(
      Identifier("s") -> StringType(),
      Identifier("i") -> PartyIndexType()
    )

    val expected = RecordType(java.util.TreeMap(map.asJava))
    assertEquals(expected, ast("{s: string; i: cid}"))
  }

  /** Parses nested record type */
  @Test
  def nestedRecordType(): Unit = {
    val nestedMap: TreeMap[Identifier, Type] = TreeMap(
      Identifier("i") -> PartyIndexType()
    )
    val map: TreeMap[Identifier, Type] = TreeMap(
      Identifier("s") -> RecordType(java.util.TreeMap(nestedMap.asJava))
    )

    val expected = RecordType(java.util.TreeMap(map.asJava))
    assertEquals(expected, ast("{s: {i: cid}}"))
  }
}
