package soup.semantics;

import org.junit.jupiter.api.Test;
import soup.semantics.base.SoupSemantics;
import soup.syntax.Reader;
import soup.syntax.model.declarations.pieces.NamedPiece;

import static org.junit.jupiter.api.Assertions.*;

public class SoupSemanticsTest {

    @Test
    void testInitial() throws Exception {
        var soup = Reader.readSoup("var x = 23; y=42");
        var env = new SoupSemantics(soup).initial().getFirst();
        assertEquals(23, env.lookup("x"));
        assertEquals(42, env.lookup("y"));
    }

    @Test
    void testInitialExpr() throws Exception {
        var soup = Reader.readSoup("var x = 23 + 42; y = x - 23; z = x < 42");
        var env = new SoupSemantics(soup).initial().getFirst();
        assertEquals(65, env.lookup("x"));
        assertEquals(42, env.lookup("y"));
        assertEquals(false, env.lookup("z"));
    }

    @Test
    void testActions1Throws() throws Exception {
        var soup = Reader.readSoup("var x = 23; p1: [ x ] / x = 42");
        var semantics = new SoupSemantics(soup);
        var env = semantics.initial().getFirst();
        assertThrows(RuntimeException.class, () -> semantics.actions(env));
    }

    @Test
    void testOnePiecesEnabled() throws Exception {
        var soup = Reader.readSoup("var x = 23; p1: [ x < 25 ] / x = 42");
        var semantics = new SoupSemantics(soup);
        var env = semantics.initial().getFirst();
        var actions = semantics.actions(env);
        assertEquals(1, actions.size());
        assertEquals("p1", ((NamedPiece)actions.getFirst()).name);
    }

    @Test
    void testTwoPiecesEnabled() throws Exception {
        var soup = Reader.readSoup("var x = 23; p1: [ x < 25 ] / x = 42 | p2: [ true ] / x = 42");
        var semantics = new SoupSemantics(soup);
        var env = semantics.initial().getFirst();
        var actions = semantics.actions(env);
        assertEquals(2, actions.size());
        assertEquals("p1", ((NamedPiece)actions.get(0)).name);
        assertEquals("p2", ((NamedPiece)actions.get(1)).name);
    }

    @Test
    void testNoPiecesEnabled() throws Exception {
        var soup = Reader.readSoup("var x = 23; p1: [ x < 23 ] / x = 42");
        var semantics = new SoupSemantics(soup);
        var env = semantics.initial().getFirst();
        var actions = semantics.actions(env);
        assertEquals(0, actions.size());
    }

    @Test
    void testNoExecuteImpure() throws Exception {
        var soup = Reader.readSoup("var x = 23; p1: [ x < 25 ] / x = 42");
        var semantics = new SoupSemantics(soup);
        var env = semantics.initial().getFirst();
        assertEquals(23, env.lookup("x"));
        var action = semantics.actions(env).getFirst();
        var env1 = semantics.execute(action, env).getFirst();
        assertEquals(42, env.lookup("x"));
        assertEquals(42, env1.lookup("x"));
        assertSame(env, env1);
    }

    @Test
    void testNoExecutePure() throws Exception {
        var soup = Reader.readSoup("var x = 23; p1: [ x < 25 ] / x = 42");
        var semantics = new SoupSemantics(soup).pureSemantics();
        var env = semantics.initial().getFirst();
        assertEquals(23, env.lookup("x"));
        var action = semantics.actions(env).getFirst();
        var env1 = semantics.execute(action, env).getFirst();
        assertEquals(23, env.lookup("x"));
        assertEquals(42, env1.lookup("x"));
        assertNotSame(env, env1);
    }
}
