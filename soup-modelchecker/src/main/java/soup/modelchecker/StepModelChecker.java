package soup.modelchecker;

import obp3.modelchecking.EmptinessCheckerAnswer;
import obp3.modelchecking.tools.BuchiModelCheckerModel;
import obp3.runtime.IExecutable;
import obp3.runtime.sli.DependentSemanticRelation;
import obp3.runtime.sli.SemanticRelation;
import obp3.runtime.sli.Step;
import obp3.traversal.dfs.DepthFirstTraversal;
import soup.syntax.Environment;
import soup.syntax.Reader;
import soup.syntax.model.declarations.Soup;
import soup.syntax.model.declarations.pieces.AnonymousPiece;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class StepModelChecker<MA, MC> {
    //model SLI
    SemanticRelation<MA, MC> modelSemantics;
    BiPredicate<String, Step<MA, MC>> atomicPropositionEvaluator;

    //property
    Soup propertyModel;

    //options
    BuchiModelCheckerModel.BuchiEmptinessCheckerAlgorithm emptinessCheckerAlgorithm;
    DepthFirstTraversal.Algorithm traversalAlgorithm;
    int depthBound;

    public StepModelChecker(
            SemanticRelation<MA, MC> modelSemantics,
            BiPredicate<String, Step<MA, MC>> atomicPropositionEvaluator,
            String property) throws IOException, ParseException {
        var propertyE = Reader.read(new BufferedReader(new StringReader(property)));

        this.modelSemantics = modelSemantics;
        this.atomicPropositionEvaluator = atomicPropositionEvaluator;
        this.propertyModel = propertyE;
        this.emptinessCheckerAlgorithm = BuchiModelCheckerModel.BuchiEmptinessCheckerAlgorithm.GS09_CDLP05_SEPARATED;
        this.traversalAlgorithm = DepthFirstTraversal.Algorithm.WHILE;
        this.depthBound = -1;
    }

    public StepModelChecker(
            SemanticRelation<MA, MC> modelSemantics,
            BiPredicate<String, Step<MA, MC>> atomicPropositionEvaluator,
            Soup propertyModel) {
        this(
                modelSemantics,
                atomicPropositionEvaluator,
                propertyModel,
                BuchiModelCheckerModel.BuchiEmptinessCheckerAlgorithm.GS09_CDLP05_SEPARATED,
                DepthFirstTraversal.Algorithm.WHILE,
                -1
        );
    }

    public StepModelChecker(
            SemanticRelation<MA, MC> modelSemantics,
            BiPredicate<String, Step<MA, MC>> atomicPropositionEvaluator,
            Soup propertyModel,
            BuchiModelCheckerModel.BuchiEmptinessCheckerAlgorithm emptinessCheckerAlgorithm,
            DepthFirstTraversal.Algorithm traversal,
            int depthBound) {
        this.modelSemantics = modelSemantics;
        this.atomicPropositionEvaluator = atomicPropositionEvaluator;
        this.propertyModel = propertyModel;
        this.emptinessCheckerAlgorithm = emptinessCheckerAlgorithm;
        this.traversalAlgorithm = traversal;
        this.depthBound = depthBound;
    }

    DependentSemanticRelation<Step<MA, MC>, AnonymousPiece, Environment> propertySemanticsProvider(BiFunction<String, Step<MA, MC>,  Boolean> atomEval) {
        return null;//new SoupStepDependentSemantics(propertyModel, atomEval).pureSemantics();
    }

    public IExecutable<?, EmptinessCheckerAnswer<?>> modelChecker() {
        return null;
    }
}