package soup.modelchecker;

import gpsl.semantics.AutomatonSemantics;
import gpsl.syntax.model.State;
import obp3.modelchecking.EmptinessCheckerAnswer;
import obp3.runtime.sli.IOSematicRelation;
import obp3.sli.core.operators.product.Product;
import org.junit.jupiter.api.Test;
import soup.semantics.base.Environment;
import soup.syntax.Reader;
import soup.syntax.model.declarations.Soup;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class MantrapSoupGPSLTest {
    String modelPath = "../soup-models/mantrap/";

    Soup readSoup(String modelName) throws IOException, ParseException {
        return Reader.readSoup(new BufferedReader(new FileReader(modelPath + modelName)));
    }

    @SuppressWarnings("unchecked")
    EmptinessCheckerAnswer<Product<?,?>> mc(Soup model, String property) {
        return (EmptinessCheckerAnswer<Product<?,?>>)SoupGPSLModelChecker.soupGPSLModelChecker(model, property).runAlone();
    }

    EmptinessCheckerAnswer<Product<?,?>> mc(String modelName, String property) throws IOException, ParseException {
        var model = readSoup(modelName);
        return mc(model, property);
    }

    // Mutual exclusion - both doors never open simultaneously
    final String exclusionPred = "p=!|d1==1 && d2==1|";
    final String exclusion = """
            exclusion = let
                door1_open = |d1==1|,
                door2_open = |d2==1|
            in! [] ¬(door1_open ∧ door2_open)
            """;
    // global deadlock freedom
    final String noDeadlock = "noDeadlock=! []!|deadlock|";

    // Security invariant - unauthorized access never occurs
    final String security = """
            security = let
                door2_open = |d2==1|,
                authorized = |authorized|
            in! [] (door2_open → authorized)
            """;

    final String door1IsOpenOnlyIfAutorized = """
            door1IsOpenOnlyIfAutorized = let
                door1_open = |d1==1|,
                authorized = |authorized|
            in! [] (door1_open → authorized)
            """;

    //liveness: something good will happen eventually

    final String door1EventuallyOpensIfAuthorized = """
            door1EventuallyOpensIfAuthorized = let
                door1_open = |d1==1|,
                authorized = |authorized|
            in! [] ◇ (authorized → ◇ door1_open)
            """;

    final String door2EventuallyOpensIfAuthorized = """
            door2EventuallyOpensIfAuthorized = let
                door2_open = |d2==1|,
                authorized = |authorized|
            in! [] ◇ (authorized → ◇ door2_open)
            """;

    final String alwaysPossibleToProgressSomehow = """
            alwaysPossibleToProgressSomehow = let
                door1_open = |d1==1|,
                door2_open = |d2==1|,
                authorized = |authorized|
            in! [] ◇ (authorized → ◇ (door1_open or door2_open))
            """;
    // Proper sequencing - door2 only opens after door1 cycle
    final String properSequencing = """
           properSequencing = let
                door1_open = |d1==1|,
                door2_open = |d2==1|
           in! [] (door2_open → (¬door2_open U door1_open))
           """;
    // Eventually return to the secure state
    final String eventualSecurity = """
            eventualSecurity = let
                door1_open = |d1==1|,
                door2_open = |d2==1|,
                authorized = |authorized|
            in! ◇ (¬authorized ∧ ¬door1_open ∧ ¬door2_open)
            """;

    //Response Properties (Causes Lead to Effects)
    // Authorization always leads to door1 opening
    final String authLeadsToDoor1Open = """
            authLeadsToDoor1Open = let
                authorized = |authorized|,
                door1_open = |d1==1|
            in! [] (authorized → ◇ door1_open)
            """;

    // Door1 opening eventually leads to closing
    final String door1OpenLeadsToDoor1Close = """
            door1OpenLeadsToDoor1Close = let
                door1_open = |d1==1|
            in! [] (door1_open → ◇ ¬door1_open)
            """;

    // Door2 opening implies door1 was previously open
    final String door2OpenImpliesDoor1WasOpenBefore = """
            door2OpenImpliesDoor1WasOpenBefore = let
                door1_open = |d1==1|,
                door2_open = |d2==1|
            in! [] (door2_open → ◇⁻ door1_open)  // Past-time LTL
            """;

    //tailgating prevention
final String tailgatingPrevention = """
            tailgatingPrevention = let
                door1_open = |d1==1|,
                door1_closed = |d1==0|,
                authorized = |authorized|
            in! [] ((door1_open ∧ ¬authorized) → ◇ door1_closed)
            """;

// Complete access sequence
final String accessSequence = """
            accessSequence = let
                authorized = |authorized|,
                door1_open = |d1==1|,
                door2_open = |d2==1|
    in! [] ( authorized →
            ( (authorized U door1_open) ∨ ◇¬authorized ) →
            ◇ ( door1_open ∧
                ( (authorized U ¬door1_open) ∨ ◇¬authorized ) →
                ◇ ( ¬door1_open ∧
                    ( (authorized U door2_open) ∨ ◇¬authorized )
                )
            )
        )
    """;

//If you stay authorized long enough, you'll get a complete sequence within bounded time
final String enoughAuthorizationGetsYouThrough ="""
    enoughAuthorizationGetsYouThrough = let
        authorized = |authorized|,
        door1_open = |d1==1|,
        door2_open = |d2==1|
    in!
           [] ( (authorized ∧ |sequence_phase==0| ∧ |mustOpenD1|) →
                        ◇ (
                            // Either we lose the preconditions (so obligation ends)
                            ¬(authorized ∧ |sequence_phase==0| ∧ |mustOpenD1|)
                            // OR the sequence starts
                            ∨ (door1_open ∧ |sequence_phase==1|)
                        )
                   ) ∧
                   [] ( (door1_open ∧ |sequence_phase==1|) →
                        ◇ (
                            ¬door1_open
                            ∨ (|sequence_phase==1| ∧ door2_open) 
                        )
                   ) ∧
                   [] ( (|sequence_phase==1| ∧ door2_open) →
                        ◇ door2_open )
""";

// Authorization behavior
final String authWithoutDoor1OpenLeadsToNotAuthorizedOrDoor1Open = """
            authWithoutDoor1OpenLeadsToNotAuthorizedOrDoor1Open = let
                door1_open = |d1==1|,
                authorized = |authorized|
            in! [] ((authorized ∧ ¬door1_open) → ◇ (¬authorized ∨ door1_open))
            """;

    final Map<String, Map<String, Result<Boolean>>> models2properties = Map.of(
            "mantrap0.soup", Map.ofEntries(
                    Map.entry(exclusion, Result.ok(false)),
                    Map.entry(noDeadlock, Result.ok(true)),
                    Map.entry(security, Result.ok(false)),
                    Map.entry(door1IsOpenOnlyIfAutorized, Result.ok(false)),
                    Map.entry(door1EventuallyOpensIfAuthorized, Result.ok(false)),
                    Map.entry(door2EventuallyOpensIfAuthorized, Result.ok(false)),
                    Map.entry(alwaysPossibleToProgressSomehow, Result.ok(true)),
                    Map.entry(properSequencing, Result.ok(false)),
                    Map.entry(eventualSecurity, Result.ok(true)),
                    Map.entry(authLeadsToDoor1Open, Result.ok(false)),
                    Map.entry(door1OpenLeadsToDoor1Close, Result.ok(false)),
                    Map.entry(door2OpenImpliesDoor1WasOpenBefore, Result.err(new IllegalArgumentException())),
                    Map.entry(tailgatingPrevention, Result.ok(false)),
                    Map.entry(accessSequence, Result.ok(false)),
                    Map.entry(authWithoutDoor1OpenLeadsToNotAuthorizedOrDoor1Open, Result.ok(false))
            ),
            "mantrap1.soup", Map.ofEntries(
                    Map.entry(exclusion, Result.ok(true)),
                    Map.entry(noDeadlock, Result.ok(true)),
                    Map.entry(security, Result.ok(false)),
                    Map.entry(door1IsOpenOnlyIfAutorized, Result.ok(false)),
                    Map.entry(door1EventuallyOpensIfAuthorized, Result.ok(false)),
                    Map.entry(door2EventuallyOpensIfAuthorized, Result.ok(false)),
                    Map.entry(alwaysPossibleToProgressSomehow, Result.ok(true)),
                    Map.entry(properSequencing, Result.ok(false)),
                    Map.entry(eventualSecurity, Result.ok(true)),
                    Map.entry(authLeadsToDoor1Open, Result.ok(false)),
                    Map.entry(door1OpenLeadsToDoor1Close, Result.ok(false)),
                    Map.entry(door2OpenImpliesDoor1WasOpenBefore, Result.err(new IllegalArgumentException())),
                    Map.entry(tailgatingPrevention, Result.ok(false)),
                    Map.entry(accessSequence, Result.ok(false)),
                    Map.entry(authWithoutDoor1OpenLeadsToNotAuthorizedOrDoor1Open, Result.ok(false))
            ),
            "mantrap2.soup", Map.ofEntries(
                    Map.entry(exclusion, Result.ok(true)),
                    Map.entry(noDeadlock, Result.ok(true)),
                    Map.entry(security, Result.ok(false)),
                    Map.entry(door1IsOpenOnlyIfAutorized, Result.ok(false)),
                    Map.entry(door1EventuallyOpensIfAuthorized, Result.ok(true)),
                    Map.entry(door2EventuallyOpensIfAuthorized, Result.ok(true)),
                    Map.entry(alwaysPossibleToProgressSomehow, Result.ok(true)),
                    Map.entry(properSequencing, Result.ok(false)),
                    Map.entry(eventualSecurity, Result.ok(true)),
                    Map.entry(authLeadsToDoor1Open, Result.ok(false)),
                    Map.entry(door1OpenLeadsToDoor1Close, Result.ok(true)),
                    Map.entry(door2OpenImpliesDoor1WasOpenBefore, Result.err(new IllegalArgumentException())),
                    Map.entry(tailgatingPrevention, Result.ok(true)),
                    Map.entry(accessSequence, Result.ok(true)),
                    Map.entry(enoughAuthorizationGetsYouThrough, Result.err(new AutomatonSemantics.GuardEvaluationException("", null))),
                    Map.entry(authWithoutDoor1OpenLeadsToNotAuthorizedOrDoor1Open, Result.ok(true))
            ),
            "mantrap3.soup", Map.ofEntries(
                    Map.entry(exclusion, Result.ok(true)),
                    Map.entry(noDeadlock, Result.ok(true)),
                    Map.entry(security, Result.ok(false)),
                    Map.entry(door1IsOpenOnlyIfAutorized, Result.ok(false)),
                    Map.entry(door1EventuallyOpensIfAuthorized, Result.ok(true)),
                    Map.entry(door2EventuallyOpensIfAuthorized, Result.ok(false)),//x
                    Map.entry(alwaysPossibleToProgressSomehow, Result.ok(true)),
                    Map.entry(properSequencing, Result.ok(false)),
                    Map.entry(eventualSecurity, Result.ok(true)),
                    Map.entry(authLeadsToDoor1Open, Result.ok(false)),
                    Map.entry(door1OpenLeadsToDoor1Close, Result.ok(true)),
                    Map.entry(door2OpenImpliesDoor1WasOpenBefore, Result.err(new IllegalArgumentException())),
                    Map.entry(tailgatingPrevention, Result.ok(true)), //x
                    Map.entry(accessSequence, Result.ok(true)), //x
                    Map.entry(enoughAuthorizationGetsYouThrough, Result.ok(true)),
                    Map.entry(authWithoutDoor1OpenLeadsToNotAuthorizedOrDoor1Open, Result.ok(true))
            )
    );

    @Test
    void testAll() throws Exception {
        for (var entry : models2properties.entrySet()) {
            for (var property : entry.getValue().entrySet()) {
                var propName = "'" + property.getKey().split("=")[0].trim() + "'";
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

    /// mantrap 0
    @Test
    void mantrap0ExclusionPred() throws Exception {
        var result = mc("mantrap0.soup", exclusionPred);
        assertFalse(result.holds);
        var soupWitness = ((Environment)result.witness.start().l());
        assertEquals( soupWitness.lookup("d1"), soupWitness.lookup("d2"));
        var propWitness = ((State)result.witness.end().r());
        assertEquals("x", propWitness.name());
        assertEquals(5, result.trace.size());
    }

    @Test
    void mantrap0ExclusionLTL() throws Exception {
        var result = mc("mantrap0.soup", exclusion);
        assertFalse(result.holds);
        var soupWitness = ((Environment)result.witness.start().l());
        assertEquals( soupWitness.lookup("d1"), soupWitness.lookup("d2"));
        var propWitness = ((State)result.witness.end().r());
        assertTrue(propWitness.name().startsWith("accept"));
        assertEquals(7, result.trace.size());
    }

    @Test
    void mantrap0NoDeadlock() throws Exception {
        var result = mc("mantrap0.soup", noDeadlock);
        assertTrue(result.holds);
    }

    @Test
    void mantrap1Exclusion() throws Exception {
        var result = mc("mantrap1.soup", exclusion);
        assertTrue(result.holds);
    }

    @Test
    void mantrap1NoDeadlock() throws Exception {
        var result = mc("mantrap1.soup", noDeadlock);
        assertTrue(result.holds);
    }

    @Test
    void mantrap2Exclusion() throws Exception {
        var result = mc("mantrap2.soup", exclusion);
        assertTrue(result.holds);
    }
}
