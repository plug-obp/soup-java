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

public class SoupRegeModelCheckerTest {
    String modelPath = "../soup-models/alice-bob/";

    Soup readSoup(String modelName) throws IOException, ParseException {
        return Reader.readSoup(new BufferedReader(new FileReader(modelPath + modelName)));
    }

    IExecutable<EmptinessCheckerAnswer<?>> mc(Soup model, String property) {
        return SoupRegeModelChecker.soupRegeModelChecker(model, property);
    }

    final String exclusionRege = "τ[true]* ⋅ τ[a == 2 ∧ b == 2]";
    final String noDeadlockRege = "τ[true]* ⋅ τ [deadlock]";
    final String fullExploration = "τ [true]* ⋅ τ [false]";
    final String initialOk = "τ[a!=0 ∨ b!=0]";
    final String initialSafety = "τ[a==0 ∧ b==0 ∧ ¬dA ∧ ¬dB] ⋅ (τ[true])* ⋅ τ[a==2 ∧ b==2]";

    /// A process cannot enter CS without first raising its flag since the last idle.”
    ///The following cannot be expressed in the future-only LTL
    ///The trace contains at least one segment where the process goes idle, never raises its flag, and then enters its critical section.
    final String flagDiscipline = """
             (τ[true]* ⋅ τ[a==0] ⋅ τ[a≠1]* ⋅ τ[a==2] ⋅ τ[true]*)
           | (τ[true]* ⋅ τ[b==0] ⋅ τ[b≠1]* ⋅ τ[b==2] ⋅ τ[true]*)
            """;

    @Test
    void testAliceBob0ExclusionPred() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var result = mc(model, exclusionRege).runAlone();
        assertFalse(result.holds);
        assertEquals(6, result.trace.size());
    }

    @Test
    void testAliceBob1ExclusionPred() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var result = mc(model, exclusionRege).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob2ExclusionPred() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var result = mc(model, exclusionRege).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob0NoDeadlock() throws Exception {
        var model = readSoup("alice-bob0.soup");
        var result = mc(model, noDeadlockRege).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob1NoDeadlock() throws Exception {
        var model = readSoup("alice-bob1.soup");
        var result = mc(model, noDeadlockRege).runAlone();
        assertFalse(result.holds);
        assertEquals(4, result.trace.size());
    }

    @Test
    void testAliceBob2NoDeadlock() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var result = mc(model, noDeadlockRege).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob2Full() throws Exception {
        var model = readSoup("alice-bob2.soup");
        var result = mc(model, fullExploration).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob4InitialOk() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, initialOk).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob4InitialNok() throws Exception {
        var model1 = Reader.readSoup("var a = 1; b = 0;");
        var result1 = mc(model1, initialOk).runAlone();
        assertFalse(result1.holds);
    }

    @Test
    void testAliceBob4InitialSafety() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, initialSafety).runAlone();
        assertTrue(result.holds);
    }

    @Test
    void testAliceBob4FlagConsistency() throws Exception {
        var model = readSoup("alice-bob4.soup");
        var result = mc(model, flagDiscipline).runAlone();
        assertTrue(result.holds);
    }
}
