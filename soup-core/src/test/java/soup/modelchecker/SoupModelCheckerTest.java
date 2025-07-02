package soup.modelchecker;

import obp3.IExecutable;
import obp3.modelchecking.EmptinessCheckerAnswer;
import obp3.traversal.dfs.DepthFirstTraversal;
import org.junit.jupiter.api.Test;
import soup.syntax.Reader;
import soup.syntax.model.declarations.Soup;
import soup.syntax.model.expressions.Expression;

import java.io.BufferedReader;
import java.io.FileReader;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void testAliceBob0ExclusionNOK() throws Exception {
        var path = "../soup-models/alice-bob/";
        var model = Reader.readSoup(new BufferedReader(new FileReader(modelPath + "alice-bob0.soup")));
        var pred = Reader.readExpression("a==2 && b==2");
        var result = predicateMC(model, pred).runAlone();
        assertFalse(result.holds);
    }

    @Test
    void testAliceBob1ExclusionOK() throws Exception {
        var path = "../soup-models/alice-bob/";
        var model = Reader.readSoup(new BufferedReader(new FileReader(modelPath + "alice-bob1.soup")));
        var pred = Reader.readExpression("a==2 && b==2");
        var result = predicateMC(model, pred).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob1DeadlockNOK() throws Exception {
        var path = "../soup-models/alice-bob/";
        var model = Reader.readSoup(new BufferedReader(new FileReader(modelPath + "alice-bob1.soup")));
        var pred = Reader.readExpression("deadlock");
        var result = predicateMC(model, pred).runAlone();
        assertFalse(result.holds);
    }
}
