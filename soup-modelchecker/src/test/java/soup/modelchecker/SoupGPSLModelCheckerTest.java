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

    /// ALICE BOB 0
    @Test
    void testAliceBob0Exclusion() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var pred = ("p=!|a==2 && b==2|");
        var result = mc(model, pred).runAlone();
        assertFalse(result.holds);
        assertEquals(6, result.trace.size());
    }

    /// ALICE BOB 0
    @Test
    void testAliceBob0ExclusionNFA() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var pred = ("""
                p = nfa
                states s, x;
                initial s;
                accept x;
                s [true] s;
                s [|a==2 && b==2|] x
                """);
        var result = mc(model, pred).runAlone();
        assertFalse(result.holds);
        assertEquals(6, result.trace.size());
    }

    /// There is no deadlock in v0
    @Test
    void testAliceBob0Deadlock() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var pred = "p=!|deadlock|";
        var result = mc(model, pred).runAlone();
        assertTrue(result.holds);
    }

    ///  At least one gets to the critical section
    @Test
    void testAliceBob0BuchiOneIn() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var pred = ("""
                p =
                states s, x;
                initial s;
                accept x;
                s [true] s;
                s [!|a==2| ∧ !|b==2|] x;
                x [!|a==2| ∧ !|b==2|] x
                """);
        var result = mc(model, pred).runAlone();
        assertTrue(result.holds);
    }

    ///  LTL: At least one gets to the critical section
    @Test
    void testAliceBob0BuchiOneInLTL() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var pred = ("""
                p = ![]<> (|a==2| or |b==2|)
                """);
        var result = mc(model, pred).runAlone();
        assertTrue(result.holds);
    }

    /// if one wants in it eventually gets in
    @Test
    void testAliceBob0BuchiFairness() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var pred = ("""
                p =
                states s, xA, xB;
                initial s;
                accept xA, xB;
                s [true] s; //ok
                s [|a==1| ∧ !|a==2|] xA; //aWantsIn
                xA [!|a==2|] xA; //aNotIn
                s [|b==1| ∧ !|b==2|] xB; //bWantsIn
                xB [!|b==2|] xB //bNotIn
                """);
        var result = mc(model, pred).runAlone();
        assertFalse(result.holds);
    }

    /// LTL if one wants in it eventually gets in
    @Test
    void testAliceBob0BuchiFairnessLTL() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var pred = ("""
                p =![](  (|a==1| -> <> |a==2|)
                       ∧ (|b==1| -> <> |b==2|))
                """);
        var result = mc(model, pred).runAlone();
        assertFalse(result.holds);
    }

    @Test
    void testAliceBob0BuchiIdling() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var pred = """
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

        var result = mc(model, pred).runAlone();
        assertFalse(result.holds);
    }

    @Test
    void testAliceBob0BuchiIdlingLTL() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var pred = """
                idling = let
                		aliceFlagUP=|a==1|,
                		aliceCS = |a==2|,
                		bobFlagUP=|b==1|,
                		bobCS = |b==2|
                	in
                		!([]   (!aliceFlagUP -> (!<> aliceCS))
                		    && (!bobFlagUP   -> (!<> bobCS  )) )
                """;

        var result = mc(model, pred).runAlone();
        assertFalse(result.holds);
    }

/*
    @Test
    void testAliceBob0BuchiIdling() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var prop = readSoup("dependent/buchi_idling.soup");
        var pred = Reader.readExpression("!status");
        var result = buchiMc(model, prop, pred).runAlone();
        assertTrue(result.holds);
    }
    /// ALICE BOB 1
    @Test
    void testAliceBob1Exclusion() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var pred = Reader.readExpression("a==2 && b==2");
        var result = predicateMC(model, pred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob1ExclusionSafety() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var prop = readSoup("dependent/exclusion.soup");
        var pred = Reader.readExpression("!status");
        var result = safetyMc(model, prop, pred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob1Deadlock() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var pred = Reader.readExpression("deadlock");
        var result = predicateMC(model, pred).runAlone();
        assertFalse(result.holds);
    }

    @Test
    void testAliceBob1DeadlockSafety() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var prop = readSoup("dependent/no-deadlock.soup");
        var pred = Reader.readExpression("!status");
        var result = safetyMc(model, prop, pred).runAlone();
        assertFalse(result.holds);
    }

    @Test
    void testAliceBob1DeadlockSafetyStepPred() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var prop = readSoup("dependent/no-deadlock1.soup");
        var pred = Reader.readExpression(/*right:*//*"p:fail");
        //TODO: this should be fixed at some point -
        //TODO: because pred should be a state predicate, but the prop is stateless.
        //TODO: so I should be able to observe steps of the synchronous product, not only the states
        assertThrows(NullPointerException.class, () -> safetyMc(model, prop, pred).runAlone());
//        var result = safetyMc(model, prop, pred).runAlone();
//        assertFalse(result.holds);
    }

    @Test
    void testAliceBob1BuchiOneIn() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var prop = readSoup("dependent/buchi_eventuallyOneInCS.soup");
        var pred = Reader.readExpression("!status");
        var result = buchiMc(model, prop, pred).runAlone();
        assertFalse(result.holds);
    }

    @Test
    void testAliceBob1BuchiFairness() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var prop = readSoup("dependent/buchi_fairness.soup");
        var pred = Reader.readExpression("!status");
        var result = buchiMc(model, prop, pred).runAlone();
        assertFalse(result.holds);
    }

    @Test
    void testAliceBob1BuchiIdling() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var prop = readSoup("dependent/buchi_idling.soup");
        var pred = Reader.readExpression("!status");
        var result = buchiMc(model, prop, pred).runAlone();
        assertTrue(result.holds);
    }

    /// ALICE BOB 2
    @Test
    void testAliceBob2Exclusion() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var pred = Reader.readExpression("a==2 && b==2");
        var result = predicateMC(model, pred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob2ExclusionSafety() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var prop = readSoup("dependent/exclusion.soup");
        var pred = Reader.readExpression("!status");
        var result = safetyMc(model, prop, pred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob2Deadlock() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var pred = Reader.readExpression("deadlock");
        var result = predicateMC(model, pred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob2DeadlockSafety() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var prop = readSoup("dependent/no-deadlock.soup");
        var pred = Reader.readExpression("!status");
        var result = safetyMc(model, prop, pred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob2BuchiOneIn() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var prop = readSoup("dependent/buchi_eventuallyOneInCS.soup");
        var pred = Reader.readExpression("!status");
        var result = buchiMc(model, prop, pred).runAlone();
        assertFalse(result.holds);
    }

    @Test
    void testAliceBob2BuchiFairness() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var prop = readSoup("dependent/buchi_fairness.soup");
        var pred = Reader.readExpression("!status");
        var result = buchiMc(model, prop, pred).runAlone();
        assertFalse(result.holds);
    }

    @Test
    void testAliceBob2BuchiIdling() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var prop = readSoup("dependent/buchi_idling.soup");
        var pred = Reader.readExpression("!status");
        var result = buchiMc(model, prop, pred).runAlone();
        assertTrue(result.holds);
    }

    /// ALICE BOB 3
    @Test
    void testAliceBob3Exclusion() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var pred = Reader.readExpression("a==2 && b==2");
        var result = predicateMC(model, pred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob3ExclusionSafety() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var prop = readSoup("dependent/exclusion.soup");
        var pred = Reader.readExpression("!status");
        var result = safetyMc(model, prop, pred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob3Deadlock() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var pred = Reader.readExpression("deadlock");
        var result = predicateMC(model, pred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob3DeadlockSafety() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var prop = readSoup("dependent/no-deadlock.soup");
        var pred = Reader.readExpression("!status");
        var result = safetyMc(model, prop, pred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob3BuchiOneIn() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var prop = readSoup("dependent/buchi_eventuallyOneInCS.soup");
        var pred = Reader.readExpression("!status");
        var result = buchiMc(model, prop, pred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob3BuchiFairness() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var prop = readSoup("dependent/buchi_fairness.soup");
        var pred = Reader.readExpression("!status");
        var result = buchiMc(model, prop, pred).runAlone();
        assertFalse(result.holds);
    }

    @Test
    void testAliceBob3BuchiIdling() throws Exception {
        var model = readSoup("alice-bob3.soup");
        var prop = readSoup("dependent/buchi_idling.soup");
        var pred = Reader.readExpression("!status");
        var result = buchiMc(model, prop, pred).runAlone();
        assertTrue(result.holds);
    }

    /// ALICE BOB 4
    @Test
    void testAliceBob4Exclusion() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var pred = Reader.readExpression("a==2 && b==2");
        var result = predicateMC(model, pred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob4ExclusionSafety() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var prop = readSoup("dependent/exclusion.soup");
        var pred = Reader.readExpression("!status");
        var result = safetyMc(model, prop, pred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob4Deadlock() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var pred = Reader.readExpression("deadlock");
        var result = predicateMC(model, pred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob4DeadlockSafety() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var prop = readSoup("dependent/no-deadlock.soup");
        var pred = Reader.readExpression("!status");
        var result = safetyMc(model, prop, pred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob4BuchiOneIn() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var prop = readSoup("dependent/buchi_eventuallyOneInCS.soup");
        var pred = Reader.readExpression("!status");
        var result = buchiMc(model, prop, pred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob4BuchiFairness() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var prop = readSoup("dependent/buchi_fairness.soup");
        var pred = Reader.readExpression("!status");
        var result = buchiMc(model, prop, pred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob4BuchiIdling() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var prop = readSoup("dependent/buchi_idling.soup");
        var pred = Reader.readExpression("!status");
        var result = buchiMc(model, prop, pred).runAlone();
        assertTrue(result.holds);
    }

    /// ALICE BOB 5
    @Test
    void testAliceBob5Exclusion() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var pred = Reader.readExpression("a==2 && b==2");
        var result = predicateMC(model, pred).runAlone();
        System.out.println(result);
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob5ExclusionSafety() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var prop = readSoup("dependent/exclusion.soup");
        var pred = Reader.readExpression("!status");
        var result = safetyMc(model, prop, pred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob5Deadlock() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var pred = Reader.readExpression("deadlock");
        var result = predicateMC(model, pred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob5DeadlockSafety() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var prop = readSoup("dependent/no-deadlock.soup");
        var pred = Reader.readExpression("!status");
        var result = safetyMc(model, prop, pred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob5BuchiOneIn() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var prop = readSoup("dependent/buchi_eventuallyOneInCS.soup");
        var pred = Reader.readExpression("!status");
        var result = buchiMc(model, prop, pred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob5BuchiFairness() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var prop = readSoup("dependent/buchi_fairness.soup");
        var pred = Reader.readExpression("!status");
        var result = buchiMc(model, prop, pred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob5BuchiIdling() throws Exception {
        var model = readSoup("alice-bob5.soup");
        var prop = readSoup("dependent/buchi_idling.soup");
        var pred = Reader.readExpression("!status");
        var result = buchiMc(model, prop, pred).runAlone();
        assertTrue(result.holds);
    }
    */
}
