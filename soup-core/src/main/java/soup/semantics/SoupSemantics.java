package soup.semantics;

import obp3.sli.core.SemanticRelation;
import soup.syntax.model.declarations.Soup;
import soup.syntax.model.declarations.pieces.AnonymousPiece;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SoupSemantics implements SemanticRelation<AnonymousPiece, RuntimeEnvironment> {
    Soup model;
    ExpressionInterpreter expressionInterpreter = new ExpressionInterpreter();
    StatementInterpreter statementInterpreter = new StatementInterpreter(expressionInterpreter);

    public SoupSemantics(Soup model) {
        var expI = new ExpressionInterpreter();
        this(model, expI, new StatementInterpreter(expI));
    }

    public SoupSemantics(Soup model, ExpressionInterpreter expressionInterpreter) {
        this(model, expressionInterpreter, new StatementInterpreter(expressionInterpreter));
    }

    public SoupSemantics(
            Soup model,
            ExpressionInterpreter expressionInterpreter,
            StatementInterpreter statementInterpreter) {
        this.model = model;
        this.expressionInterpreter = expressionInterpreter;
        this.statementInterpreter = statementInterpreter;
    }

    @Override
    public List<RuntimeEnvironment> initial() {
        var runtimeEnvironment = new RuntimeEnvironment(model);
        for (var variable : model.variables) {
            runtimeEnvironment.define(
                    variable.name,
                    variable.initial.accept(expressionInterpreter, runtimeEnvironment));
        }
        return Collections.singletonList(runtimeEnvironment);
    }

    @Override
    public List<AnonymousPiece> actions(RuntimeEnvironment configuration) {
        return configuration.model.pieces.stream().filter(
                piece -> {
                    var guard = piece.guard.accept(expressionInterpreter, configuration);
                    return expressionInterpreter.ensureBoolean("guard", guard);
                }).collect(Collectors.toList());
    }

    @Override
    public List<RuntimeEnvironment> execute(AnonymousPiece action, RuntimeEnvironment configuration) {
        return Collections.singletonList(
                action.effect.accept(statementInterpreter, configuration)
        );
    }

    public SemanticRelation<AnonymousPiece, RuntimeEnvironment> pureSemantics() {
        return new SemanticRelation<AnonymousPiece, RuntimeEnvironment>() {
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
