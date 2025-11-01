package soup.modelchecker;

import rege.modelchecker.StepModelChecker;
import obp3.modelchecking.EmptinessCheckerAnswer;
import obp3.runtime.IExecutable;
import obp3.runtime.sli.Step;
import soup.semantics.base.Environment;
import soup.semantics.base.SoupSemantics;
import soup.semantics.diagnosis.StepEnvironment;
import soup.semantics.diagnosis.StepExpressionSemantics;
import soup.syntax.model.declarations.Soup;
import soup.syntax.model.declarations.pieces.AnonymousPiece;

public class SoupRegeModelChecker {
    public static IExecutable<EmptinessCheckerAnswer<?>> soupRegeModelChecker(
            Soup modelSoup,
            String property) {
        var checker = new StepModelChecker<>(
                new SoupSemantics(modelSoup).pureSemantics(),
                SoupRegeModelChecker::stepAtomEvaluator,
                property);
        return checker.modelChecker();
    }

    public static boolean stepAtomEvaluator(String atom, Step<AnonymousPiece, Environment> step) {
        try {
            return (boolean) StepExpressionSemantics.evaluate(atom, new StepEnvironment(step.start(), step.action(), step.end()));
        } catch (Exception _) {
            throw new RuntimeException("Atom '%s' evaluation failed.".formatted(atom));
        }
    };
}
