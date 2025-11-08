package plaid.prelude.logic

import plaid.prelude.ast.{Num, Str}

import java.util.concurrent.atomic.AtomicInteger

/**
 * Support for generating unique instances of AST objects.
 */
object Unique {
  private val start = 1
  private val partyCounter = new AtomicInteger(start)
  private val stringCounter = new AtomicInteger(start)

  def reset(): Unit =
    partyCounter.set(start)
    stringCounter.set(start)

  /**
   * Generate a unique party index using a negative number to avoid collisions.
   */
  def party() = Num(-partyCounter.getAndIncrement())

  /**
   * Generate a unique string that includes an illegal symbol to avoid
   * collisions.
   */
  def string() = Str(s"$$${stringCounter.getAndIncrement()}")
}
