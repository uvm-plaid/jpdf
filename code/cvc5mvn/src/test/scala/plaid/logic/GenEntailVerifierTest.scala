package plaid.logic

import io.github.cvc5.CVC5ApiException
import org.junit.Test
import org.junit.Assert._
import plaid.antlr.Load
import plaid.ast._

import java.util.{Map, TreeMap}

class GenEntailVerifierTest {

  @Test
  @throws[ CVC5ApiException ]
  def genFreshStringTest(): Unit = {
    val program = Program(List(), List(), List())
    val genEntailVerifier = new GenEntailVerifier(program, "2")

    assertEquals(Str("$1"), genEntailVerifier.genFreshValue(StringType()))
    assertEquals(Str("$2"), genEntailVerifier.genFreshValue(StringType()))
    assertEquals(Str("$3"), genEntailVerifier.genFreshValue(StringType()))
  }

  @Test
  @throws[ CVC5ApiException ]
  def genFreshCIDTest(): Unit = {
    val program = Program(List(), List(), List())
    val genEntailVerifier = new GenEntailVerifier(program, "2")

    assertEquals(Num(-1), genEntailVerifier.genFreshValue(PartyIndexType()))
    assertEquals(Num(-2), genEntailVerifier.genFreshValue(PartyIndexType()))
    assertEquals(Num(-3), genEntailVerifier.genFreshValue(PartyIndexType()))
  }

  @Test
  @throws[ CVC5ApiException ]
  def genFreshRecordTest(): Unit = {
    val src = "{ s : string ; i : cid}"
    val program = Program(List(), List(), List())
    val genEntailVerifier = new GenEntailVerifier(program, "2")

    val recordType = Load.typeMarker(src).asInstanceOf[RecordType]
    val map = new TreeMap[Identifier, Expr]()
    map.put(Identifier("s"), Str("$2"))
    map.put(Identifier("i"), Num(-1))
    val expected = FieldExpr(map)

    assertEquals(expected, genEntailVerifier.genFreshValue(recordType))
  }

  @Test
  @throws[ CVC5ApiException ]
  def nestedRecordType(): Unit = {
    val src = "{t: {s:string; t2:{i:cid}}}"
    val program = Program(List(), List(), List())
    val genEntailVerifier = new GenEntailVerifier(program, "2")

    val recordType = Load.typeMarker(src).asInstanceOf[RecordType]

    val innermap = new TreeMap[Identifier, Expr]()
    innermap.put(Identifier("s"), Str("$1"))
    innermap.put(Identifier("t2"), FieldExpr(new TreeMap(Map.of(Identifier("i"), Num(-2)))))

    val expected = FieldExpr(new TreeMap(Map.of(Identifier("t"), FieldExpr(innermap))))
    assertEquals(expected, genEntailVerifier.genFreshValue(recordType))
  }

  @Test
  @throws[ CVC5ApiException ]
  def genEntailTest(): Unit = {
    val e1 =
      """m[z++"s"]@i1 == m[x++"s"]@i1 + m[y++"s"]@i1 AND
        |m[z++"s"]@i2 == m[x++"s"]@i2 + m[y++"s"]@i2""".stripMargin
    val e2 =
      """m[z++"s"]@i1 + m[z++"s"]@i2 ==
        |m[x++"s"]@i1 + m[y++"s"]@i1 + m[x++"s"]@i2 + m[y++"s"]@i2""".stripMargin

    val program = Program(List(), List(), List())
    val genEntailVerifier = new GenEntailVerifier(program, "2")

    val typing = List(
      TypedIdentifier(Identifier("i2"), PartyIndexType()),
      TypedIdentifier(Identifier("y"), StringType()),
      TypedIdentifier(Identifier("x"), StringType()),
      TypedIdentifier(Identifier("i1"), PartyIndexType()),
      TypedIdentifier(Identifier("z"), StringType())
    )

    val result1 = genEntailVerifier.genEntails(typing, Load.constraint(e1), Load.constraint(e2))
    assertTrue(result1)

    val result2 = genEntailVerifier.genEntails(typing, Load.constraint(e2), Load.constraint(e1))
    assertFalse(result2)
  }

}
