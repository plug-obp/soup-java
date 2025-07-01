package soup.semantics;

import obp3.sli.core.SemanticRelation;
import soup.syntax.model.declarations.Soup;
import soup.syntax.model.declarations.pieces.AnonymousPiece;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SoupSemantics implements SemanticRelation<AnonymousPiece, RuntimeEnvironment> {
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
    public List<RuntimeEnvironment> initial() {
        var runtimeEnvironment = new RuntimeEnvironment(model);
        for (var variable : model.variables) {
            runtimeEnvironment.define(
                    variable.name,
                    variable.initial.accept(expressionSemantics, runtimeEnvironment));
        }
        return Collections.singletonList(runtimeEnvironment);
    }

    @Override
    public List<AnonymousPiece> actions(RuntimeEnvironment configuration) {
        if (!(configuration.model instanceof Soup soup)) { return Collections.emptyList(); }
        return soup.pieces.stream().filter(
                piece -> {
                    var guard = piece.guard.accept(expressionSemantics, configuration);
                    return expressionSemantics.ensureBoolean("guard", guard);
                }).collect(Collectors.toList());
    }

    @Override
    public List<RuntimeEnvironment> execute(AnonymousPiece action, RuntimeEnvironment configuration) {
        return Collections.singletonList(
                action.effect.accept(statementSemantics, configuration)
        );
    }

    public SemanticRelation<AnonymousPiece, RuntimeEnvironment> pureSemantics() {
        return new SemanticRelation<>() {
            @Override
            public List<RuntimeEnvironment> initial() {
                return SoupSemantics.this.initial();
            }

            @Override
            public List<AnonymousPiece> actions(RuntimeEnvironment configuration) {
                return SoupSemantics.this.actions(configuration);
            }

            @Override
            public List<RuntimeEnvironment> execute(AnonymousPiece action, RuntimeEnvironment configuration) {
                var runtimeEnvironment = new RuntimeEnvironment(configuration);
                return SoupSemantics.this.execute(action, runtimeEnvironment);
            }
        };
    }
}
