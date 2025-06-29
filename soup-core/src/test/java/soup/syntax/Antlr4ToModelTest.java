package soup.syntax;

import org.junit.jupiter.api.Test;
import soup.syntax.model.Position;
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Antlr4ToModelTest {
    @Test
    void testLiteral() throws Exception {
        assertAll("",
            () -> assertEquals(BooleanLiteral.TRUE, Reader.readExpression("true")),
            () -> assertEquals(BooleanLiteral.FALSE, Reader.readExpression("false")),
            () -> assertEquals(new IntegerLiteral(23, Position.ZERO), Reader.readExpression("23")),
            () -> assertEquals(new IntegerLiteral(23, Position.ZERO), Reader.readExpression("+23")),
            () -> assertEquals(new IntegerLiteral(-23, Position.ZERO), Reader.readExpression("-23")),
            () -> assertEquals(new DoubleLiteral(23.3, Position.ZERO), Reader.readExpression("23.3")),
            () -> assertEquals(new DoubleLiteral(23.3, Position.ZERO), Reader.readExpression("+23.3")),
            () -> assertEquals(new DoubleLiteral(-23.3, Position.ZERO), Reader.readExpression("-23.3"))
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
                            new IntegerLiteral(-23, Position.ZERO),
                            Position.ZERO),
                Reader.readExpression("--23"));
        assertEquals(
                new PlusExpression(
                        new IntegerLiteral(-23, Position.ZERO),
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
        var i23 = new IntegerLiteral(23, Position.ZERO);
        var i42 = new IntegerLiteral(42, Position.ZERO);
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
}
