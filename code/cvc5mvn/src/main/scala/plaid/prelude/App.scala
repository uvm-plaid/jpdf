package plaid.prelude

import picocli.CommandLine
import picocli.CommandLine.{Command, Option, Parameters}
import plaid.prelude.antlr.Loader
import plaid.prelude.logic.ConstraintAnalyzer

import java.io.File
import java.nio.file.Files

@Command(
  name = "prelude",
  version = Array("prelude-dev"),
  mixinStandardHelpOptions = true)
class App extends Runnable {

  @Option(names = Array("--field-size", "-s"), description = Array("Order of the finite field"))
  var fieldSize: String = "2"

  @Parameters(paramLabel = "<path>", description = Array("Path to a prelude source file"))
  var path: String = _

  override def run(): Unit =
    val src = Files.readString(new File(path).toPath)
    val ast = Loader.program(src)
    val analyzer = new ConstraintAnalyzer(ast, fieldSize)
    val constraints = analyzer.inferPrePostFN(ast.resolveCommandFunction(Loader.expression("main")))
    println(s"The precondition for main: ${prettyPrint(constraints.precondition)}")
    println(s"The postcondition for main: ${prettyPrint(constraints.postcondition)}")
}

object App {
  def main(args: Array[String]): Unit = {
    val exitCode = new CommandLine(new App()).execute(args: _*)
    System.exit(exitCode)
  }
}
