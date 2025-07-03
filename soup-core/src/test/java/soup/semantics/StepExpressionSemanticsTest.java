package soup.semantics;

import org.junit.jupiter.api.Test;
import soup.semantics.base.Environment;
import soup.semantics.diagnosis.StepEnvironment;
import soup.semantics.diagnosis.StepExpressionSemantics;
import soup.syntax.model.Position;
import soup.syntax.model.declarations.pieces.NamedPiece;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class StepExpressionSemanticsTest {

    @Test
    void testStepRef() throws Exception {
        var env = new StepEnvironment(
                new Environment(null, Map.of("x", 23)),
                null
        );
        assertEquals(23, StepExpressionSemantics.evaluate("x", env));
    }

    @Test
    void testStepDeadlock() throws Exception {
        var env = new StepEnvironment(
                new Environment(null, Map.of("x", 23)),
                null
        );
        assertFalse(env.isSoupAction());
        assertEquals(false, StepExpressionSemantics.evaluate("deadlock", env));

        env.target = new Environment(env);
        assertEquals(true, StepExpressionSemantics.evaluate("deadlock", env));

        env.action = Optional.of(new NamedPiece("x", null, null, Position.ZERO));
        assertEquals(false, StepExpressionSemantics.evaluate("deadlock", env));
    }

    @Test
    void testStepRefPrime() throws Exception {
        var env = new StepEnvironment(
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
                new NamedPiece("p1", null, null, Position.ZERO),
                null);
        assertTrue((boolean)StepExpressionSemantics.evaluate("p:p1", env));
        assertFalse((boolean)StepExpressionSemantics.evaluate("p:p2", env));
        env.action = Optional.empty();
        assertFalse((boolean)StepExpressionSemantics.evaluate("p:p1", env));
        assertFalse((boolean)StepExpressionSemantics.evaluate("p:p2", env));
    }

    @Test
    void testEnabled() throws Exception {
        var env = new StepEnvironment(
                new Environment(null, Map.of("x", 23)),
                new NamedPiece("p1", null, null, Position.ZERO),
                new Environment(null, Map.of("x", 42)));
        assertTrue((boolean)StepExpressionSemantics.evaluate("enabled p:p1", env));
        assertFalse((boolean)StepExpressionSemantics.evaluate("enabled p:p2", env));
        assertTrue((boolean)StepExpressionSemantics.evaluate("enabled x' > x", env));
        assertTrue((boolean)StepExpressionSemantics.evaluate("enabled x' == x + 19", env));
        assertTrue((boolean)StepExpressionSemantics.evaluate("enabled x' > x && p:p1", env));
        assertFalse((boolean)StepExpressionSemantics.evaluate("enabled x' < x", env));
    }
}
