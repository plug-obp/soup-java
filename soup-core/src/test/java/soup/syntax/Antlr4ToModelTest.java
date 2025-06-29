package soup.syntax;

import org.junit.jupiter.api.Test;
import soup.syntax.model.Position;
import soup.syntax.model.declarations.Soup;
import soup.syntax.model.declarations.VariableDeclaration;
import soup.syntax.model.declarations.pieces.AnonymousPiece;
import soup.syntax.model.declarations.pieces.NamedPiece;
import soup.syntax.model.dependent.EnabledExpression;
import soup.syntax.model.dependent.InputReference;
import soup.syntax.model.dependent.NamedPieceReference;
import soup.syntax.model.dependent.PrimedReference;
import soup.syntax.model.expressions.ConditionalExpression;
import soup.syntax.model.expressions.Expression;
import soup.syntax.model.expressions.Reference;
import soup.syntax.model.expressions.binary.BinaryExpression;
import soup.syntax.model.expressions.binary.arithmetic.*;
import soup.syntax.model.expressions.binary.propositional.*;
import soup.syntax.model.expressions.binary.relational.*;
import soup.syntax.model.expressions.literals.BooleanLiteral;
import soup.syntax.model.expressions.literals.DoubleLiteral;
import soup.syntax.model.expressions.literals.IntegerLiteral;
import soup.syntax.model.expressions.unary.MinusExpression;
import soup.syntax.model.expressions.unary.NotExpression;
import soup.syntax.model.expressions.unary.ParenExpression;
import soup.syntax.model.expressions.unary.PlusExpression;
import soup.syntax.model.statements.Assignment;
import soup.syntax.model.statements.IfStatement;
import soup.syntax.model.statements.Sequence;
import soup.syntax.model.statements.Skip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Antlr4ToModelTest {
    Expression i23 = new IntegerLiteral(23, Position.ZERO);
    Expression i42 = new IntegerLiteral(42, Position.ZERO);
    Reference<VariableDeclaration> rx = new Reference<>("x", Position.ZERO);
    Reference<VariableDeclaration> ry = new Reference<>("y", Position.ZERO);
    @Test
    void testLiteral() throws Exception {
        assertAll("",
            () -> assertEquals(BooleanLiteral.TRUE, Reader.readExpression("true")),
            () -> assertEquals(BooleanLiteral.FALSE, Reader.readExpression("false")),
            () -> assertEquals(new IntegerLiteral(23, Position.ZERO), Reader.readExpression("23")),
            () -> assertEquals(new DoubleLiteral(23.3, Position.ZERO), Reader.readExpression("23.3"))
        );
    }

    @Test
    void testReference() throws Exception {
        assertEquals(new Reference<Object>("x", Position.ZERO), Reader.readExpression("x"));
        assertEquals(new Reference<Object>("aLongS", Position.ZERO), Reader.readExpression("aLongS"));
    }

    @Test
    void testParen() throws Exception {
        assertEquals(new ParenExpression(BooleanLiteral.TRUE, Position.ZERO), Reader.readExpression("(true)"));
    }

    @Test
    void testUnary() throws Exception {
        assertEquals(new NotExpression("!", BooleanLiteral.TRUE, Position.ZERO)
                , Reader.readExpression("!true"));
        assertEquals(
                new MinusExpression(
                    new MinusExpression(
                            new IntegerLiteral(23, Position.ZERO),
                            Position.ZERO),
                    Position.ZERO),
                Reader.readExpression("--23"));
        assertEquals(
                new PlusExpression(
                        new MinusExpression(
                            new IntegerLiteral(23, Position.ZERO),
                            Position.ZERO
                        ),
                        Position.ZERO),
                Reader.readExpression("+-23"));
        assertEquals(
                new MinusExpression(
                        new ParenExpression(
                            new IntegerLiteral(23, Position.ZERO),
                            Position.ZERO),
                        Position.ZERO),
                Reader.readExpression("-(23)"));
        assertEquals(
                new PlusExpression(
                        new ParenExpression(
                                new IntegerLiteral(23, Position.ZERO),
                                Position.ZERO),
                        Position.ZERO),
                Reader.readExpression("+(23)"));
    }

    @Test
    void testBinary() throws Exception {

        BinaryExpression expected = new Multiplication(i42, i42, Position.ZERO);
        assertEquals(expected, Reader.readExpression("42 * 42"));

        expected = new Division(i42, i23, Position.ZERO);
        assertEquals(expected, Reader.readExpression("42 / 23"));

        expected = new Modulus(i42, i23, Position.ZERO);
        assertEquals(expected, Reader.readExpression("42 %23"));

        expected = new Addition(i23, i42, Position.ZERO);
        assertEquals(expected, Reader.readExpression("23 + 42"));

        expected = new Substraction(i42, i23, Position.ZERO);
        assertEquals(expected, Reader.readExpression("42 - 23"));

        expected = new LessThan("<", i42, i23, Position.ZERO);
        assertEquals(expected, Reader.readExpression("42 < 23"));

        expected = new LessThanOrEqual("<=", i42, i23, Position.ZERO);
        assertEquals(expected, Reader.readExpression("42 <= 23"));

        expected = new GreaterThan(">", i42, i23, Position.ZERO);
        assertEquals(expected, Reader.readExpression("42 > 23"));

        expected = new GreaterThanOrEqual(">=", i42, i23, Position.ZERO);
        assertEquals(expected, Reader.readExpression("42 >= 23"));

        expected = new Equal("==", i42, i23, Position.ZERO);
        assertEquals(expected, Reader.readExpression("42 == 23"));

        expected = new NotEqual("!=", i42, i23, Position.ZERO);
        assertEquals(expected, Reader.readExpression("42 != 23"));

        expected = new Conjunction("&&", BooleanLiteral.TRUE, BooleanLiteral.FALSE, Position.ZERO);
        assertEquals(expected, Reader.readExpression("true && false"));

        expected = new Disjunction("||", BooleanLiteral.TRUE, BooleanLiteral.FALSE, Position.ZERO);
        assertEquals(expected, Reader.readExpression("true || false"));

        expected = new Implication("->", BooleanLiteral.TRUE, BooleanLiteral.FALSE, Position.ZERO);
        assertEquals(expected, Reader.readExpression("true -> false"));

        expected = new Equivalence("<->", BooleanLiteral.TRUE, BooleanLiteral.FALSE, Position.ZERO);
        assertEquals(expected, Reader.readExpression("true <-> false"));

        expected = new ExclusiveDisjunction("xor", BooleanLiteral.TRUE, BooleanLiteral.FALSE, Position.ZERO);
        assertEquals(expected, Reader.readExpression("true xor false"));
    }

    @Test
    void testConditional() throws Exception {
        var expected = new ConditionalExpression(
                BooleanLiteral.TRUE,
                i23,
                i42,
                Position.ZERO
        );
        assertEquals(expected, Reader.readExpression("true ? 23 : 42"));
    }

    @Test
    void testSkip() throws Exception {
        assertEquals(Skip.INSTANCE, Reader.readStatement("skip"));
    }
    @Test
    void testAssignment() throws Exception {
        var expected = new Assignment(
                new Reference<>("x", Position.ZERO),
                i23,
                Position.ZERO
        );
        assertEquals(expected, Reader.readStatement("x = 23"));
    }

    @Test
    void testIf() throws Exception {
        var expected = new IfStatement(
                BooleanLiteral.TRUE,
                new Assignment(rx, i23, Position.ZERO),
                new Assignment(ry, i42, Position.ZERO),
                Position.ZERO
        );
        assertEquals(expected, Reader.readStatement("if true then x = 23 else y = 42"));

        expected = new IfStatement(
                BooleanLiteral.FALSE,
                new Assignment(rx, i23, Position.ZERO),
                Skip.INSTANCE,
                Position.ZERO
        );
        assertEquals(expected, Reader.readStatement("if false then x = 23"));
    }

    @Test
    void testSeq() throws Exception {
        var expected = new Sequence(
                new Assignment(rx, i23, Position.ZERO),
                new Assignment(ry, i42, Position.ZERO),
                Position.ZERO);
        assertEquals(expected, Reader.readStatement("x=23; y=42"));
    }

    @Test
    void dependentAssignment() throws Exception {
        var expected = new Assignment(
                rx,
                new Addition(
                        new InputReference(rx, Position.ZERO),
                        i23,
                        Position.ZERO
                ),
                Position.ZERO
        );
        assertEquals(expected, Reader.readStatement("x = @x + 23"));
    }

    @Test
    void anonPiece() throws Exception {
        var expected = new AnonymousPiece(
                new Reference<>("p", Position.ZERO),
                new Assignment(rx, i23, Position.ZERO),
                Position.ZERO
        );
        assertEquals(expected, Reader.readPiece("[ p ] / x = 23"));

        expected = new AnonymousPiece(
                BooleanLiteral.TRUE,
                new Assignment(rx, i23, Position.ZERO),
                Position.ZERO
        );
        assertEquals(expected, Reader.readPiece("/ x = 23"));
    }

    @Test
    void namedPiece() throws Exception {
        var expected = new NamedPiece(
                "piece",
                new Reference<>("p", Position.ZERO),
                new Assignment(rx, i23, Position.ZERO),
                Position.ZERO
        );
        assertEquals(expected, Reader.readPiece("piece: [ p ] / x = 23"));

        expected = new NamedPiece(
                "piece",
                BooleanLiteral.TRUE,
                new Assignment(rx, i23, Position.ZERO),
                Position.ZERO
        );
        assertEquals(expected, Reader.readPiece("piece: / x = 23"));

        expected = new NamedPiece(
                "piece",
                new Reference<>("p", Position.ZERO),
                Skip.INSTANCE,
                Position.ZERO
        );
        assertEquals(expected, Reader.readPiece("piece: [ p ]"));
    }

    @Test
    void testDependentPiece() throws Exception {
        var expected = new AnonymousPiece(
                new Reference<>("p", Position.ZERO),
                new Assignment(
                        rx,
                        new Addition(
                                new InputReference(rx, Position.ZERO),
                                i23,
                                Position.ZERO
                        ),
                        Position.ZERO
                ),
                Position.ZERO
        );
        assertEquals(expected, Reader.readPiece("[ p ] / x = @x + 23"));
    }

    @Test
    void testSoup() throws Exception {
        var expected = new Soup(
                List.of(
                        new VariableDeclaration(
                                "x",
                                i42,
                                Position.ZERO
                        )
                ),
                List.of(
                        new NamedPiece(
                                "p1",
                                new Reference<>("p", Position.ZERO),
                                new Assignment(rx, i23, Position.ZERO),
                                Position.ZERO
                        ),
                        new NamedPiece(
                                "p2",
                                BooleanLiteral.TRUE,
                                new Assignment(rx, i23, Position.ZERO),
                                Position.ZERO
                        )
                ),
                Position.ZERO
        );
        assertEquals(expected, Reader.readSoup("var x = 42 p1: [ p ] / x = 23 | p2: / x = 23"));
    }

    @Test
    void testSoupOnePiece() throws Exception {
        var expected = new Soup(
                List.of(
                        new VariableDeclaration(
                                "x",
                                i42,
                                Position.ZERO
                        )
                ),
                List.of(
                        new NamedPiece(
                                "p1",
                                rx,
                                new Assignment(rx, i42, Position.ZERO),
                                Position.ZERO
                        )
                ),
                Position.ZERO
        );
        assertEquals(expected, Reader.readSoup("var x = 42 p1: [ x ] / x = 42"));
    }

    @Test
    void testSoupNoVars() throws Exception {
        var expected = new Soup(
                Collections.emptyList(),
                List.of(
                        new NamedPiece(
                                "p1",
                                new Reference<>("p", Position.ZERO),
                                new Assignment(rx, i23, Position.ZERO),
                                Position.ZERO
                        ),
                        new NamedPiece(
                                "p2",
                                BooleanLiteral.TRUE,
                                new Assignment(rx, i23, Position.ZERO),
                                Position.ZERO
                        )
                ),
                Position.ZERO
        );
        assertEquals(expected, Reader.readSoup("p1: [ p ] / x = 23 | p2: / x = 23"));
    }

    @Test
    void testSoupNoPieces() throws Exception {
        var expected = new Soup(
                List.of(
                        new VariableDeclaration(
                                "x",
                                i42,
                                Position.ZERO
                        )
                ),
                Collections.emptyList(),
                Position.ZERO
        );
        assertEquals(expected, Reader.readSoup("var x = 42"));
    }

    @Test
    void testPrimedReference() throws Exception {
        var expected = new PrimedReference("x", Position.ZERO);
        assertEquals(expected, Reader.readExpression("x'"));
    }

    @Test
    void testNamedPieceReference() throws Exception {
        var expected = new NamedPieceReference("toto", Position.ZERO);
        assertEquals(expected, Reader.readExpression("p:toto"));
    }

    @Test
    void testEnabledExpression() throws Exception {
        var expected = new EnabledExpression(
                new ParenExpression(
                    new Equal(
                            "==",
                            rx,
                            i23,
                            Position.ZERO
                    ),
                    Position.ZERO),
                Position.ZERO
        );
        assertEquals(expected, Reader.readExpression("enabled (x == 23)"));
    }

    @Test
    void testLink() throws Exception {
        var va = new VariableDeclaration("a", BooleanLiteral.TRUE, Position.ZERO);
        var ra1 = new Reference<VariableDeclaration>("a", Position.ZERO);
        ra1.setDeclaration(va);
        var ra2 = new Reference<VariableDeclaration>("a", Position.ZERO);
        ra2.setDeclaration(va);
        var expected = new Soup(
                List.of(va),
                List.of(
                        new AnonymousPiece(
                                ra1,
                                new Assignment(ra2, i23, Position.ZERO),
                                Position.ZERO
                        )
                ),
                Position.ZERO
        );
        var soup = Reader.readSoup("var a = true [a] / a = 23");
        Reader.link(soup);
        assertEquals(expected, soup);
    }
}
