package plaid.prelude.antlr

import plaid.prelude.PreludeBaseVisitor
import plaid.prelude.PreludeParser.*
import plaid.prelude.ast.*

import scala.jdk.CollectionConverters.*

object TypeVisitor extends PreludeBaseVisitor[Type] {

  override def visitPartyIndexType(ctx: PartyIndexTypeContext) = PartyIndexType()

  override def visitStringType(ctx: StringTypeContext) = StringType()

  override def visitRecordType(ctx: RecordTypeContext) = RecordType(
    elements = new java.util.TreeMap(
      ctx.typedIdent().asScala
        .map { x =>
          val id = Identifier(x.ident().getText)
          val tpe = visit(x.`type`())
          id -> tpe
        }
        .toMap.asJava))
}