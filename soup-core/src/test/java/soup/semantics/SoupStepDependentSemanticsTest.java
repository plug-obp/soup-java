package soup.semantics;

import obp3.sli.core.operators.product.Step;
import org.junit.jupiter.api.Test;
import soup.semantics.base.Environment;
import soup.semantics.dependent.SoupStepDependentSemantics;
import soup.syntax.Reader;
import soup.syntax.model.Position;
import soup.syntax.model.declarations.pieces.AnonymousPiece;
import soup.syntax.model.declarations.pieces.NamedPiece;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SoupStepDependentSemanticsTest {

    @Test
    void tstS() throws Exception {
        var se = new Environment(null, Map.of("x", 0));
        var te = new Environment(null, Map.of("x", 3));
        var a = Optional.<AnonymousPiece>of(new NamedPiece("piece", null, null, Position.ZERO));
        var step = new Step<>(se, a, te);

        var code = """
                var x = 0;
                | piece: [x == 0 ∧ @x'==3 ] / x = @x' + 1
                """;
        var soup = Reader.readSoup(code);
        var semantics = new SoupStepDependentSemantics(soup);
        var initial = semantics.initial().getFirst();
        var action = semantics.actions(step, initial).getFirst();
        var target = semantics.execute(action, step, initial).getFirst();

        assertEquals(4, target.lookup("x"));
    }
}
