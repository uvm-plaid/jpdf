package plaid.prelude

import picocli.CommandLine
import picocli.CommandLine.{Command, Option, Parameters}
import plaid.prelude.antlr.Loader
import plaid.prelude.ast.{Identifier, dependencyOrdered, expandAll, resolve}
import plaid.prelude.logic.ConstraintAnalyzer

import java.io.File
import java.nio.file.Files
import scala.compiletime.uninitialized

@Command(
  name = "prelude",
  version = Array("prelude-dev"),
  mixinStandardHelpOptions = true)
class App extends Runnable {

  @Option(names = Array("--field-size", "-s"), description = Array("Order of the finite field"))
  var fieldSize: String = "2"

  @Parameters(paramLabel = "<path>", description = Array("Path to a prelude source file"))
  var path: String = uninitialized

  override def run(): Unit =
    val src = Files.readString(new File(path).toPath)
    val ast = Loader.program(src)
    val exprFns = ast.exprFuncs.expandAll()
    val constraintFns = ast.constraintFuncs.expandAll(exprFns)
    val cmdFns = ast.cmdFuncs.expandAll(exprFns, constraintFns)

    val analyzer = new ConstraintAnalyzer(ast, fieldSize)
    val constraints = analyzer.inferPrePostFN(ast.cmdFuncs.resolve(Identifier("main")))
    println(s"The precondition for main: ${constraints.precondition.prettyPrint()}")
    println(s"The postcondition for main: ${constraints.postcondition.prettyPrint()}")
}

object App {
  def main(args: Array[String]): Unit = {
    val exitCode = new CommandLine(new App()).execute(args*)
    System.exit(exitCode)
  }
}
