package soup.semantics;

import org.junit.jupiter.api.Test;
import soup.syntax.Reader;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SoupSemanticsTest {

    @Test
    void testInitial() throws Exception {
        var soup = Reader.readSoup("var x = 23; y=42");
        var env = new SoupSemantics(soup).initial().get(0);
        assertEquals(23, env.lookup("x"));
        assertEquals(42, env.lookup("y"));
    }

    @Test
    void testInitialExpr() throws Exception {
        var soup = Reader.readSoup("var x = 23 + 42; y = x - 23; z = x < 42");
        var env = new SoupSemantics(soup).initial().get(0);
        assertEquals(65, env.lookup("x"));
        assertEquals(42, env.lookup("y"));
        assertEquals(false, env.lookup("z"));
    }
}
