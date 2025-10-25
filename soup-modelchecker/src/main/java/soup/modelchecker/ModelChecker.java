package soup.modelchecker;

import obp3.runtime.IExecutable;
import obp3.modelchecking.EmptinessCheckerAnswer;
import obp3.modelchecking.buchi.ndfs.gs09.cdlp05.separated.EmptinessCheckerBuchiGS09CDLP05Separated;
import obp3.modelchecking.safety.SafetyDephtFirstTraversal;
import obp3.runtime.sli.DependentSemanticRelation;
import obp3.runtime.sli.SemanticRelation;
import obp3.sli.core.operators.SemanticRelation2RootedGraph;
import obp3.runtime.sli.Step;
import obp3.sli.core.operators.product.StepSynchronousProductSemantics;
import obp3.sli.core.operators.product.model.StepProductParameters;
import obp3.traversal.dfs.DepthFirstTraversal;

import java.util.function.Predicate;

public abstract class ModelChecker<MA, MC, PA, PC> {
    public abstract SemanticRelation<MA, MC> getModelSemantics();
    public abstract DependentSemanticRelation<Step<MA, MC>, PA, PC> getPropertySemantics();
    public abstract boolean isBuchi();
    public abstract <X> Predicate<X> acceptingPredicate();

    DepthFirstTraversal.Algorithm traversalAlgorithm;
    int depthBound;

    public ModelChecker(
            DepthFirstTraversal.Algorithm traversalAlgorithm,
            int depthBound) {
        this.traversalAlgorithm = traversalAlgorithm;
        this.depthBound = depthBound;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public IExecutable<EmptinessCheckerAnswer<?>> modelChecker() {
        if (getPropertySemantics() == null) {
            var rg = new SemanticRelation2RootedGraph<>(getModelSemantics());
            return (IExecutable)new SafetyDephtFirstTraversal<>(
                    traversalAlgorithm, rg, depthBound, null, acceptingPredicate());
        }
        var product = new StepSynchronousProductSemantics<>(new StepProductParameters<>(getModelSemantics(), getPropertySemantics()));
        var rg = new SemanticRelation2RootedGraph<>(product);

        return (isBuchi()) ?
                (IExecutable)new EmptinessCheckerBuchiGS09CDLP05Separated<>(
                        traversalAlgorithm, rg, depthBound, null, (c) -> acceptingPredicate().test(c.r())) :
                (IExecutable)new SafetyDephtFirstTraversal<>(traversalAlgorithm, rg, depthBound, null, (c) -> acceptingPredicate().test(c.r()));
    }
}
