package plaid.antlr;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import plaid.PreludeLexer;
import plaid.PreludeParser;

import java.io.IOException;
import java.io.InputStream;

public class PreludeLoader {

    public PreludeParser createParser(String src) {
        ANTLRInputStream input = new ANTLRInputStream(src);
        PreludeLexer lexer = new PreludeLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PreludeParser parser = new PreludeParser(tokens);
        parser.setBuildParseTree(true);
        return parser;
    }

}
