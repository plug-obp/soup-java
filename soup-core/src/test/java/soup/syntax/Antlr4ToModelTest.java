package soup.syntax;

import org.junit.jupiter.api.Test;
import soup.syntax.model.Position;
import soup.syntax.model.expressions.Reference;
import soup.syntax.model.expressions.literals.BooleanLiteral;
import soup.syntax.model.expressions.literals.DoubleLiteral;
import soup.syntax.model.expressions.literals.IntegerLiteral;
import soup.syntax.model.expressions.unary.MinusExpression;
import soup.syntax.model.expressions.unary.NotExpression;
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
        assertEquals(BooleanLiteral.TRUE, Reader.readExpression("(true)"));
    }

    @Test
    void textUnary() throws Exception {
        assertEquals(new NotExpression("!", BooleanLiteral.TRUE, Position.ZERO)
                , Reader.readExpression("!true"));
        assertEquals(new MinusExpression(new IntegerLiteral(23, Position.ZERO), Position.ZERO)
                , Reader.readExpression("-(23)"));
        assertEquals(new PlusExpression(new IntegerLiteral(23, Position.ZERO), Position.ZERO)
                , Reader.readExpression("+(23)"));
    }
}
