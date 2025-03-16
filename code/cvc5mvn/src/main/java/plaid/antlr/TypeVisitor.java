package plaid.antlr;

import plaid.ast.*;

import plaid.PreludeBaseVisitor;
import plaid.PreludeParser.PartyIndexTypeContext;
import plaid.PreludeParser.RecordTypeContext;
import plaid.PreludeParser.StringTypeContext;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.TreeMap;

public class TypeVisitor extends PreludeBaseVisitor<Type> {
    @Override
    public PartyIndexType visitPartyIndexType(PartyIndexTypeContext ctx) {
        return new PartyIndexType();
    }

    @Override
    public StringType visitStringType(StringTypeContext ctx) {
        return new StringType();
    }

    @Override
    public RecordType visitRecordType(RecordTypeContext ctx) {
        Map<Identifier, Type> map = ctx.typedIdent().stream().collect(Collectors.toMap(
                x -> new Identifier(x.ident().getText()),
                x -> visit(x.type())));
        return new RecordType(new TreeMap<>(map));
    }
}
