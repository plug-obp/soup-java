package soup.modelchecker;

import obp3.modelchecking.EmptinessCheckerAnswer;
import obp3.runtime.IExecutable;
import org.junit.jupiter.api.Test;
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

    IExecutable<EmptinessCheckerAnswer<?>> mc(Soup model, String property) {
        return SoupGPSLModelChecker.soupGPSLModelChecker(model, property);
    }

    final String exclusionPred = "p=!|a==2 && b==2|";
    final String exclusionNFA = """
                p = nfa
                states s, x;
                initial s;
                accept x;
                s [true] s;
                s [|a==2 && b==2|] x
                """;
    final String exclusionLTL = """
            p = ! [] ! |a==2 && b==2|
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

    final String noDeadlockPred = "p=!|deadlock|";
    final String noDeadlockLTL = "p=! G !|deadlock|";

    final String atLeastOneInLTL = "p = ![]<> (|a==2| or |b==2|)";
    final String atLeastOneInBuchi = """
                p =
                states s, x;
                initial s;
                accept x;
                s [true] s;
                s [!|a==2| ∧ !|b==2|] x;
                x [!|a==2| ∧ !|b==2|] x
                """;

    final String fairnessLTL = """
                p =![](  (|a==1| -> <> |a==2|)
                       ∧ (|b==1| -> <> |b==2|))
                """;

    final String fairnessBuchi = """
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

    final String idlingLTL = """
                idling = let
                		aliceFlagUP=|a==1|,
                		aliceCS = |a==2|,
                		bobFlagUP=|b==1|,
                		bobCS = |b==2|
                	in
                		!([]   (!aliceFlagUP -> (!<> aliceCS))
                		    && (!bobFlagUP   -> (!<> bobCS  )) )
                """;

    final String idlingBuchi = """
                idling = let
                		aU=|a==1|,
                		aC = |a==2|,
                		bU=|b==1|,
                		bC = |b==2|
                	in
                		states s0, s1, s2, s3, s4;
                		initial s0;
                		accept s1;
                		s0 [(aC & !aU) ∨ (bC & !bU)] s1;
                		s0 [(!aC & !aU & bU) ∨ (!aC & !aU & !bC)] s2;
                		s0 [(aU & !bC & !bU) ∨ (!aC & !bC & !bU)] s3;
                		s0 [(aU & bU) ∨ (aU & !bC)] s4;
                		s1 [true] s1;
                		s2 [!aC] s2;
                		s2 [aC] s1;
                		s3 [!bC] s3;
                		s3 [bC] s1;
                		s4 [aU] s4;
                		s4 [!aC & !aU] s2;
                		s4 [aC & !aU] s1
                """;

    /// ALICE BOB 0
    @Test
    void testAliceBob0ExclusionPred() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var result = mc(model, exclusionPred).runAlone();
        assertFalse(result.holds);
        assertEquals(6, result.trace.size());
    }

    @Test
    void testAliceBob0ExclusionNFA() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var result = mc(model, exclusionNFA).runAlone();
        assertFalse(result.holds);
        assertEquals(6, result.trace.size());
    }

    @Test
    void testAliceBob0ExclusionLTL() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var result = mc(model, exclusionLTL).runAlone();
        assertFalse(result.holds);
        assertEquals(9, result.trace.size());
    }

    @Test
    void testAliceBob0ExclusionBuchi() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var result = mc(model, exclusionBuchi).runAlone();
        assertFalse(result.holds);
        assertEquals(9, result.trace.size());
    }

    /// There is no deadlock in v0
    @Test
    void testAliceBob0DeadlockPred() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var result = mc(model, noDeadlockPred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob0DeadlockLTL() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var result = mc(model, noDeadlockLTL).runAlone();
        assertTrue(result.holds);
    }

    ///  At least one gets to the critical section
    @Test
    void testAliceBob0BuchiOneIn() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var result = mc(model, atLeastOneInBuchi).runAlone();
        assertTrue(result.holds);
    }

    ///  LTL: At least one gets to the critical section
    @Test
    void testAliceBob0BuchiOneInLTL() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var result = mc(model, atLeastOneInLTL).runAlone();
        assertTrue(result.holds);
    }

    /// if one wants in it eventually gets in
    @Test
    void testAliceBob0BuchiFairnessBuchi() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var result = mc(model, fairnessBuchi).runAlone();
        assertFalse(result.holds);
    }

    /// LTL if one wants in it eventually gets in
    @Test
    void testAliceBob0BuchiFairnessLTL() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var result = mc(model, fairnessLTL).runAlone();
        assertFalse(result.holds);
    }

    /// if one does not want to go it wont go
    @Test
    void testAliceBob0BuchiIdlingBuchi() throws Exception {
        var model = readSoup("alice-bob0.soup");

        var result = mc(model, idlingBuchi).runAlone();
        assertFalse(result.holds);
    }

    @Test
    void testAliceBob0BuchiIdlingLTL() throws Exception {
        var model = readSoup("alice-bob0.soup");

        var result = mc(model, idlingLTL).runAlone();
        assertFalse(result.holds);
    }

    /// ALICE BOB 1
    @Test
    void testAliceBob1ExclusionPred() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var result = mc(model, exclusionPred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob1ExclusionNFA() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var result = mc(model, exclusionNFA).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob1ExclusionLTL() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var result = mc(model, exclusionLTL).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob1ExclusionBuchi() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var result = mc(model, exclusionBuchi).runAlone();
        assertTrue(result.holds);
    }

    /// There is a deadlock in v1
    @Test
    void testAliceBob1DeadlockPred() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var result = mc(model, noDeadlockPred).runAlone();
        assertFalse(result.holds);
        assertEquals(4, result.trace.size());
    }

    @Test
    void testAliceBob1DeadlockLTL() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var result = mc(model, noDeadlockLTL).runAlone();
        assertFalse(result.holds);
        assertEquals(5, result.trace.size());
    }

    ///  At least one gets to the critical section
    @Test
    void testAliceBob1BuchiOneIn() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var result = mc(model, atLeastOneInBuchi).runAlone();
        assertFalse(result.holds);
    }

    ///  LTL: At least one gets to the critical section
    @Test
    void testAliceBob1BuchiOneInLTL() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var result = mc(model, atLeastOneInLTL).runAlone();
        assertFalse(result.holds);
    }

    /// if one wants in it eventually gets in
    @Test
    void testAliceBob1BuchiFairnessBuchi() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var result = mc(model, fairnessBuchi).runAlone();
        assertFalse(result.holds);
    }

    /// LTL if one wants in it eventually gets in
    @Test
    void testAliceBob1BuchiFairnessLTL() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var result = mc(model, fairnessLTL).runAlone();
        assertFalse(result.holds);
    }

    /// if one does not want to go it wont go
    @Test
    void testAliceBob1BuchiIdlingBuchi() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var result = mc(model, idlingBuchi).runAlone();
        assertFalse(result.holds);
    }

    @Test
    void testAliceBob1BuchiIdlingLTL() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var result = mc(model, idlingLTL).runAlone();
        assertFalse(result.holds);
    }

    /// ALICE BOB 2
    @Test
    void testAliceBob2ExclusionPred() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var result = mc(model, exclusionPred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob2ExclusionNFA() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var result = mc(model, exclusionNFA).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob2ExclusionLTL() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var result = mc(model, exclusionLTL).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob2ExclusionBuchi() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var result = mc(model, exclusionBuchi).runAlone();
        assertTrue(result.holds);
    }

    /// There is no deadlock in v2
    @Test
    void testAliceBob2DeadlockPred() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var result = mc(model, noDeadlockPred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob2DeadlockLTL() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var result = mc(model, noDeadlockLTL).runAlone();
        assertTrue(result.holds);
    }

    ///  At least one gets to the critical section
    @Test
    void testAliceBob2BuchiOneIn() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var result = mc(model, atLeastOneInBuchi).runAlone();
        assertFalse(result.holds);
        assertEquals(6, result.trace.size());
    }

    ///  LTL: At least one gets to the critical section
    @Test
    void testAliceBob2BuchiOneInLTL() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var result = mc(model, atLeastOneInLTL).runAlone();
        assertFalse(result.holds);
        assertEquals(6, result.trace.size());
    }

    /// if one wants in it eventually gets in
    @Test
    void testAliceBob2BuchiFairnessBuchi() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var result = mc(model, fairnessBuchi).runAlone();
        assertFalse(result.holds);
        assertEquals(6, result.trace.size());
    }

    /// LTL if one wants in it eventually gets in
    @Test
    void testAliceBob2BuchiFairnessLTL() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var result = mc(model, fairnessLTL).runAlone();
        assertFalse(result.holds);
        assertEquals(6, result.trace.size());
    }

    /// if one does not want to go it wont go
    @Test
    void testAliceBob2BuchiIdlingBuchi() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var result = mc(model, idlingBuchi).runAlone();
        assertFalse(result.holds);
    }

    @Test
    void testAliceBob2BuchiIdlingLTL() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var result = mc(model, idlingLTL).runAlone();
        assertFalse(result.holds);
    }

    /// ALICE BOB 3
    @Test
    void testAliceBob3ExclusionPred() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var result = mc(model, exclusionPred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob3ExclusionNFA() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var result = mc(model, exclusionNFA).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob3ExclusionLTL() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var result = mc(model, exclusionLTL).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob3ExclusionBuchi() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var result = mc(model, exclusionBuchi).runAlone();
        assertTrue(result.holds);
    }

    /// There is no deadlock in v3
    @Test
    void testAliceBob3DeadlockPred() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var result = mc(model, noDeadlockPred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob3DeadlockLTL() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var result = mc(model, noDeadlockLTL).runAlone();
        assertTrue(result.holds);
    }

    ///  At least one gets to the critical section ok in v3
    @Test
    void testAliceBob3BuchiOneIn() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var result = mc(model, atLeastOneInBuchi).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob3BuchiOneInLTL() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var result = mc(model, atLeastOneInLTL).runAlone();
        assertTrue(result.holds);
    }

    /// if one wants in it eventually gets in fails in v3
    @Test
    void testAliceBob3BuchiFairnessBuchi() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var result = mc(model, fairnessBuchi).runAlone();
        assertFalse(result.holds);
        assertEquals(7, result.trace.size());
    }

    @Test
    void testAliceBob3BuchiFairnessLTL() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var result = mc(model, fairnessLTL).runAlone();
        assertFalse(result.holds);
        assertEquals(7, result.trace.size());
    }

    /// if one does not want to go it wont go
    @Test
    void testAliceBob3BuchiIdlingBuchi() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var result = mc(model, idlingBuchi).runAlone();
        assertFalse(result.holds);
    }

    @Test
    void testAliceBob3BuchiIdlingLTL() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var result = mc(model, idlingLTL).runAlone();
        assertFalse(result.holds);
    }

    /// ALICE BOB 4
    @Test
    void testAliceBob4ExclusionPred() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, exclusionPred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob4ExclusionNFA() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, exclusionNFA).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob4ExclusionLTL() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, exclusionLTL).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob4ExclusionBuchi() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, exclusionBuchi).runAlone();
        assertTrue(result.holds);
    }

    /// There is no deadlock in v3
    @Test
    void testAliceBob4DeadlockPred() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, noDeadlockPred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob4DeadlockLTL() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, noDeadlockLTL).runAlone();
        assertTrue(result.holds);
    }

    ///  At least one gets to the critical section ok in v3
    @Test
    void testAliceBob4BuchiOneIn() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, atLeastOneInBuchi).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob4BuchiOneInLTL() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, atLeastOneInLTL).runAlone();
        assertTrue(result.holds);
    }

    /// if one wants in it eventually gets in fails in v3
    @Test
    void testAliceBob4BuchiFairnessBuchi() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, fairnessBuchi).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob4BuchiFairnessLTL() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, fairnessLTL).runAlone();
        assertTrue(result.holds);
    }

    /// if one does not want to go it wont go
    /// TODO: Why does this property fail here ?
    @Test
    void testAliceBob4BuchiIdlingBuchi() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, idlingBuchi).runAlone();
        assertFalse(result.holds);
    }

    @Test
    void testAliceBob4BuchiIdlingLTL() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, idlingLTL).runAlone();
        assertFalse(result.holds);
    }

    /// ALICE BOB 5
    @Test
    void testAliceBob5ExclusionPred() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var result = mc(model, exclusionPred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob5ExclusionNFA() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var result = mc(model, exclusionNFA).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob5ExclusionLTL() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var result = mc(model, exclusionLTL).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob5ExclusionBuchi() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var result = mc(model, exclusionBuchi).runAlone();
        assertTrue(result.holds);
    }

    /// There is no deadlock in v3
    @Test
    void testAliceBob5DeadlockPred() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var result = mc(model, noDeadlockPred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob5DeadlockLTL() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var result = mc(model, noDeadlockLTL).runAlone();
        assertTrue(result.holds);
    }

    ///  At least one gets to the critical section ok in v3
    @Test
    void testAliceBob5BuchiOneIn() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var result = mc(model, atLeastOneInBuchi).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob5BuchiOneInLTL() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var result = mc(model, atLeastOneInLTL).runAlone();
        assertTrue(result.holds);
    }

    /// if one wants in it eventually gets in fails in v3
    @Test
    void testAliceBob5BuchiFairnessBuchi() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var result = mc(model, fairnessBuchi).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob5BuchiFairnessLTL() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var result = mc(model, fairnessLTL).runAlone();
        assertTrue(result.holds);
    }

    /// if one does not want to go it wont go
    /// TODO: Why does this property fail here ?
    @Test
    void testAliceBob5BuchiIdlingBuchi() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var result = mc(model, idlingBuchi).runAlone();
        assertFalse(result.holds);
    }

    @Test
    void testAliceBob5BuchiIdlingLTL() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var result = mc(model, idlingLTL).runAlone();
        assertFalse(result.holds);
    }
}
