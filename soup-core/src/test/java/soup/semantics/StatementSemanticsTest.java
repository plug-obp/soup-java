package soup.semantics;

import org.junit.jupiter.api.Test;
import soup.syntax.Reader;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StatementSemanticsTest {

    @Test
    public void assign() throws Exception {
        var env0 = new Environment(
                Reader.readStatement("x=42"),
                new HashMap<>(Map.of("x", 23)));

        var env1 = StatementSemantics.evaluate(env0);
        assertEquals(42, env1.lookup("x"));
        assertEquals(env0, env1);

        env0.model = Reader.readStatement("x=23 + 23");
        env1 = StatementSemantics.evaluate(env0);
        assertEquals(46, env1.lookup("x"));
        assertEquals(env0, env1);

        env1.define("y", 23);
        env1.model = Reader.readStatement("x= x - y");
        var env2 = StatementSemantics.evaluate(env0);
        assertEquals(23, env2.lookup("x"));
        assertEquals(env1, env2);
    }

    @Test
    void testIfStmt() throws Exception {
        var env0 = new Environment(Reader.readStatement("if x < 42 then x = 42"), new HashMap<>(Map.of("x", 23)));
        StatementSemantics.evaluate(env0);
        assertEquals(42, env0.lookup("x"));
        env0.model = Reader.readStatement("if x < 42 then x = 52 else x = 23");
        StatementSemantics.evaluate(env0);
        assertEquals(23, env0.lookup("x"));
        env0.model = Reader.readStatement("if x < 42 then x = 52 else x = 23");
        StatementSemantics.evaluate(env0);
        assertEquals(52, env0.lookup("x"));
    }

    @Test
    void testNestedIfStmt() throws Exception {
        var env0 = new Environment(
                Reader.readStatement("if (x < 42) then if (x < 23) then x = 42 else x = 23"),
                new HashMap<>(Map.of("x", 23)));
        StatementSemantics.evaluate(env0);
        assertEquals(23, env0.lookup("x"));
    }

    @Test
    void testIfNoBoolCond() throws Exception {
        var env0 = new Environment(
                Reader.readStatement("if x then x = 42 else x = 23"),
                new HashMap<>(Map.of("x", 23)));
        assertThrows(RuntimeException.class, () -> StatementSemantics.evaluate(env0));
        assertEquals(23, env0.lookup("x"));
    }

    @Test
    void testSeq2() throws Exception {
        var env0 = new Environment(
                Reader.readStatement("x=x+1;x=x+1"),
                new HashMap<>(Map.of("x", 1)));
        StatementSemantics.evaluate(env0);
        assertEquals(3, env0.lookup("x"));
    }

    @Test
    void testSeq3() throws Exception {
        var env0 = new Environment(
                Reader.readStatement("x=x+y; y=x; x=x+1"),
                new HashMap<>(Map.of("x", 1, "y", 2)));
        StatementSemantics.evaluate(env0);
        assertEquals(4, env0.lookup("x"));
        assertEquals(3, env0.lookup("y"));
    }
}
