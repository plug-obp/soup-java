package soup.syntax;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import soup.parser.SoupLexer;
import soup.parser.SoupParser;
import soup.syntax.model.SyntaxTreeElement;
import soup.syntax.model.declarations.Soup;
import soup.syntax.model.declarations.pieces.AnonymousPiece;
import soup.syntax.model.expressions.Expression;
import soup.syntax.model.statements.Statement;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

public class Reader {

    public static Expression readExpression(String expression) throws Exception {
        return readExpression(new StringReader(expression));
    }
    public static Expression readExpression(java.io.Reader input) throws IOException, ParseException {
        return read(input, Expression.class);
    }

    public static Statement readStatement(String statement) throws Exception {
        return readStatement(new StringReader(statement));
    }
    public static Statement readStatement(java.io.Reader input) throws IOException, ParseException {
        return read(input, Statement.class);
    }

    public static AnonymousPiece readPiece(String piece) throws Exception {
        return readPiece(new StringReader(piece));
    }
    public static AnonymousPiece readPiece(java.io.Reader input) throws IOException, ParseException {
        return read(input, AnonymousPiece.class);
    }

    public static Soup readSoup(String soup) throws Exception {
        return readSoup(new StringReader(soup));
    }
    public static Soup readSoup(java.io.Reader input) throws IOException, ParseException {
        return read(input, Soup.class);
    }

    public static Soup read(java.io.Reader input) throws IOException, ParseException {
        return readSoup(input);
    }

    public static <T extends SyntaxTreeElement> T read(java.io.Reader input, Class<T> type) throws IOException, ParseException {
        var parser = parser(input);
        var tree = parser.expression();
        if (parser.getCurrentToken().getType() != Token.EOF) {
            throw new ParseException("EOF expected", parser.getCurrentToken().getStartIndex());
        }
        return buildSyntaxTree(tree, type);
    }

    public static SoupParser parser(java.io.Reader r) throws IOException {
        var stream = CharStreams.fromReader(r);
        var lexer = new SoupLexer(stream);
        var tokens = new CommonTokenStream(lexer);
        return new SoupParser(tokens);
    }

    public static <T extends SyntaxTreeElement> T buildSyntaxTree(ParseTree tree, Class<T> type) {
        var builder = new Antrl4ToSyntax();
        ParseTreeWalker.DEFAULT.walk(builder, tree);
        return builder.get(tree, type);
    }
}
