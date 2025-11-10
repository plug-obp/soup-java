package soup.modelchecker;

import gpsl.semantics.AutomatonSemantics;
import obp3.modelchecking.EmptinessCheckerAnswer;
import obp3.sli.core.operators.product.Product;
import org.junit.jupiter.api.Test;
import soup.syntax.Reader;
import soup.syntax.model.declarations.Soup;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MantrapSoupRegeTest {
    String modelPath = "../soup-models/mantrap/";

    Soup readSoup(String modelName) throws IOException, ParseException {
        return Reader.readSoup(new BufferedReader(new FileReader(modelPath + modelName)));
    }

    @SuppressWarnings("unchecked")
    EmptinessCheckerAnswer<Product<?,?>> mc(Soup model, String property) {
        return (EmptinessCheckerAnswer<Product<?,?>>) SoupRegeModelChecker.soupRegeModelChecker(model, property).runAlone();
    }

    EmptinessCheckerAnswer<Product<?,?>> mc(String modelName, String property) throws IOException, ParseException {
        var model = readSoup(modelName);
        return mc(model, property);
    }

    final String exclusionRege = "τ[true]* ⋅ τ[d1 == 1 ∧ d2 == 1]";
    final String noDeadlockRege = "τ[true]* ⋅ τ [deadlock]";

    final String m3SecViolation = """
                τ[true]*
            ⋅   t[sequence_phase == 0]
            ⋅   t[authorized ∧ d2 == 0 ∧ !mustOpenD1]
            ⋅   t[d2==1]""";

    final String d2NoAuth0 = """
                τ[true]*
            ⋅   t[authorized]
            ⋅   t[d1 == 1]
            ⋅   t[!authorized]*
            ⋅   t[d2==1]""";

    final String d2NoAuth = """
                τ[true]*
            ⋅   t[authorized ∧ mustOpenD1]
            ⋅   t[d1 == 1]
            ⋅   t[!authorized]*
            ⋅   t[d2==1]""";
    // wrong door for phase
    final String phaseViolation = """
                τ[true]* ⋅
           (
              t[sequence_phase == 0  ∧ !mustOpenD1 ∧ d2 == 1]
            | t[sequence_phase == 1  ∧ mustOpenD1 ∧ d1 == 1]
           )
           """;
    // stroger wrong door for phase
    final String phaseViolation1 = """
                τ[true]* ⋅
           (
              t[sequence_phase == 0  ∧ !mustOpenD1] ⋅ t[d2 == 1]
            | t[sequence_phase == 1  ∧ mustOpenD1] ⋅ t[d1 == 1]
           )
           """;

    // Door2 opening implies door1 was previously open
    final String door2ImpliesSequencePhase0 = """
                t[true]* . t[d2==1 ∧ sequence_phase != 0]
            """;

    final String door1ImpliesSequencePhase1 = """    
            t[true]* . t[d1==1 ∧ sequence_phase != 1]
            """;

    final Map<String, Map<String, Result<Boolean>>> models2properties = Map.of(
            "mantrap0.soup", Map.ofEntries(
                    Map.entry(exclusionRege, Result.ok(false)),
                    Map.entry(noDeadlockRege, Result.ok(true)),
                    Map.entry(d2NoAuth0, Result.ok(false))
            ),
            "mantrap1.soup", Map.ofEntries(
                    Map.entry(exclusionRege, Result.ok(true)),
                    Map.entry(noDeadlockRege, Result.ok(true)),
                    Map.entry(d2NoAuth0, Result.ok(true))
            ),
            "mantrap2.soup", Map.ofEntries(
                    Map.entry(exclusionRege, Result.ok(true)),
                    Map.entry(noDeadlockRege, Result.ok(true)),
                    Map.entry(d2NoAuth0, Result.ok(true)),
                    Map.entry(d2NoAuth, Result.ok(true))
            ),
            "mantrap3.soup", Map.ofEntries(
                    Map.entry(exclusionRege, Result.ok(true)),
                    Map.entry(noDeadlockRege, Result.ok(true)),
                    Map.entry(m3SecViolation, Result.ok(true)),
                    Map.entry(d2NoAuth0, Result.ok(true)),
                    Map.entry(d2NoAuth, Result.ok(true)),
                    Map.entry(phaseViolation, Result.ok(true)),
                    Map.entry(phaseViolation1, Result.ok(true)),
                    Map.entry(door2ImpliesSequencePhase0, Result.ok(true)),
                    Map.entry(door1ImpliesSequencePhase1, Result.ok(true))
            )
    );

    @Test
    void testAll() throws Exception {
        for (var entry : models2properties.entrySet()) {
            for (var property : entry.getValue().entrySet()) {
                var propName = "'" + property.getKey() + "'";
                EmptinessCheckerAnswer<Product<?,?>> result = null;
                try {
                    result = mc(entry.getKey(), property.getKey());
                } catch (Exception e){
                    if (property.getValue().isOk()) {
                        fail("Model " + entry.getKey() + " property " + propName + "\nthrows '" + e.getClass() + "' "+ e.getLocalizedMessage());
                    }
                }

                switch (property.getValue()) {
                    case Result.Ok(var v) when v.equals(true) ->
                            assertTrue(result.holds, "Model " + entry.getKey() + " property " + propName);
                    case Result.Ok(var _)->
                            assertFalse(result.holds, "Model " + entry.getKey() + " property " + propName);
                    case Result.Err(var e) ->
                            assertThrows(e.getClass(), () -> mc(entry.getKey(), property.getKey()));
                }
            }
        }
    }
    @Test
    void mantrap3door1ImpliesSequencePhase1() throws Exception {
        var result = mc("mantrap3.soup", door1ImpliesSequencePhase1);
        assertTrue(result.holds);
    }

    @Test
    void mantrap3door2ImpliesSequencePhase0() throws Exception {
        var result = mc("mantrap3.soup", door2ImpliesSequencePhase0);
        assertTrue(result.holds);
    }
}



