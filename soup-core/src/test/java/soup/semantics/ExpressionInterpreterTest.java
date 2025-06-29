package soup.semantics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExpressionInterpreterTest {
    @Test
    void testLiteral() throws Exception {
        assertEquals(true, ExpressionInterpreter.evaluate("true", Collections.emptyMap()));
        assertEquals(false, ExpressionInterpreter.evaluate("false", Collections.emptyMap()));
        assertEquals(23, ExpressionInterpreter.evaluate("23", Collections.emptyMap()));
        assertEquals(23.4, ExpressionInterpreter.evaluate("23.4", Collections.emptyMap()));
        assertEquals(-23, ExpressionInterpreter.evaluate("-23", Collections.emptyMap()));

        assertEquals(Integer.MIN_VALUE-1, ExpressionInterpreter.evaluate(Objects.toString(Integer.MIN_VALUE-1), Collections.emptyMap()));
        assertEquals(Integer.MAX_VALUE, ExpressionInterpreter.evaluate(Objects.toString(Integer.MAX_VALUE), Collections.emptyMap()));

        assertThrows(ParseException.class, () -> ExpressionInterpreter.evaluate(Objects.toString(Double.MIN_VALUE), Collections.emptyMap()));
    }

    @Test
    void testReference() throws Exception {
        assertEquals(true, ExpressionInterpreter.evaluate("x", Map.of("x", true)));
        assertEquals(42, ExpressionInterpreter.evaluate("x", Map.of("x", 42)));
        assertEquals(42, ExpressionInterpreter.evaluate("toto", Map.of("toto", 42)));
        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("x", Collections.emptyMap()));
    }

    @Test
    void testUnary() throws Exception {
        assertEquals(false, ExpressionInterpreter.evaluate("!true", Collections.emptyMap()));
        assertEquals(true, ExpressionInterpreter.evaluate("!false", Collections.emptyMap()));
        assertEquals(42, ExpressionInterpreter.evaluate("--42", Collections.emptyMap()));
        assertEquals(-42, ExpressionInterpreter.evaluate("-(42)", Collections.emptyMap()));
        assertEquals(42, ExpressionInterpreter.evaluate("+(42)", Collections.emptyMap()));
        assertEquals(42, ExpressionInterpreter.evaluate("+42", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("+false", Collections.emptyMap()));
    }

    @Test
    void testBinaryMul() throws Exception {
        assertEquals(966, ExpressionInterpreter.evaluate("23*42", Collections.emptyMap()));
        assertEquals(0, ExpressionInterpreter.evaluate("23*0", Collections.emptyMap()));
        assertEquals(-23, ExpressionInterpreter.evaluate("23*-1", Collections.emptyMap()));

        assertEquals(966.0, ExpressionInterpreter.evaluate("23.0*42.0", Collections.emptyMap()));
        assertEquals(0.0, ExpressionInterpreter.evaluate("23.0*0.0", Collections.emptyMap()));
        assertEquals(-23.0, ExpressionInterpreter.evaluate("23.0*-1.0", Collections.emptyMap()));

        assertEquals(966.0, ExpressionInterpreter.evaluate("23.0*42", Collections.emptyMap()));
        assertEquals(0.0, ExpressionInterpreter.evaluate("23.0*0", Collections.emptyMap()));
        assertEquals(-23.0, ExpressionInterpreter.evaluate("23.0*-1", Collections.emptyMap()));

        assertEquals(966.0, ExpressionInterpreter.evaluate("23*42.0", Collections.emptyMap()));
        assertEquals(0.0, ExpressionInterpreter.evaluate("23*0.0", Collections.emptyMap()));
        assertEquals(-23.0, ExpressionInterpreter.evaluate("23*-1.0", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("23*true", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("false*23", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("false*true", Collections.emptyMap()));
    }

    @Test
    void testBinaryDiv() throws Exception {
        assertEquals(0, ExpressionInterpreter.evaluate("23/42", Collections.emptyMap()));
        assertEquals(23, ExpressionInterpreter.evaluate("23/1", Collections.emptyMap()));
        assertEquals(-23, ExpressionInterpreter.evaluate("23/-1", Collections.emptyMap()));
        assertThrows(ArithmeticException.class, () -> ExpressionInterpreter.evaluate("23/0", Collections.emptyMap()));

        assertEquals(0.547619, (double)ExpressionInterpreter.evaluate("23.0/42.0", Collections.emptyMap()), 0.0000001);
        assertEquals(Double.POSITIVE_INFINITY, ExpressionInterpreter.evaluate("23.0/0.0", Collections.emptyMap()));
        assertEquals(-23.0, ExpressionInterpreter.evaluate("23.0/-1.0", Collections.emptyMap()));

        assertEquals(0.547619, (double)ExpressionInterpreter.evaluate("23.0/42", Collections.emptyMap()), 0.0000001);
        assertEquals(Double.POSITIVE_INFINITY, ExpressionInterpreter.evaluate("23.0/0", Collections.emptyMap()));
        assertEquals(-23.0, ExpressionInterpreter.evaluate("23.0/-1", Collections.emptyMap()));

        assertEquals(0.547619, (double)ExpressionInterpreter.evaluate("23/42.0", Collections.emptyMap()), 0.0000001);
        assertEquals(Double.POSITIVE_INFINITY, ExpressionInterpreter.evaluate("23/0.0", Collections.emptyMap()));
        assertEquals(-23.0, ExpressionInterpreter.evaluate("23/-1.0", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("23/true", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("false/23", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("false/true", Collections.emptyMap()));
    }

    @Test
    void testBinaryMod() throws Exception {
        assertEquals(19, ExpressionInterpreter.evaluate("42%23", Collections.emptyMap()));
        assertEquals(1, ExpressionInterpreter.evaluate("23%2", Collections.emptyMap()));
        assertEquals(0, ExpressionInterpreter.evaluate("23%-1", Collections.emptyMap()));
        assertThrows(ArithmeticException.class, () -> ExpressionInterpreter.evaluate("23%0", Collections.emptyMap()));

        assertEquals(19.0, (double)ExpressionInterpreter.evaluate("42.0%23.0", Collections.emptyMap()), 0.0000001);
        assertEquals(1.0, ExpressionInterpreter.evaluate("23.0%2.0", Collections.emptyMap()));
        assertEquals(0.0, ExpressionInterpreter.evaluate("23.0%-1.0", Collections.emptyMap()));

        assertEquals(19, (double)ExpressionInterpreter.evaluate("42.0%23", Collections.emptyMap()), 0.0000001);
        assertEquals(Double.NaN, ExpressionInterpreter.evaluate("23.0%0", Collections.emptyMap()));
        assertEquals(0.0, ExpressionInterpreter.evaluate("23.0%-1", Collections.emptyMap()));

        assertEquals(19.0, (double)ExpressionInterpreter.evaluate("42%23.0", Collections.emptyMap()), 0.0000001);
        assertEquals(1.0, ExpressionInterpreter.evaluate("23%2.0", Collections.emptyMap()));
        assertEquals(0.0, ExpressionInterpreter.evaluate("23%-1.0", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("23%true", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("false%23", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("false%true", Collections.emptyMap()));
    }

    @Test
    void testBinaryAdd() throws Exception {
        assertEquals(65, ExpressionInterpreter.evaluate("42+23", Collections.emptyMap()));
        assertEquals(65.0, ExpressionInterpreter.evaluate("42.0+23", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("23+true", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("false+23", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("false+true", Collections.emptyMap()));
    }

    @Test
    void testBinaryMinus() throws Exception {
        assertEquals(19, ExpressionInterpreter.evaluate("42-23", Collections.emptyMap()));
        assertEquals(19.0, ExpressionInterpreter.evaluate("42.0-23", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("23-true", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("false-23", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("false-true", Collections.emptyMap()));
    }
}
