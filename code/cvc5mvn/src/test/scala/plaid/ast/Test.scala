import org.junit.Test
import org.junit.Assert._
import plaid.antlr.Loader
import plaid.ast._

class IntJunitTests {

  trait Notification

  case class Email(sender: String, title: String, body: String) extends Notification

  case class SMS(caller: String, message: String) extends Notification

  case class VoiceRecording(contactName: String, link: String) extends Notification
  @Test
  def patternMatching {
    val root = Loader.toCommand("m[\"x\"]@2 := s[\"foo\"]@3")
    val msg = root match {
      /*
      case AssignCommand(new AtExpr(new Str("x"), new Num(2)), new Str("asdf")) =>
        "match"
      case _: Object => "no match"*/

      case Email(sender, title, _) =>
        s"You got an email from $sender with title: $title"
    }
    print(msg)
  }

  @Test
  def testOneIsPositive {
    assertTrue(1 > 0)
  }

  @Test
  def testMinusOneIsNegative {
    assertTrue(-1 < 0)
  }
}