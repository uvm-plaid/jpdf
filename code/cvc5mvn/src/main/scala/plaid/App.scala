package plaid

import java.io.File
import java.nio.file.Files

import io.github.cvc5.CVC5ApiException
import picocli.CommandLine
import picocli.CommandLine.{Command, Option, Parameters}

import plaid.antlr.Loader
import plaid.ast.Program
import plaid.logic.{ConstraintAnalyzer, Constraints}

@Command(
  name = "prelude",
  version = Array("prelude-dev"),
  mixinStandardHelpOptions = true
)
class App extends Runnable {

  @Option(names = Array("--field-size", "-s"), description = Array("Order of the finite field"))
  var fieldSize: String = "2"

  @Parameters(paramLabel = "<path>", description = Array("Path to a prelude source file"))
  var path: String = _

  def staticAnalysis(program: Program): Constraints = {
    val analyzer = new ConstraintAnalyzer(program, fieldSize)
    analyzer.inferPrePostFN(program.resolveCommandFunction(Loader.toExpression("main")))
  }

  override def run(): Unit = {
    val programSource = readSourceCode()
    val programAST = Loader.toProgram(programSource)

    try {
      val constraints = staticAnalysis(programAST)
      println(s"The precondition for main: ${ScalaFunctions.prettyPrint(constraints.precondition)}")
      println(s"The postcondition for main: ${ScalaFunctions.prettyPrint(constraints.postcondition)}")
    } catch {
      case e: CVC5ApiException => throw new RuntimeException(e)
    }
  }

  private def readSourceCode(): String = {
    try {
      Files.readString(new File(path).toPath)
    } catch {
      case e: Exception => throw new RuntimeException(e)
    }
  }
}

object App {
  def main(args: Array[String]): Unit = {
    val exitCode = new CommandLine(new App()).execute(args: _*)
    System.exit(exitCode)
  }
}
