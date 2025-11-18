package soup.modelchecker;

import gpsl.semantics.AutomatonSemantics;
import gpsl.syntax.model.State;
import obp3.modelchecking.EmptinessCheckerAnswer;
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

import static org.junit.jupiter.api.Assertions.*;

public class MantrapSoupGPSLTest {
    // Mutual exclusion - both doors never open simultaneously
    final String exclusionPred = "p =! !|d1==1 && d2==1|";
    final String exclusion = """
            exclusion = let
                door1_open = |d1==1|,
                door2_open = |d2==1|
            in! [] ¬(door1_open ∧ door2_open)
            """;
    // global deadlock freedom
    final String noDeadlock = "noDeadlock=! []!|deadlock|";
    // Security invariant - unauthorized access never occurs
    //If Door2 is open without authorization, it must close in the next step
    final String security = """
            security = let
                door2_open = |d2==1|,
                authorized = |authorized|
            in! [] ( (door2_open ∧ ¬authorized) → ◯(|d2==0|) ) //[] (door2_open → authorized)
            """;

    final String securityDoor1 = """
            security = let
                door1_open = |d1==1|,
                authorized = |authorized|
            in! [] ( (door1_open ∧ ¬authorized) → ◯(|d1==0|) ) //[] (door2_open → authorized)
            """;

    final String door1IsOpenOnlyIfAutorized = """
            door1IsOpenOnlyIfAutorized = let
                door1_open = |d1==1|,
                authorized = |authorized|
            in! [] ((door1_open ∧ ¬authorized) → <>(|d1==0|) )
            """;
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

    //liveness: something good will happen eventually
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

    final String authLeadsToDoor2Open = """
            authLeadsToDoor1Open = let
                authorized = |authorized|,
                door1_open = |d1==1|
            in! [] (authorized → ◇ door1_open)
            """;

    final String authLeadsToDoor1OpenM2 = """
            authLeadsToDoor1OpenM2 = let
                authorized = |authorized|,
                door1_open = |d1==1|
            in! [] ((authorized ∧ |mustOpenD1| ∧ |sequence_phase==1|) → ◇ door1_open)
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

    // in solution 3, the phases can be used to check if the door opens in the right phase
    final String doorsOpensInTheRightPhase = """
            doorsOpensInTheRightPhase = let
                door1_open = |d1==1|,
                door2_open = |d2==1|,
                phase0 = |sequence_phase==0|,
                phase1 = |sequence_phase==1|
            in! [] ((door1_open → phase1) ∧ (door2_open → phase0))
            """;
    final String phase1LeadsToDoor1Open = """
            phase1LeadsToDoor1Open = let
                phase1 = |sequence_phase==1|,
                door1_open = |d1==1|,
            in! [] (phase1 → ◇ door1_open)
    """;

    final String phase0LeadsToDoor2Open = """
            phase0LeadsToDoor2Open = let
                phase0 = |sequence_phase==0|,
                door2_open = |d2==1|,
            in! [] (phase0 → ◇ door2_open)
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
    final String enoughAuthorizationGetsYouThrough = """
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

    final String authWithoutDoor2OpenLeadsToNotAuthorizedOrDoor2Open = """
            authWithoutDoor1OpenLeadsToNotAuthorizedOrDoor1Open = let
                door2_open = |d2==1|,
                authorized = |authorized|
            in! [] ((authorized ∧ ¬door2_open) → ◇ (¬authorized ∨ door2_open))
            """;

    final String door1InfinitelyOften = """
            door1InfinitelyOften =! []◇ |d1==1|
            """;
    final String everyPhase0LeadsToD1Opening = """
            everyPhase0LeadsToD1Opening =! [] (|sequence_phase==0| → ◇ |d1==1|)
            """;
    final String resetDoesNotShortcircuit = """
            resetDoesNotShortcircuit =! [] ((|sequence_phase==1| ∧ X|sequence_phase==0|) → |d2==1|)
            """;
    final String door2ImpAuth = """
            door2ImpAuth =! [] (|d2==1| -> |authorized|)
            """;
    final String door2AfterDoor1D = """
            door2AfterDoor1D = let
                    d1 = |d1==1|,
                    d2 = |d2==1|,
                    a = |authorized|
                in!
                G((a∧(a U d1))→(¬d2 U (d1∧F(a∧d2))))
                //[]((auth ∧ (auth U d1)) → (auth U (d1 ∧ (auth U d2))))
            
//                      []([]q -> p)
//                [] ((|authorized == 0 && authorized' == 1| ∧ |d1==1|) → (|authorized| W |d2==1|))
//                    [] (q → (p W r))
//[] ( (|authorized| ∧ |d1==1|) → (|authorized| W |d2==1|) )
            """;

    final String door1AfterDoor2D = """
            door1AfterDoo2D = let
                    p = (|d2==1| → ◇ |d1==1|),
                    q = |authorized|,
                    r = ¬ |authorized|,
                in!
                    [] (q → (p W r))
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
                    Map.entry(eventualSecurity, Result.ok(true)),
                    Map.entry(authLeadsToDoor1Open, Result.ok(false)),
                    Map.entry(door1OpenLeadsToDoor1Close, Result.ok(false)),
                    Map.entry(door2OpenImpliesDoor1WasOpenBefore, Result.err(new IllegalArgumentException())),
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
                    Map.entry(eventualSecurity, Result.ok(true)),
                    Map.entry(authLeadsToDoor1Open, Result.ok(false)),
                    Map.entry(door1OpenLeadsToDoor1Close, Result.ok(false)),
                    Map.entry(door2OpenImpliesDoor1WasOpenBefore, Result.err(new IllegalArgumentException())),
                    Map.entry(accessSequence, Result.ok(false)),
                    Map.entry(authWithoutDoor1OpenLeadsToNotAuthorizedOrDoor1Open, Result.ok(false))
            ),
            "mantrap2.soup", Map.ofEntries(
                    Map.entry(exclusion, Result.ok(true)),
                    Map.entry(noDeadlock, Result.ok(true)),
                    Map.entry(security, Result.ok(true)),
                    Map.entry(securityDoor1, Result.ok(true)),
                    Map.entry(door1IsOpenOnlyIfAutorized, Result.ok(true)),
                    Map.entry(door1EventuallyOpensIfAuthorized, Result.ok(true)),
                    Map.entry(door2EventuallyOpensIfAuthorized, Result.ok(true)),
                    Map.entry(alwaysPossibleToProgressSomehow, Result.ok(true)),
//                    Map.entry(authLeadsToDoor1Open, Result.ok(false)),
//                    Map.entry(authLeadsToDoor2Open, Result.ok(false)),
                    Map.entry(eventualSecurity, Result.ok(true)),
                    Map.entry(door1OpenLeadsToDoor1Close, Result.ok(true)),
                    Map.entry(door2OpenImpliesDoor1WasOpenBefore, Result.err(new IllegalArgumentException())),
                    Map.entry(accessSequence, Result.ok(true)),
                    Map.entry(enoughAuthorizationGetsYouThrough, Result.err(new AutomatonSemantics.GuardEvaluationException("", null))),
                    Map.entry(authWithoutDoor1OpenLeadsToNotAuthorizedOrDoor1Open, Result.ok(true)),
                    Map.entry(authWithoutDoor2OpenLeadsToNotAuthorizedOrDoor2Open, Result.ok(true))
            ),
            "mantrap3.soup", Map.ofEntries(
                    Map.entry(exclusion, Result.ok(true)),
                    Map.entry(noDeadlock, Result.ok(true)),
                    Map.entry(security, Result.ok(true)),
                    Map.entry(door1IsOpenOnlyIfAutorized, Result.ok(true)),
                    Map.entry(door1EventuallyOpensIfAuthorized, Result.ok(true)),
                    Map.entry(door2EventuallyOpensIfAuthorized, Result.ok(true)),
                    Map.entry(alwaysPossibleToProgressSomehow, Result.ok(true)),
                    Map.entry(eventualSecurity, Result.ok(true)),
                    Map.entry(authLeadsToDoor1OpenM2, Result.ok(true)),
                    Map.entry(door1OpenLeadsToDoor1Close, Result.ok(true)),
                    Map.entry(door2OpenImpliesDoor1WasOpenBefore, Result.err(new IllegalArgumentException())),
                    Map.entry(doorsOpensInTheRightPhase, Result.ok(true)),
                    Map.entry(accessSequence, Result.ok(true)),
                    Map.entry(enoughAuthorizationGetsYouThrough, Result.ok(true)),
                    Map.entry(authWithoutDoor1OpenLeadsToNotAuthorizedOrDoor1Open, Result.ok(true)),
                    Map.entry(authWithoutDoor2OpenLeadsToNotAuthorizedOrDoor2Open, Result.ok(true)),
                    Map.entry(phase1LeadsToDoor1Open, Result.ok(false)),
                    Map.entry(phase0LeadsToDoor2Open, Result.ok(false))
//                    Map.entry(door1InfinitelyOften, Result.ok(false)),
//                    Map.entry(everyPhase0LeadsToD1Opening, Result.ok(false)),
//                    Map.entry(resetDoesNotShortcircuit, Result.ok(false)),
//                    Map.entry(door2ImpAuth, Result.ok(true)),
//                    Map.entry(door2AfterDoor1D, Result.ok(false))
//                    Map.entry(door1AfterDoor2D, Result.ok(true))

            )
    );
    String modelPath = "../soup-models/mantrap/";

    Soup readSoup(String modelName) throws IOException, ParseException {
        return Reader.readSoup(new BufferedReader(new FileReader(modelPath + modelName)));
    }

    @SuppressWarnings("unchecked")
    EmptinessCheckerAnswer<Product<?, ?>> mc(Soup model, String property) {
        return (EmptinessCheckerAnswer<Product<?, ?>>) SoupGPSLModelChecker.soupGPSLModelChecker(model, property).runAlone();
    }

    EmptinessCheckerAnswer<Product<?, ?>> mc(String modelName, String property) throws IOException, ParseException {
        var model = readSoup(modelName);
        return mc(model, property);
    }

    @Test
    void testAll() throws Exception {
        for (var entry : models2properties.entrySet()) {
            for (var property : entry.getValue().entrySet()) {
                var propName = "'" + property.getKey().split("=")[0].trim() + "'";
                EmptinessCheckerAnswer<Product<?, ?>> result = null;
                try {
                    result = mc(entry.getKey(), property.getKey());
                } catch (Exception e) {
                    if (property.getValue().isOk()) {
                        fail("Model " + entry.getKey() + " property " + propName + "\nthrows '" + e.getClass() + "' " + e.getLocalizedMessage());
                    }
                }

                switch (property.getValue()) {
                    case Result.Ok(var v) when v.equals(true) ->
                            assertTrue(result.holds, "Model " + entry.getKey() + " property " + propName);
                    case Result.Ok(var _) ->
                            assertFalse(result.holds, "Model " + entry.getKey() + " property " + propName);
                    case Result.Err(var e) -> assertThrows(e.getClass(), () -> mc(entry.getKey(), property.getKey()));
                }
            }
        }
    }

    /// mantrap 0
    @Test
    void mantrap0ExclusionPred() throws Exception {
        var result = mc("mantrap0.soup", exclusionPred);
        assertFalse(result.holds);
        var soupWitness = ((Environment) result.witness.start().l());
        assertEquals(soupWitness.lookup("d1"), soupWitness.lookup("d2"));
        var propWitness = ((State) result.witness.end().r());
        assertEquals("x", propWitness.name());
        assertEquals(5, result.trace.size());
    }

    @Test
    void mantrap0ExclusionLTL() throws Exception {
        var result = mc("mantrap0.soup", exclusion);
        assertFalse(result.holds);
        var soupWitness = ((Environment) result.witness.start().l());
        assertEquals(soupWitness.lookup("d1"), soupWitness.lookup("d2"));
        var propWitness = ((State) result.witness.end().r());
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
