package soup.modelchecker;

import gpsl.modelchecker.StepModelChecker;
import obp3.modelchecking.EmptinessCheckerAnswer;
import obp3.runtime.IExecutable;
import obp3.runtime.sli.Step;
import soup.semantics.base.Environment;
import soup.semantics.base.SoupSemantics;
import soup.semantics.diagnosis.StepEnvironment;
import soup.semantics.diagnosis.StepExpressionSemantics;
import soup.syntax.model.declarations.Soup;
import soup.syntax.model.declarations.pieces.AnonymousPiece;

public class SoupGPSLModelChecker {

    public static IExecutable<EmptinessCheckerAnswer<?>> soupGPSLModelChecker(
            Soup modelSoup,
            String property) {
        var checker = new StepModelChecker<>(
                new SoupSemantics(modelSoup).pureSemantics(),
                SoupGPSLModelChecker::stepAtomEvaluator,
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
