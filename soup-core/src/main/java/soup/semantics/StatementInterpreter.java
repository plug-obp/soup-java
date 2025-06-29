package soup.semantics;

import soup.syntax.model.FunctionalVisitorBase;
import soup.syntax.model.statements.Assignment;
import soup.syntax.model.statements.IfStatement;
import soup.syntax.model.statements.Sequence;
import soup.syntax.model.statements.Skip;

public class StatementInterpreter extends FunctionalVisitorBase<RuntimeEnvironment, RuntimeEnvironment> {
    ExpressionInterpreter expressionInterpreter;
    public StatementInterpreter(ExpressionInterpreter expressionInterpreter) {
        this.expressionInterpreter = expressionInterpreter;
    }

    public RuntimeEnvironment visit(Skip node, RuntimeEnvironment environment) {
        return environment;
    }

    public RuntimeEnvironment visit(Assignment node, RuntimeEnvironment environment) {
        var value = node.expression.accept(expressionInterpreter, environment);
        environment.update(node.target.name, value);
        return environment;
    }

    public RuntimeEnvironment visit(IfStatement node, RuntimeEnvironment environment) {
        var cond = node.condition.accept(expressionInterpreter, environment);
        var condV = expressionInterpreter.ensureBoolean("if", cond);
        if (condV) {
            return node.thenStatement.accept(this, environment);
        }
        return node.elseStatement.accept(this, environment);
    }

    public RuntimeEnvironment visit(Sequence node, RuntimeEnvironment environment) {
        var afterLeft = node.left.accept(this, environment);
        return node.right.accept(this, afterLeft);
    }
}
