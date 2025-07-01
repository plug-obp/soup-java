package soup.semantics;

import obp3.sli.core.MaybeStutter;
import org.junit.jupiter.api.Test;
import soup.syntax.model.Position;
import soup.syntax.model.declarations.pieces.NamedPiece;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StepExpressionSemanticsTest {

    @Test
    void testStepRef() throws Exception {
        var env = new StepEnvironment(
                new Environment(null, Map.of("x", 23)),
                null,
                null
        );
        assertEquals(23, StepExpressionSemantics.evaluate("x", env));
    }

    @Test
    void testStepDeadlock() throws Exception {
        var env = new StepEnvironment(
                new Environment(null, Map.of("x", 23)),
                null,
                null
        );
        assertFalse(env.isSoupAction());
        assertEquals(false, StepExpressionSemantics.evaluate("deadlock", env));

        env.target = new Environment(env);
        assertEquals(false, StepExpressionSemantics.evaluate("deadlock", env));

        env.action = MaybeStutter.stutter();
        assertEquals(true, StepExpressionSemantics.evaluate("deadlock", env));

        env.action = MaybeStutter.of(new NamedPiece("x", null, null, Position.ZERO));
        assertEquals(false, StepExpressionSemantics.evaluate("deadlock", env));
    }

    @Test
    void testStepRefPrime() throws Exception {
        var env = new StepEnvironment(
                null,
                null,
                new Environment(null, Map.of("x", 23))
        );
        assertEquals(23, StepExpressionSemantics.evaluate("x'", env));

        assertThrows(RuntimeException.class, () -> StepExpressionSemantics.evaluate("y'", env));
    }

    @Test
    void testNamedPieceRef() throws Exception {
        var env = new StepEnvironment(
                null,
                MaybeStutter.of(new NamedPiece("p1", null, null, Position.ZERO)),
                null);
        assertTrue((boolean)StepExpressionSemantics.evaluate("p:p1", env));
        assertFalse((boolean)StepExpressionSemantics.evaluate("p:p2", env));
        env.action = MaybeStutter.stutter();
        assertFalse((boolean)StepExpressionSemantics.evaluate("p:p1", env));
        assertFalse((boolean)StepExpressionSemantics.evaluate("p:p2", env));
    }

    @Test
    void testEnabled() throws Exception {
        var env = new StepEnvironment(
                new Environment(null, Map.of("x", 23)),
                MaybeStutter.of(new NamedPiece("p1", null, null, Position.ZERO)),
                new Environment(null, Map.of("x", 42)));
        assertTrue((boolean)StepExpressionSemantics.evaluate("enabled p:p1", env));
        assertFalse((boolean)StepExpressionSemantics.evaluate("enabled p:p2", env));
        assertTrue((boolean)StepExpressionSemantics.evaluate("enabled x' > x", env));
        assertTrue((boolean)StepExpressionSemantics.evaluate("enabled x' == x + 19", env));
        assertTrue((boolean)StepExpressionSemantics.evaluate("enabled x' > x && p:p1", env));
        assertFalse((boolean)StepExpressionSemantics.evaluate("enabled x' < x", env));
    }
}
