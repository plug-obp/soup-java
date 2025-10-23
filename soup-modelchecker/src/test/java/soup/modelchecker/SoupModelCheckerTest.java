package soup.modelchecker;

import obp3.runtime.IExecutable;
import obp3.modelchecking.EmptinessCheckerAnswer;
import obp3.traversal.dfs.DepthFirstTraversal;
import org.junit.jupiter.api.Test;
import soup.syntax.Reader;
import soup.syntax.model.declarations.Soup;
import soup.syntax.model.expressions.Expression;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

public class SoupModelCheckerTest {
    String modelPath = "../soup-models/alice-bob/";
    IExecutable<EmptinessCheckerAnswer<?>> predicateMC(Soup model, Expression predicate) {
        var soupModelChecker = new SoupSoupModelChecker(
                model,
                null,
                false,
                predicate,
                DepthFirstTraversal.Algorithm.WHILE,
                -1);
        return soupModelChecker.modelChecker();
    }

    Soup readSoup(String modelName) throws IOException, ParseException {
        return Reader.readSoup(new BufferedReader(new FileReader(modelPath + modelName)));
    }

    IExecutable<EmptinessCheckerAnswer<?>> mc(Soup model, Soup property, Expression predicate, boolean isBuchi) {
        var soupModelChecker = new SoupSoupModelChecker(
                model,
                property,
                isBuchi,
                predicate,
                DepthFirstTraversal.Algorithm.WHILE,
                -1);
        return soupModelChecker.modelChecker();
    }
    IExecutable<EmptinessCheckerAnswer<?>> safetyMc(Soup model, Soup property, Expression predicate) {
        return mc(model, property, predicate, false);
    }

    IExecutable<EmptinessCheckerAnswer<?>> buchiMc(Soup model, Soup property, Expression predicate) {
        return mc(model, property, predicate, true);
    }

    /// ALICE BOB 0
    @Test
    void testAliceBob0Exclusion() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var pred = Reader.readExpression("a==2 && b==2");
        var result = predicateMC(model, pred).runAlone();
        assertFalse(result.holds);
    }

    @Test
    void testAliceBob0ExclusionSafety() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var prop = readSoup("dependent/exclusion.soup");
        var pred = Reader.readExpression("!status");
        var result = safetyMc(model, prop, pred).runAlone();
        assertFalse(result.holds);
    }

    @Test
    void testAliceBob0Deadlock() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var pred = Reader.readExpression("deadlock");
        var result = predicateMC(model, pred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob0DeadlockSafety() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var prop = readSoup("dependent/no-deadlock.soup");
        var pred = Reader.readExpression("!status");
        var result = safetyMc(model, prop, pred).runAlone();
        assertTrue(result.holds);
        System.out.println(result);
    }

    @Test
    void testAliceBob0BuchiOneIn() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var prop = readSoup("dependent/buchi_eventuallyOneInCS.soup");
        var pred = Reader.readExpression("!status");
        var result = buchiMc(model, prop, pred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob0BuchiFairness() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var prop = readSoup("dependent/buchi_fairness.soup");
        var pred = Reader.readExpression("!status");
        var result = buchiMc(model, prop, pred).runAlone();
        assertFalse(result.holds);
    }

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
        var pred = Reader.readExpression(/*right:*/"p:fail");
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
}
