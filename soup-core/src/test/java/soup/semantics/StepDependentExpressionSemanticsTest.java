package soup.semantics;

import obp3.runtime.sli.Step;
import org.junit.jupiter.api.Test;
import soup.semantics.base.Environment;
import soup.semantics.dependent.StepDependentEnvironment;
import soup.semantics.dependent.StepDependentExpressionSemantics;
import soup.semantics.diagnosis.StepExpressionSemantics;
import soup.syntax.Reader;
import soup.syntax.model.Position;
import soup.syntax.model.declarations.pieces.AnonymousPiece;
import soup.syntax.model.declarations.pieces.NamedPiece;
import soup.syntax.model.expressions.Expression;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StepDependentExpressionSemanticsTest {

    @Test
    void dependentExpressionEvaluation() {
        var se = new Environment(null, Map.of("x", 0));
        var te = new Environment(null, Map.of("x", 3));
        var a = Optional.<AnonymousPiece>of(new NamedPiece("piece", null, null, Position.ZERO));
        var step = new Step<>(se, a, te);
        var environment = new StepDependentEnvironment(step, new Environment());
        Function<String, Object> evaluate = (code) -> {
            Expression exp = null;
            try {
                exp = Reader.readExpression(code);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            var sem = new StepDependentExpressionSemantics(new StepExpressionSemantics());
          return sem.evaluate(exp, environment);
        };
        assertEquals(true, evaluate.apply("@true"));
        assertEquals(0, evaluate.apply("@x"));
        assertEquals(3, evaluate.apply("@x'"));
        assertEquals(true, evaluate.apply("@p:piece"));
        assertEquals(false, evaluate.apply("@p:no"));
        assertEquals(true, evaluate.apply("@x==0"));
        assertEquals(true, evaluate.apply("@(x==0)"));
        assertEquals(false, evaluate.apply("@x==3"));
        assertEquals(true, evaluate.apply("@(x'==x+3)"));

        environment.define("x", 5);
        assertEquals(8, evaluate.apply("x+@x'"));
        assertEquals(true, evaluate.apply("@p:piece ∧ x==5"));
    }
}
