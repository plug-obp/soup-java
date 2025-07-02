package soup.semantics.base;

import obp3.sli.core.SemanticRelation;
import soup.syntax.model.declarations.Soup;
import soup.syntax.model.declarations.pieces.AnonymousPiece;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SoupSemantics implements SemanticRelation<AnonymousPiece, Environment> {
    Soup model;
    ExpressionSemantics expressionSemantics;
    StatementSemantics statementSemantics;

    public SoupSemantics(Soup model) {
        var expI = new ExpressionSemantics();
        this(model, expI, new StatementSemantics(expI));
    }

    public SoupSemantics(Soup model, ExpressionSemantics expressionSemantics) {
        this(model, expressionSemantics, new StatementSemantics(expressionSemantics));
    }

    public SoupSemantics(
            Soup model,
            ExpressionSemantics expressionSemantics,
            StatementSemantics statementSemantics) {
        this.model = model;
        this.expressionSemantics = expressionSemantics;
        this.statementSemantics = statementSemantics;
    }

    @Override
    public List<Environment> initial() {
        var environment = new Environment(model);
        for (var variable : model.variables) {
            environment.define(
                    variable.name,
                    expressionSemantics.evaluate(variable.initial, environment));
        }
        return Collections.singletonList(environment);
    }

    @Override
    public List<AnonymousPiece> actions(Environment configuration) {
        if (!(configuration.model instanceof Soup soup)) { return Collections.emptyList(); }
        return soup.pieces.stream().filter(
                piece -> {
                    var guard = piece.guard.accept(expressionSemantics, configuration);
                    return expressionSemantics.ensureBoolean("guard", guard);
                }).collect(Collectors.toList());
    }

    @Override
    public List<Environment> execute(AnonymousPiece action, Environment configuration) {
        return Collections.singletonList(
                statementSemantics.evaluate(action.effect, configuration)
        );
    }

    public SemanticRelation<AnonymousPiece, Environment> pureSemantics() {
        return new SemanticRelation<>() {
            @Override
            public List<Environment> initial() {
                return SoupSemantics.this.initial();
            }

            @Override
            public List<AnonymousPiece> actions(Environment configuration) {
                return SoupSemantics.this.actions(configuration);
            }

            @Override
            public List<Environment> execute(AnonymousPiece action, Environment configuration) {
                var runtimeEnvironment = new Environment(configuration);
                return SoupSemantics.this.execute(action, runtimeEnvironment);
            }
        };
    }
}
