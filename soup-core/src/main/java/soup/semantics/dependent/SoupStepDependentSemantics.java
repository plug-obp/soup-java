package soup.semantics.dependent;

import obp3.runtime.sli.DependentSemanticRelation;
import obp3.runtime.sli.Step;
import soup.semantics.base.Environment;
import soup.semantics.base.StatementSemantics;
import soup.semantics.diagnosis.StepExpressionSemantics;
import soup.syntax.model.declarations.Soup;
import soup.syntax.model.declarations.pieces.AnonymousPiece;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SoupStepDependentSemantics implements DependentSemanticRelation<Step<AnonymousPiece, Environment>, AnonymousPiece, Environment> {
    Soup model;
    StepExpressionSemantics inputSemantics;
    StepDependentExpressionSemantics expressionSemantics;
    StatementSemantics statementSemantics;

    public SoupStepDependentSemantics(Soup model) {
        this.model = model;
        this.inputSemantics = new StepExpressionSemantics();
        this.expressionSemantics = new StepDependentExpressionSemantics(this.inputSemantics);
        this.statementSemantics = new StatementSemantics(this.expressionSemantics);
    }

    public SoupStepDependentSemantics(
            Soup model,
            StepExpressionSemantics inputSemantics,
            StepDependentExpressionSemantics expressionSemantics,
            StatementSemantics statementSemantics) {
        this.model = model;
        this.inputSemantics = inputSemantics;
        this.expressionSemantics = expressionSemantics;
        this.statementSemantics = statementSemantics;
    }

    @Override
    public List<Environment> initial() {
        var environment = new StepDependentEnvironment(model, null);
        for (var variable : model.variables) {
            environment.define(variable.name, expressionSemantics.evaluate(variable.initial, environment));
        }
        return Collections.singletonList(environment);
    }

    @Override
    public List<AnonymousPiece> actions(Step<AnonymousPiece, Environment> input, Environment configuration) {
        if (!(configuration.model instanceof Soup soup)) { return Collections.emptyList(); }
        var extendedConfiguration = new StepDependentEnvironment(input, configuration);
        return soup.pieces.stream().filter(
                piece -> {
                    var guard = piece.guard.accept(expressionSemantics, extendedConfiguration);
                    return expressionSemantics.ensureBoolean("guard", guard);
                }).collect(Collectors.toList());
    }

    @Override
    public List<Environment> execute(AnonymousPiece action, Step<AnonymousPiece, Environment> input, Environment configuration) {
        var extendedConfiguration = new StepDependentEnvironment(input, configuration);
        return Collections.singletonList(
                statementSemantics.evaluate(action.effect, extendedConfiguration)
        );
    }

    public DependentSemanticRelation<Step<AnonymousPiece, Environment>, AnonymousPiece, Environment> pureSemantics() {
        return new DependentSemanticRelation<>() {

            @Override
            public List<Environment> initial() {
                return SoupStepDependentSemantics.this.initial();
            }

            @Override
            public List<AnonymousPiece> actions(Step<AnonymousPiece, Environment> input, Environment configuration) {
                return SoupStepDependentSemantics.this.actions(input, configuration);
            }

            @Override
            public List<Environment> execute(AnonymousPiece action, Step<AnonymousPiece, Environment> input, Environment configuration) {
                var config = new Environment(configuration);
                return SoupStepDependentSemantics.this.execute(action, input, config);
            }
        };
    }
}
