package soup.semantics;

import org.junit.jupiter.api.Test;
import soup.semantics.base.ExpressionSemantics;

import java.text.ParseException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExpressionInterpreterTest {
    @Test
    void testLiteral() throws Exception {
        assertEquals(true, ExpressionSemantics.evaluate("true", Collections.emptyMap()));
        assertEquals(false, ExpressionSemantics.evaluate("false", Collections.emptyMap()));
        assertEquals(23, ExpressionSemantics.evaluate("23", Collections.emptyMap()));
        assertEquals(23.4, ExpressionSemantics.evaluate("23.4", Collections.emptyMap()));
        assertEquals(-23, ExpressionSemantics.evaluate("-23", Collections.emptyMap()));

        assertEquals(Integer.MIN_VALUE-1, ExpressionSemantics.evaluate(Objects.toString(Integer.MIN_VALUE-1), Collections.emptyMap()));
        assertEquals(Integer.MAX_VALUE, ExpressionSemantics.evaluate(Objects.toString(Integer.MAX_VALUE), Collections.emptyMap()));

        assertThrows(ParseException.class, () -> ExpressionSemantics.evaluate(Objects.toString(Double.MIN_VALUE), Collections.emptyMap()));
    }

    @Test
    void testReference() throws Exception {
        assertEquals(true, ExpressionSemantics.evaluate("x", Map.of("x", true)));
        assertEquals(42, ExpressionSemantics.evaluate("x", Map.of("x", 42)));
        assertEquals(42, ExpressionSemantics.evaluate("toto", Map.of("toto", 42)));
        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("x", Collections.emptyMap()));
    }

    @Test
    void testUnary() throws Exception {
        assertEquals(false, ExpressionSemantics.evaluate("!true", Collections.emptyMap()));
        assertEquals(true, ExpressionSemantics.evaluate("!false", Collections.emptyMap()));
        assertEquals(42, ExpressionSemantics.evaluate("--42", Collections.emptyMap()));
        assertEquals(-42, ExpressionSemantics.evaluate("-(42)", Collections.emptyMap()));
        assertEquals(42, ExpressionSemantics.evaluate("+(42)", Collections.emptyMap()));
        assertEquals(42, ExpressionSemantics.evaluate("+42", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("+false", Collections.emptyMap()));
    }

    @Test
    void testBinaryMul() throws Exception {
        assertEquals(966, ExpressionSemantics.evaluate("23*42", Collections.emptyMap()));
        assertEquals(0, ExpressionSemantics.evaluate("23*0", Collections.emptyMap()));
        assertEquals(-23, ExpressionSemantics.evaluate("23*-1", Collections.emptyMap()));

        assertEquals(966.0, ExpressionSemantics.evaluate("23.0*42.0", Collections.emptyMap()));
        assertEquals(0.0, ExpressionSemantics.evaluate("23.0*0.0", Collections.emptyMap()));
        assertEquals(-23.0, ExpressionSemantics.evaluate("23.0*-1.0", Collections.emptyMap()));

        assertEquals(966.0, ExpressionSemantics.evaluate("23.0*42", Collections.emptyMap()));
        assertEquals(0.0, ExpressionSemantics.evaluate("23.0*0", Collections.emptyMap()));
        assertEquals(-23.0, ExpressionSemantics.evaluate("23.0*-1", Collections.emptyMap()));

        assertEquals(966.0, ExpressionSemantics.evaluate("23*42.0", Collections.emptyMap()));
        assertEquals(0.0, ExpressionSemantics.evaluate("23*0.0", Collections.emptyMap()));
        assertEquals(-23.0, ExpressionSemantics.evaluate("23*-1.0", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("23*true", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("false*23", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("false*true", Collections.emptyMap()));
    }

    @Test
    void testBinaryDiv() throws Exception {
        assertEquals(0, ExpressionSemantics.evaluate("23/42", Collections.emptyMap()));
        assertEquals(23, ExpressionSemantics.evaluate("23/1", Collections.emptyMap()));
        assertEquals(-23, ExpressionSemantics.evaluate("23/-1", Collections.emptyMap()));
        assertThrows(ArithmeticException.class, () -> ExpressionSemantics.evaluate("23/0", Collections.emptyMap()));

        assertEquals(0.547619, (double) ExpressionSemantics.evaluate("23.0/42.0", Collections.emptyMap()), 0.0000001);
        assertEquals(Double.POSITIVE_INFINITY, ExpressionSemantics.evaluate("23.0/0.0", Collections.emptyMap()));
        assertEquals(-23.0, ExpressionSemantics.evaluate("23.0/-1.0", Collections.emptyMap()));

        assertEquals(0.547619, (double) ExpressionSemantics.evaluate("23.0/42", Collections.emptyMap()), 0.0000001);
        assertEquals(Double.POSITIVE_INFINITY, ExpressionSemantics.evaluate("23.0/0", Collections.emptyMap()));
        assertEquals(-23.0, ExpressionSemantics.evaluate("23.0/-1", Collections.emptyMap()));

        assertEquals(0.547619, (double) ExpressionSemantics.evaluate("23/42.0", Collections.emptyMap()), 0.0000001);
        assertEquals(Double.POSITIVE_INFINITY, ExpressionSemantics.evaluate("23/0.0", Collections.emptyMap()));
        assertEquals(-23.0, ExpressionSemantics.evaluate("23/-1.0", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("23/true", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("false/23", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("false/true", Collections.emptyMap()));
    }

    @Test
    void testBinaryMod() throws Exception {
        assertEquals(19, ExpressionSemantics.evaluate("42%23", Collections.emptyMap()));
        assertEquals(1, ExpressionSemantics.evaluate("23%2", Collections.emptyMap()));
        assertEquals(0, ExpressionSemantics.evaluate("23%-1", Collections.emptyMap()));
        assertThrows(ArithmeticException.class, () -> ExpressionSemantics.evaluate("23%0", Collections.emptyMap()));

        assertEquals(19.0, (double) ExpressionSemantics.evaluate("42.0%23.0", Collections.emptyMap()), 0.0000001);
        assertEquals(1.0, ExpressionSemantics.evaluate("23.0%2.0", Collections.emptyMap()));
        assertEquals(0.0, ExpressionSemantics.evaluate("23.0%-1.0", Collections.emptyMap()));

        assertEquals(19, (double) ExpressionSemantics.evaluate("42.0%23", Collections.emptyMap()), 0.0000001);
        assertEquals(Double.NaN, ExpressionSemantics.evaluate("23.0%0", Collections.emptyMap()));
        assertEquals(0.0, ExpressionSemantics.evaluate("23.0%-1", Collections.emptyMap()));

        assertEquals(19.0, (double) ExpressionSemantics.evaluate("42%23.0", Collections.emptyMap()), 0.0000001);
        assertEquals(1.0, ExpressionSemantics.evaluate("23%2.0", Collections.emptyMap()));
        assertEquals(0.0, ExpressionSemantics.evaluate("23%-1.0", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("23%true", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("false%23", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("false%true", Collections.emptyMap()));
    }

    @Test
    void testBinaryAdd() throws Exception {
        assertEquals(65, ExpressionSemantics.evaluate("42+23", Collections.emptyMap()));
        assertEquals(65.0, ExpressionSemantics.evaluate("42.0+23", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("23+true", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("false+23", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("false+true", Collections.emptyMap()));
    }

    @Test
    void testBinaryMinus() throws Exception {
        assertEquals(19, ExpressionSemantics.evaluate("42-23", Collections.emptyMap()));
        assertEquals(19.0, ExpressionSemantics.evaluate("42.0-23", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("23-true", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("false-23", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("false-true", Collections.emptyMap()));
    }

    //test <
    @Test
    void testBinaryLT() throws Exception {
        assertEquals(false, ExpressionSemantics.evaluate("42<23", Collections.emptyMap()));
        assertEquals(true, ExpressionSemantics.evaluate("23<42", Collections.emptyMap()));
        assertEquals(false, ExpressionSemantics.evaluate("23<23", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("true<42", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("42<false", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("true<false", Collections.emptyMap()));
    }
    //test <=
    @Test
    void testBinaryLE() throws Exception {
        assertEquals(false, ExpressionSemantics.evaluate("42<=23", Collections.emptyMap()));
        assertEquals(true, ExpressionSemantics.evaluate("23<=42", Collections.emptyMap()));
        assertEquals(true, ExpressionSemantics.evaluate("23<=23", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("true<=42", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("42<=false", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("true<=false", Collections.emptyMap()));
    }
    //test >
    @Test
    void testBinaryGT() throws Exception {
        assertEquals(true, ExpressionSemantics.evaluate("42>23", Collections.emptyMap()));
        assertEquals(false, ExpressionSemantics.evaluate("23>42", Collections.emptyMap()));
        assertEquals(false, ExpressionSemantics.evaluate("23>23", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("true>42", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("42>false", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("true>false", Collections.emptyMap()));
    }
    //test >=
    @Test
    void testBinaryGE() throws Exception {
        assertEquals(true, ExpressionSemantics.evaluate("42>=23", Collections.emptyMap()));
        assertEquals(false, ExpressionSemantics.evaluate("23>=42", Collections.emptyMap()));
        assertEquals(true, ExpressionSemantics.evaluate("23>=23", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("true>=42", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("42>=false", Collections.emptyMap()));
        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("true>=false", Collections.emptyMap()));
    }
    //test ==
    @Test
    void testBinaryEQ() throws Exception {
        assertEquals(true, ExpressionSemantics.evaluate("true==true", Collections.emptyMap()));
        assertEquals(true, ExpressionSemantics.evaluate("42==42", Collections.emptyMap()));
        assertEquals(true, ExpressionSemantics.evaluate("42.0==42.0", Collections.emptyMap()));

        assertEquals(false, ExpressionSemantics.evaluate("true==false", Collections.emptyMap()));
        assertEquals(false, ExpressionSemantics.evaluate("42==23", Collections.emptyMap()));
        assertEquals(false, ExpressionSemantics.evaluate("42==42.0", Collections.emptyMap()));
        assertEquals(false, ExpressionSemantics.evaluate("42.0==23.0", Collections.emptyMap()));
        assertEquals(false, ExpressionSemantics.evaluate("42==true", Collections.emptyMap()));
    }
    //test !=
    @Test
    void testBinaryNEQ() throws Exception {
        assertEquals(false, ExpressionSemantics.evaluate("true!=true", Collections.emptyMap()));
        assertEquals(false, ExpressionSemantics.evaluate("42!=42", Collections.emptyMap()));
        assertEquals(false, ExpressionSemantics.evaluate("42.0!=42.0", Collections.emptyMap()));

        assertEquals(true, ExpressionSemantics.evaluate("true!=false", Collections.emptyMap()));
        assertEquals(true, ExpressionSemantics.evaluate("42!=23", Collections.emptyMap()));
        assertEquals(true, ExpressionSemantics.evaluate("42!=42.0", Collections.emptyMap()));
        assertEquals(true, ExpressionSemantics.evaluate("42.0!=23.0", Collections.emptyMap()));
        assertEquals(true, ExpressionSemantics.evaluate("42!=true", Collections.emptyMap()));
    }

    //test &&
    @Test
    void testAnd() throws Exception {
        assertEquals(true, ExpressionSemantics.evaluate("true&&true", Collections.emptyMap()));
        assertEquals(false, ExpressionSemantics.evaluate("true&&false", Collections.emptyMap()));
        assertEquals(false, ExpressionSemantics.evaluate("false&&true", Collections.emptyMap()));
        assertEquals(false, ExpressionSemantics.evaluate("false&&false", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("true&&23", Collections.emptyMap()));
    }
    //test ||
    @Test
    void testOR() throws Exception {
        assertEquals(true, ExpressionSemantics.evaluate("true||true", Collections.emptyMap()));
        assertEquals(true, ExpressionSemantics.evaluate("true||false", Collections.emptyMap()));
        assertEquals(true, ExpressionSemantics.evaluate("false||true", Collections.emptyMap()));
        assertEquals(false, ExpressionSemantics.evaluate("false||false", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("true&&23", Collections.emptyMap()));
    }
    //test XOR
    @Test
    void testXOR() throws Exception {
        assertEquals(false, ExpressionSemantics.evaluate("true xor true", Collections.emptyMap()));
        assertEquals(true, ExpressionSemantics.evaluate("true xor false", Collections.emptyMap()));
        assertEquals(true, ExpressionSemantics.evaluate("false xor true", Collections.emptyMap()));
        assertEquals(false, ExpressionSemantics.evaluate("false xor false", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("true&&23", Collections.emptyMap()));
    }
    //test ->
    @Test
    void testImp() throws Exception {
        assertEquals(true, ExpressionSemantics.evaluate("true -> true", Collections.emptyMap()));
        assertEquals(false, ExpressionSemantics.evaluate("true -> false", Collections.emptyMap()));
        assertEquals(true, ExpressionSemantics.evaluate("false -> true", Collections.emptyMap()));
        assertEquals(true, ExpressionSemantics.evaluate("false -> false", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("true&&23", Collections.emptyMap()));
    }
    //test <->
    @Test
    void testEquiv() throws Exception {
        assertEquals(true, ExpressionSemantics.evaluate("true <-> true", Collections.emptyMap()));
        assertEquals(false, ExpressionSemantics.evaluate("true <-> false", Collections.emptyMap()));
        assertEquals(false, ExpressionSemantics.evaluate("false <-> true", Collections.emptyMap()));
        assertEquals(true, ExpressionSemantics.evaluate("false <-> false", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("true&&23", Collections.emptyMap()));
    }

    @Test
    void testCondExp() throws Exception {
        assertEquals(true, ExpressionSemantics.evaluate("true ? true : 23", Collections.emptyMap()));
        assertEquals(23, ExpressionSemantics.evaluate("false ? true : 23", Collections.emptyMap()));

        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("42 ? false : 23", Collections.emptyMap()));
    }

    @Test
    void testRef() throws Exception {
        assertEquals(2, ExpressionSemantics.evaluate("x + 1", Map.of("x", 1, "y", 2)));
        assertEquals(3, ExpressionSemantics.evaluate("x + y", Map.of("x", 1, "y", 2)));

        assertEquals(3, ExpressionSemantics.evaluate("x ? y + 1 : 42", Map.of("x", true, "y", 2)));
        assertEquals(42, ExpressionSemantics.evaluate("x ? y + 1 : 42", Map.of("x", false, "y", 2)));

        assertThrows(RuntimeException.class, () -> ExpressionSemantics.evaluate("x + z", Map.of("x", 1, "y", 2)));
    }
}
