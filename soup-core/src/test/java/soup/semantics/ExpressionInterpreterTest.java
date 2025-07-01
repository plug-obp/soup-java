package soup.semantics;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Collections;
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

    //test <
    @Test
    void testBinaryLT() throws Exception {
        assertEquals(false, ExpressionInterpreter.evaluate("42<23", Collections.emptyMap()));
        assertEquals(true, ExpressionInterpreter.evaluate("23<42", Collections.emptyMap()));
        assertEquals(false, ExpressionInterpreter.evaluate("23<23", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("true<42", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("42<false", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("true<false", Collections.emptyMap()));
    }
    //test <=
    @Test
    void testBinaryLE() throws Exception {
        assertEquals(false, ExpressionInterpreter.evaluate("42<=23", Collections.emptyMap()));
        assertEquals(true, ExpressionInterpreter.evaluate("23<=42", Collections.emptyMap()));
        assertEquals(true, ExpressionInterpreter.evaluate("23<=23", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("true<=42", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("42<=false", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("true<=false", Collections.emptyMap()));
    }
    //test >
    @Test
    void testBinaryGT() throws Exception {
        assertEquals(true, ExpressionInterpreter.evaluate("42>23", Collections.emptyMap()));
        assertEquals(false, ExpressionInterpreter.evaluate("23>42", Collections.emptyMap()));
        assertEquals(false, ExpressionInterpreter.evaluate("23>23", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("true>42", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("42>false", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("true>false", Collections.emptyMap()));
    }
    //test >=
    @Test
    void testBinaryGE() throws Exception {
        assertEquals(true, ExpressionInterpreter.evaluate("42>=23", Collections.emptyMap()));
        assertEquals(false, ExpressionInterpreter.evaluate("23>=42", Collections.emptyMap()));
        assertEquals(true, ExpressionInterpreter.evaluate("23>=23", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("true>=42", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("42>=false", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("true>=false", Collections.emptyMap()));
    }
    //test ==
    @Test
    void testBinaryEQ() throws Exception {
        assertEquals(true, ExpressionInterpreter.evaluate("true==true", Collections.emptyMap()));
        assertEquals(true, ExpressionInterpreter.evaluate("42==42", Collections.emptyMap()));
        assertEquals(true, ExpressionInterpreter.evaluate("42.0==42.0", Collections.emptyMap()));

        assertEquals(false, ExpressionInterpreter.evaluate("true==false", Collections.emptyMap()));
        assertEquals(false, ExpressionInterpreter.evaluate("42==23", Collections.emptyMap()));
        assertEquals(false, ExpressionInterpreter.evaluate("42==42.0", Collections.emptyMap()));
        assertEquals(false, ExpressionInterpreter.evaluate("42.0==23.0", Collections.emptyMap()));
        assertEquals(false, ExpressionInterpreter.evaluate("42==true", Collections.emptyMap()));
    }
    //test !=
    @Test
    void testBinaryNEQ() throws Exception {
        assertEquals(false, ExpressionInterpreter.evaluate("true!=true", Collections.emptyMap()));
        assertEquals(false, ExpressionInterpreter.evaluate("42!=42", Collections.emptyMap()));
        assertEquals(false, ExpressionInterpreter.evaluate("42.0!=42.0", Collections.emptyMap()));

        assertEquals(true, ExpressionInterpreter.evaluate("true!=false", Collections.emptyMap()));
        assertEquals(true, ExpressionInterpreter.evaluate("42!=23", Collections.emptyMap()));
        assertEquals(true, ExpressionInterpreter.evaluate("42!=42.0", Collections.emptyMap()));
        assertEquals(true, ExpressionInterpreter.evaluate("42.0!=23.0", Collections.emptyMap()));
        assertEquals(true, ExpressionInterpreter.evaluate("42!=true", Collections.emptyMap()));
    }

    //test &&
    @Test
    void testAnd() throws Exception {
        assertEquals(true, ExpressionInterpreter.evaluate("true&&true", Collections.emptyMap()));
        assertEquals(false, ExpressionInterpreter.evaluate("true&&false", Collections.emptyMap()));
        assertEquals(false, ExpressionInterpreter.evaluate("false&&true", Collections.emptyMap()));
        assertEquals(false, ExpressionInterpreter.evaluate("false&&false", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("true&&23", Collections.emptyMap()));
    }
    //test ||
    @Test
    void testOR() throws Exception {
        assertEquals(true, ExpressionInterpreter.evaluate("true||true", Collections.emptyMap()));
        assertEquals(true, ExpressionInterpreter.evaluate("true||false", Collections.emptyMap()));
        assertEquals(true, ExpressionInterpreter.evaluate("false||true", Collections.emptyMap()));
        assertEquals(false, ExpressionInterpreter.evaluate("false||false", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("true&&23", Collections.emptyMap()));
    }
    //test XOR
    @Test
    void testXOR() throws Exception {
        assertEquals(false, ExpressionInterpreter.evaluate("true xor true", Collections.emptyMap()));
        assertEquals(true, ExpressionInterpreter.evaluate("true xor false", Collections.emptyMap()));
        assertEquals(true, ExpressionInterpreter.evaluate("false xor true", Collections.emptyMap()));
        assertEquals(false, ExpressionInterpreter.evaluate("false xor false", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("true&&23", Collections.emptyMap()));
    }
    //test ->
    @Test
    void testImp() throws Exception {
        assertEquals(true, ExpressionInterpreter.evaluate("true -> true", Collections.emptyMap()));
        assertEquals(false, ExpressionInterpreter.evaluate("true -> false", Collections.emptyMap()));
        assertEquals(true, ExpressionInterpreter.evaluate("false -> true", Collections.emptyMap()));
        assertEquals(true, ExpressionInterpreter.evaluate("false -> false", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("true&&23", Collections.emptyMap()));
    }
    //test <->
    @Test
    void testEquiv() throws Exception {
        assertEquals(true, ExpressionInterpreter.evaluate("true <-> true", Collections.emptyMap()));
        assertEquals(false, ExpressionInterpreter.evaluate("true <-> false", Collections.emptyMap()));
        assertEquals(false, ExpressionInterpreter.evaluate("false <-> true", Collections.emptyMap()));
        assertEquals(true, ExpressionInterpreter.evaluate("false <-> false", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("true&&23", Collections.emptyMap()));
    }

    @Test
    void testCondExp() throws Exception {
        assertEquals(true, ExpressionInterpreter.evaluate("true ? true : 23", Collections.emptyMap()));
        assertEquals(23, ExpressionInterpreter.evaluate("false ? true : 23", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("42 ? false : 23", Collections.emptyMap()));
    }

    @Test
    void testRef() throws Exception {
        assertEquals(2, ExpressionInterpreter.evaluate("x + 1", Map.of("x", 1, "y", 2)));
        assertEquals(3, ExpressionInterpreter.evaluate("x + y", Map.of("x", 1, "y", 2)));

        assertEquals(3, ExpressionInterpreter.evaluate("x ? y + 1 : 42", Map.of("x", true, "y", 2)));
        assertEquals(42, ExpressionInterpreter.evaluate("x ? y + 1 : 42", Map.of("x", false, "y", 2)));

        assertThrows(RuntimeException.class, () -> ExpressionInterpreter.evaluate("x + z", Map.of("x", 1, "y", 2)));
    }
}
