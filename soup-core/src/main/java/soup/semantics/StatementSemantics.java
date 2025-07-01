package soup.semantics;

import soup.syntax.model.FunctionalVisitorBase;
import soup.syntax.model.statements.*;

public class StatementSemantics extends FunctionalVisitorBase<RuntimeEnvironment, RuntimeEnvironment> {
    ExpressionSemantics expressionSemantics;
    public StatementSemantics(ExpressionSemantics expressionSemantics) {
        this.expressionSemantics = expressionSemantics;
    }

    public static RuntimeEnvironment evaluate(
            RuntimeEnvironment env) {
        return evaluate((Statement)env.model, new ExpressionSemantics(), env);
    }

    public static RuntimeEnvironment evaluate(
            Statement statement,
            RuntimeEnvironment env) {
        return evaluate(statement, new ExpressionSemantics(), env);
    }
    public static RuntimeEnvironment evaluate(
            Statement statement,
            ExpressionSemantics expressionSemantics,
            RuntimeEnvironment env) {
        var si = new StatementSemantics(expressionSemantics);
        return statement.accept(si, env);
    }

    public RuntimeEnvironment visit(Skip node, RuntimeEnvironment environment) {
        return environment;
    }

    public RuntimeEnvironment visit(Assignment node, RuntimeEnvironment environment) {
        var value = node.expression.accept(expressionSemantics, environment);
        environment.update(node.target.name, value);
        return environment;
    }

    public RuntimeEnvironment visit(IfStatement node, RuntimeEnvironment environment) {
        var cond = node.condition.accept(expressionSemantics, environment);
        var condV = expressionSemantics.ensureBoolean("if", cond);
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
