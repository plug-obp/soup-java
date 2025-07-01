package soup.semantics;

import obp3.sli.core.MaybeStutter;
import org.junit.jupiter.api.Test;
import soup.syntax.model.expressions.literals.BooleanLiteral;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class RuntimeEnvironmentTest {

    @Test
    void testEqualsRE() throws Exception {
        var e1 = new RuntimeEnvironment(null, Map.of("x", 23));
        var e2 = new RuntimeEnvironment(null, Map.of("x", 23));

        assertEquals(e1, e2);

        e1.model = BooleanLiteral.TRUE;
        e2.model = BooleanLiteral.TRUE;

        assertEquals(e1, e2);

        e1.model = BooleanLiteral.FALSE;
        e2.model = BooleanLiteral.TRUE;
        assertNotEquals(e1, e2);
    }

    @Test
    void testEqualsSRE() throws Exception {
        var e1 = new RuntimeEnvironment(null, Map.of("x", 23));
        var e2 = new RuntimeEnvironment(null, Map.of("x", 23));
        var se1 = new StepRuntimeEnvironment(e1, MaybeStutter.stutter(), e2);
        var se2 = new StepRuntimeEnvironment(e1, MaybeStutter.stutter(), e2);

        assertEquals(se1, se2);
        se1.model = BooleanLiteral.TRUE;
        se2.model = BooleanLiteral.FALSE;
        assertNotEquals(se1, se2);
        se2.model = BooleanLiteral.TRUE;
        assertEquals(se1, se2);
        se2.environment = Map.of("y", 42);
        assertNotEquals(se1, se2);
    }
}
