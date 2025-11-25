package soup.modelchecker;

import gpsl.semantics.AutomatonSemantics;
import gpsl.syntax.model.State;
import obp3.modelchecking.EmptinessCheckerAnswer;
import obp3.runtime.IExecutable;
import obp3.runtime.sli.Step;
import obp3.sli.core.operators.product.Product;
import org.junit.jupiter.api.Test;
import soup.semantics.base.Environment;
import soup.syntax.Reader;
import soup.syntax.model.declarations.Soup;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

public class SoupGPSLModelCheckerTest {
    String modelPath = "../soup-models/alice-bob/";

    Soup readSoup(String modelName) throws IOException, ParseException {
        return Reader.readSoup(new BufferedReader(new FileReader(modelPath + modelName)));
    }

    @SuppressWarnings("unchecked")
    EmptinessCheckerAnswer<Product<Environment,State>> mc(Soup model, String property) {
        return SoupGPSLModelChecker.soupGPSLModelChecker(model, property).runAlone();
    }

    final String exclusionPred = "p=! !|a==2 && b==2|";
    final String exclusionNFA = """
                p = nfa
                states s, x;
                initial s;
                accept x;
                s [true] s;
                s [|a==2 && b==2|] x
                """;
    final String exclusionLTL = """
            p =! [] ! |a==2 && b==2|
            """;
    final String exclusionBuchi = """
            p = let p = |a==2| && |b==2| in
            buchi
                states s, x;
                initial s;
                accept x;
                s [!p] s;
                s [p] x;
                x [true] x
            """;

    final String noDeadlockPred = "p=! !|deadlock|";
    final String noDeadlockLTL = "p=! G !|deadlock|";


    /// recurrence: Alice or Bob will enter the critical section infinitely often
    final String atLeastOneInLTL = "p =! []<> (|a==2| or |b==2|)";
    final String atLeastOneInBuchi = """
                p =
                states s, x;
                initial s;
                accept x;
                s [true] s;
                s [!|a==2| ∧ !|b==2|] x;
                x [!|a==2| ∧ !|b==2|] x
                """;

    /// If a process wants to enter (flag up), it will eventually get to the CS.
    final String livenessLTL = """
                p =! [](  (|a==1| -> <> |a==2|)
                        ∧ (|b==1| -> <> |b==2|))
                """;

    final String livenessBuchi = """
                p =
                states s, xA, xB;
                initial s;
                accept xA, xB;
                s [true] s; //ok
                s [|a==1| ∧ !|a==2|] xA; //aWantsIn
                xA [!|a==2|] xA; //aNotIn
                s [|b==1| ∧ !|b==2|] xB; //bWantsIn
                xB [!|b==2|] xB //bNotIn
                """;

    /// If the other process is idle (does not wish to enter its critical section),
    /// a waiting process should eventually succeed in entering the critical section.
    /// When A is waiting (a == 1) and B is not interested (dB == false),
    /// A must eventually enter its critical section (a == 2).
    final String idlingLTL = """
            idling = let
                aIsWaiting = |a==1|,
                bNotInterested = |b==0|,
                aInCriticalSection = |a==2|,
                bIsWaiting = |b==1|,
                aNotInterested = |a==0|,
                bInCriticalSection = |b==2|
                in ! G (
                            (( aIsWaiting ∧ bNotInterested)→ F aInCriticalSection)
                           ∧(( bIsWaiting ∧ aNotInterested)→ F bInCriticalSection))
            """;

    final String idlingBuchi = """
            idling = let
                aIsWaiting = |a==1|,
                bNotInterested = |b==0|,
                aInCriticalSection = |a==2|,
                bIsWaiting = |b==1|,
                aNotInterested = |a==0|,
                bInCriticalSection = |b==2|
            in buchi
                states s0, s1, s2;
                initial s0;
                accept s1, s2;
                s0 [true] s0;
                s0 [!aInCriticalSection & aIsWaiting & bNotInterested] s1;
                s0 [!bInCriticalSection & bIsWaiting & aNotInterested] s2;
                s1 [!aInCriticalSection] s1;
                s2 [!bInCriticalSection] s2
            """;

    final String idlingWithFlagsLTL = """
            idling = let
                aIsWaiting = |a==1|,
                bNotInterested = |dB==false|,
                aInCriticalSection = |a==2|,
                bIsWaiting = |b==1|,
                aNotInterested = |dA==false|,
                bInCriticalSection = |b==2|
                in ! G (
                            (( aIsWaiting ∧ bNotInterested)→ F aInCriticalSection)
                           ∧(( bIsWaiting ∧ aNotInterested)→ F bInCriticalSection))
            """;

    /// Flag discipline
    /// No process can enter its critical section unless it has previously raised its flag.
    /// LIMITATION: if we want to express this without flags, we need one of the following:
    ///     - the past LTL operator -- currently, GPSL does not support past LTL operators
    ///     - instrument the model (to remember the intention of a process to enter the CS) -- the flags do just this
    ///     - a buchi automata that captures the intention to enter either:
    ///             - using a witness variable that stores the intention -- currently, GPSL does not allow variable definition (dependent Soup does)
    ///             - encoding the intention in the state-space of the buchi automata -- we could do that with GPSL, but it's messy.
    final String flagDisciplineLTL = """
                flagDiscipline = let
                		aliceFlagUP=|a==1|,
                		aliceCS = |a==2|,
                		bobFlagUP=|b==1|,
                		bobCS = |b==2|
                	in
                		!([]   (aliceCS -> P aliceFlagUP) //P is the past operator, currently unsupported
                		    && (bobCS   -> P bobFlagUP  ) )
                """;

    final String flagDisciplineNFA = """
            flagDisciplineNFA = let
                    aIdle   = |a == 0|,
                    aNoFlag = |a != 1|,
                    aCS     = |a == 2|,
                    bIdle   = |a == 0|,
                    bNoFlag = |a != 1|,
                    bCS     = |a == 2|
                in nfa
                    states s0, sa1, sb1, saX, sbX;
                    initial s0;
                    accept saX, sbX;
                    s0 [true] s0;
                    s0 [aIdle] sa1;
                    sa1 [aNoFlag] sa1;
                    sa1 [aCS] saX;
                    s0 [bIdle] sb1;
                    sb1 [bNoFlag] sb1;
                    sb1 [bCS] sbX
            """;

    /// Flag discipline with flags
    /// No process can enter its critical section unless it has previously raised its flag.
    /// Whenever a process is in its critical section, its flag must have been raised (it must have wanted to enter)
    final String flagDisciplineWithFlagsLTL = """
                flagDiscipline = let
                		aliceFlagUP=|dA|,
                		aliceCS = |a==2|,
                		bobFlagUP=|dB|,
                		bobCS = |b==2|
                	in
                		!([]   (aliceCS -> aliceFlagUP)
                		    && (bobCS   -> bobFlagUP  ) )
                """;

    final String flagDisciplineWithFlagsBuchi = """
                idling = let
                		aliceFlagUP=|dA|,
                		aliceCS = |a==2|,
                		bobFlagUP=|dB|,
                		bobCS = |b==2|
                	in
                		states s0, s1, s2;
                		initial s2;
                		accept s0;
                		s2 [(bobCS & !bobFlagUP) || (aliceCS & !aliceFlagUP)] s0;
                		s2 [(!aliceCS & !bobCS) || (aliceFlagUP & !bobCS) || (!aliceCS & bobFlagUP) || (aliceFlagUP & bobFlagUP)] s1;
                		s1 [!aliceCS || aliceFlagUP] s1;
                		s1 [aliceCS & !aliceFlagUP] s0;
                		s0 [true] s0
                """;

    /// ALICE BOB 0
    @Test
    void testAliceBob0ExclusionPred() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var result = mc(model, exclusionPred);
        assertFalse(result.holds);
        var soupWitness = ((Environment)result.witness.start().l());
        assertEquals( soupWitness.lookup("a"), soupWitness.lookup("b"));
        var propWitness = ((State)result.witness.end().r());
        assertEquals("x", propWitness.name());
        assertEquals(6, result.trace.size());
    }

    @Test
    void testAliceBob0ExclusionNFA() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var result = mc(model, exclusionNFA);
        assertFalse(result.holds);
        assertEquals(6, result.trace.size());
    }

    @Test
    void testAliceBob0ExclusionLTL() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var result = mc(model, exclusionLTL);
        assertFalse(result.holds);
        assertEquals(9, result.trace.size());
    }

    @Test
    void testAliceBob0ExclusionBuchi() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var result = mc(model, exclusionBuchi);
        assertFalse(result.holds);
        assertEquals(9, result.trace.size());
    }

    /// There is no deadlock in v0
    @Test
    void testAliceBob0DeadlockPred() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var result = mc(model, noDeadlockPred);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob0DeadlockLTL() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var result = mc(model, noDeadlockLTL);
        assertTrue(result.holds);
    }

    ///  At least one gets to the critical section
    @Test
    void testAliceBob0OneInBuchi() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var result = mc(model, atLeastOneInBuchi);
        assertTrue(result.holds);
    }

    ///  LTL: At least one gets to the critical section
    @Test
    void testAliceBob0OneInLTL() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var result = mc(model, atLeastOneInLTL);
        assertTrue(result.holds);
    }

    /// if one wants in it eventually gets in
    @Test
    void testAliceBob0LivenessBuchi() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var result = mc(model, livenessBuchi);
        assertFalse(result.holds);
    }

    /// LTL if one wants in it eventually gets in
    @Test
    void testAliceBob0LivenessLTL() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var result = mc(model, livenessLTL);
        assertFalse(result.holds);
    }

    @Test
    void testAliceBob0IdlingBuchi() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var result = mc(model, idlingBuchi);
        assertFalse(result.holds);
    }

    @Test
    void testAliceBob0IdlingLTL() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var result = mc(model, idlingLTL);
        assertFalse(result.holds);
    }

    /// ALICE BOB 1
    @Test
    void testAliceBob1ExclusionPred() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var result = mc(model, exclusionPred);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob1ExclusionNFA() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var result = mc(model, exclusionNFA);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob1ExclusionLTL() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var result = mc(model, exclusionLTL);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob1ExclusionBuchi() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var result = mc(model, exclusionBuchi);
        assertTrue(result.holds);
    }

    /// There is a deadlock in v1
    @Test
    void testAliceBob1DeadlockPred() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var result = mc(model, noDeadlockPred);
        assertFalse(result.holds);
        assertEquals(4, result.trace.size());
    }

    @Test
    void testAliceBob1DeadlockLTL() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var result = mc(model, noDeadlockLTL);
        assertFalse(result.holds);
        assertEquals(5, result.trace.size());
    }

    ///  At least one gets to the critical section
    @Test
    void testAliceBob1OneInBuchi() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var result = mc(model, atLeastOneInBuchi);
        assertFalse(result.holds);
    }

    ///  LTL: At least one gets to the critical section
    @Test
    void testAliceBob1OneInLTL() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var result = mc(model, atLeastOneInLTL);
        assertFalse(result.holds);
    }

    /// if one wants in it eventually gets in
    @Test
    void testAliceBob1LivenessBuchi() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var result = mc(model, livenessBuchi);
        assertFalse(result.holds);
    }

    /// LTL if one wants in it eventually gets in
    @Test
    void testAliceBob1LivenessLTL() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var result = mc(model, livenessLTL);
        assertFalse(result.holds);
    }

    @Test
    void testAliceBob1IdlingBuchi() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var result = mc(model, idlingBuchi);
        assertFalse(result.holds);
    }

    @Test
    void testAliceBob1IdlingLTL() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var result = mc(model, idlingLTL);
        assertFalse(result.holds);
    }

    /// ALICE BOB 2
    @Test
    void testAliceBob2ExclusionPred() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var result = mc(model, exclusionPred);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob2ExclusionNFA() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var result = mc(model, exclusionNFA);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob2ExclusionLTL() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var result = mc(model, exclusionLTL);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob2ExclusionBuchi() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var result = mc(model, exclusionBuchi);
        assertTrue(result.holds);
    }

    /// There is no deadlock in v2
    @Test
    void testAliceBob2DeadlockPred() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var result = mc(model, noDeadlockPred);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob2DeadlockLTL() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var result = mc(model, noDeadlockLTL);
        assertTrue(result.holds);
    }

    ///  At least one gets to the critical section
    @Test
    void testAliceBob2OneInBuchi() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var result = mc(model, atLeastOneInBuchi);
        assertFalse(result.holds);
        assertEquals(6, result.trace.size());
    }

    ///  LTL: At least one gets to the critical section
    @Test
    void testAliceBob2OneInLTL() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var result = mc(model, atLeastOneInLTL);
        assertFalse(result.holds);
        assertEquals(6, result.trace.size());
    }

    /// if one wants in it eventually gets in
    @Test
    void testAliceBob2LivenessBuchi() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var result = mc(model, livenessBuchi);
        assertFalse(result.holds);
        assertEquals(6, result.trace.size());
    }

    /// LTL if one wants in it eventually gets in
    @Test
    void testAliceBob2LivenessLTL() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var result = mc(model, livenessLTL);
        assertFalse(result.holds);
        assertEquals(6, result.trace.size());
    }

    @Test
    void testAliceBob2IdlingBuchi() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var result = mc(model, idlingBuchi);
        assertFalse(result.holds);
    }

    @Test
    void testAliceBob2IdlingLTL() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var result = mc(model, idlingLTL);
        assertFalse(result.holds);
    }

    /// ALICE BOB 3
    @Test
    void testAliceBob3ExclusionPred() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var result = mc(model, exclusionPred);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob3ExclusionNFA() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var result = mc(model, exclusionNFA);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob3ExclusionLTL() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var result = mc(model, exclusionLTL);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob3ExclusionBuchi() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var result = mc(model, exclusionBuchi);
        assertTrue(result.holds);
    }

    /// There is no deadlock in v3
    @Test
    void testAliceBob3DeadlockPred() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var result = mc(model, noDeadlockPred);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob3DeadlockLTL() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var result = mc(model, noDeadlockLTL);
        assertTrue(result.holds);
    }

    ///  At least one gets to the critical section ok in v3
    @Test
    void testAliceBob3OneInBuchi() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var result = mc(model, atLeastOneInBuchi);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob3OneInLTL() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var result = mc(model, atLeastOneInLTL);
        assertTrue(result.holds);
    }

    /// if one wants in it eventually gets in fails in v3
    @Test
    void testAliceBob3LivenessBuchi() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var result = mc(model, livenessBuchi);
        assertFalse(result.holds);
        assertEquals(7, result.trace.size());
    }

    @Test
    void testAliceBob3LivenessLTL() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var result = mc(model, livenessLTL);
        assertFalse(result.holds);
        assertEquals(7, result.trace.size());
    }

    @Test
    void testAliceBob3IdlingBuchi() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var result = mc(model, idlingBuchi);
        assertFalse(result.holds);
    }

    @Test
    void testAliceBob3IdlingLTL() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var result = mc(model, idlingLTL);
        assertFalse(result.holds);
    }

    /// ALICE BOB 4
    @Test
    void testAliceBob4ExclusionPred() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, exclusionPred);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob4ExclusionNFA() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, exclusionNFA);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob4ExclusionLTL() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, exclusionLTL);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob4ExclusionBuchi() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, exclusionBuchi);
        assertTrue(result.holds);
    }

    /// There is no deadlock in v3
    @Test
    void testAliceBob4DeadlockPred() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, noDeadlockPred);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob4DeadlockLTL() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, noDeadlockLTL);
        assertTrue(result.holds);
    }

    ///  At least one gets to the critical section ok in v3
    @Test
    void testAliceBob4OneInBuchi() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, atLeastOneInBuchi);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob4OneInLTL() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, atLeastOneInLTL);
        assertTrue(result.holds);
    }

    /// if one wants in it eventually gets in fails in v3
    @Test
    void testAliceBob4LivenessBuchi() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, livenessBuchi);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob4LivenessLTL() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, livenessLTL);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob4IdlingBuchi() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, idlingBuchi);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob4IdlingLTL() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, idlingLTL);
        assertTrue(result.holds);
    }


    @Test
    void testAliceBob4IdlingWithFlagsLTL() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, idlingWithFlagsLTL);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob4FlagDisciplineLTL() throws Exception {
        var model = readSoup("alice-bob4.soup");
        //Past operator is not supported in GPSL
        var e = assertThrows(IllegalArgumentException.class, () -> {
            mc(model, flagDisciplineLTL);
        });
        assertEquals("""
                Failed to parse property: error at 7:24-7:35: unexpected 'aliceFlagUP' [syntax-error]
                  		!([]   (aliceCS -> P aliceFlagUP) //P is the past operator, currently unsupported
                                         ^^^^^^^^^^^
                error at 8:24-8:33: unexpected 'bobFlagUP' [syntax-error]
                  		    && (bobCS   -> P bobFlagUP  ) )
                                         ^^^^^^^^^
                """, e.getMessage());
    }

    @Test
    void testAliceBob4FlagDisciplineNFA() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, flagDisciplineNFA);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob4FlagDisciplineWithFlagsLTL() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, flagDisciplineWithFlagsLTL);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob4FlagDisciplineWithFlagsBuchi() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, flagDisciplineWithFlagsBuchi);
        assertTrue(result.holds);
    }

    /// ALICE BOB 5
    @Test
    void testAliceBob5ExclusionPred() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var result = mc(model, exclusionPred);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob5ExclusionNFA() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var result = mc(model, exclusionNFA);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob5ExclusionLTL() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var result = mc(model, exclusionLTL);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob5ExclusionBuchi() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var result = mc(model, exclusionBuchi);
        assertTrue(result.holds);
    }

    /// There is no deadlock in v3
    @Test
    void testAliceBob5DeadlockPred() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var result = mc(model, noDeadlockPred);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob5DeadlockLTL() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var result = mc(model, noDeadlockLTL);
        assertTrue(result.holds);
    }

    ///  At least one gets to the critical section ok in v3
    @Test
    void testAliceBob5BuchiOneIn() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var result = mc(model, atLeastOneInBuchi);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob5BuchiOneInLTL() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var result = mc(model, atLeastOneInLTL);
        assertTrue(result.holds);
    }

    /// if one wants in it eventually gets in fails in v3
    @Test
    void testAliceBob5BuchiLivenessBuchi() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var result = mc(model, livenessBuchi);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob5BuchiLivenessLTL() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var result = mc(model, livenessLTL);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob5BuchiIdlingBuchi() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var result = mc(model, idlingBuchi);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob5IdlingLTL() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var result = mc(model, idlingLTL);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob5FlagDisciplineLTL() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var e = assertThrows(IllegalArgumentException.class, () -> mc(model, flagDisciplineLTL));
        assertEquals("""
                Failed to parse property: error at 7:24-7:35: unexpected 'aliceFlagUP' [syntax-error]
                  		!([]   (aliceCS -> P aliceFlagUP) //P is the past operator, currently unsupported
                                         ^^^^^^^^^^^
                error at 8:24-8:33: unexpected 'bobFlagUP' [syntax-error]
                  		    && (bobCS   -> P bobFlagUP  ) )
                                         ^^^^^^^^^
                """, e.getMessage());
    }

    @Test
    void testAliceBob5FlagDisciplineWithFlagsLTL() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var e = assertThrows(AutomatonSemantics.GuardEvaluationException.class, () -> mc(model, flagDisciplineWithFlagsLTL));
        assertEquals("Failed to evaluate guard: Atom 'dB' evaluation failed.", e.getMessage());
    }

    /// No livelock 1: Stronger than no deadlock - ensures continuous progress conditionally
    /// globally either no intention to go in the critical section or one of them gets to the critical section eventually
    final String noLiveLock = """
        noLiveLock =! []( !(|a==1| ∨ |b==1|) ∨
        <>(|a==2| ∨ |b==2|) )
        """;

    @Test
    void testAliceBob4NoLivelock() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, noLiveLock);
        assertTrue(result.holds);
    }

    final String initialSafety = """
            initialSafety =! ((|a==0| ∧ |b==0| ∧ !|dA| ∧ !|dB|) ->
                            []!(|a==2| ∧ |b==2|))
            """;
    @Test
    void testAliceBob4InitialSafety() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, initialSafety);
        assertTrue(result.holds);
    }

    final String starvationFreedom = """
            starvationFreedom =! []<>(|a==1| -> <>|a==2|) ∧ []<>(|b==1| -> <>|b==2|)
            """;
    @Test
    void testAliceBob4StarvationFreedom() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, starvationFreedom);
        assertTrue(result.holds);
    }

    final String flagConsistency = """
            flagConsistency =¬ []( (|a==1| ∨ |a==2|) ↔ |dA|) ∧ ( (|b==1| ∨ |b==2|) ↔ |dB|) )
            """;
    @Test
    void testAliceBob4FlagConsistency() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, flagConsistency);
        assertTrue(result.holds);
    }

    final String criticalExit = """
            criticalExit =! [](|a==2| -> <>|a==0|) ∧ [](|b==2| -> <>|b==0|)
            """;
    @Test
    void testAliceBob4CriticalExit() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, criticalExit);
        assertTrue(result.holds);
    }

    final String noSpuriousWait = """
            noSpuriousWait =! [](|a==1| -> F(|a==2| ∨ !|dA|)) ∧ [](|b==1| -> F(|b==2| ∨ !|dB|))
            """;
    @Test
    void testAliceBob4NoSpuriousWait() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, noSpuriousWait);
        assertTrue(result.holds);
    }

    final String entryOrder = """
            entryOrder =! (
                    []( (|dA| ∧ !|dB|) -> (!|b==2| W |a==2|) )
                 ∧  []( (|dB| ∧ !|dA|) -> (!|a==2| W |b==2|) ) )
            """;
    @Test
    void testAliceBob4EntryOrder() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, entryOrder);
        assertTrue(result.holds);
    }

    final String noReentry = """
        noReentry = let
                aliceCS      = |a==2|,
                bobCS        = |b==2|,
                aliceWaiting = |a==1|,
                bobWaiting   = |b==1|
            in! (
                  []((aliceCS ∧ bobWaiting ∧ X|a==0|)→ X(!aliceCS U bobCS))
                ∧ []((bobCS ∧ aliceWaiting ∧ X|b==0|)→ X(!bobCS U aliceCS)))
        """;
    @Test
    void testAliceBob4NoReentry() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, noReentry);
        assertTrue(result.holds);
    }

    final String nonInterference = """
        nonInterference =! (
                [](|a==2| ∧ |b==0| -> <>(|b==1| ∨ |b==0|))
            ∧   [](|b==2| ∧ |a==0| -> <>(|a==1| ∨ |a==0|)) )
        """;
    @Test
    void testAliceBob4NonInterference() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, nonInterference);
        assertTrue(result.holds);
    }

    /// Processes eventually express interest if they can
    /// this requires a fairness assumption, otherwise either process could permanently decide not to raise its flag
    final String eventualInterest = """
        eventualInterest =! ([]<>(|a==0| -> <>|a==1|) ∧ []<>(|b==0| -> <>|b==1|))
        """;
    @Test
    void testAliceBob4EventualInterest() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, eventualInterest);
        assertFalse(result.holds);
    }

    final String noMutualWaiting = """
        noMutualWaiting = <>[] (|a==1| ∧ |b==1|)
        """;
    @Test
    void testAliceBob4NoMutualWaiting() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, noMutualWaiting);
        assertTrue(result.holds);
    }
}
