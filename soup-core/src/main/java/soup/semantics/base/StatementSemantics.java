package soup.semantics.base;

import soup.syntax.model.FunctionalVisitorBase;
import soup.syntax.model.statements.*;

public class StatementSemantics extends FunctionalVisitorBase<Environment, Environment> {
    ExpressionSemantics expressionSemantics;
    public StatementSemantics(ExpressionSemantics expressionSemantics) {
        this.expressionSemantics = expressionSemantics;
    }

    public static Environment evaluate(
            Environment env) {
        return evaluate((Statement)env.model, new ExpressionSemantics(), env);
    }

    public static Environment evaluate(
            Statement statement,
            ExpressionSemantics expressionSemantics,
            Environment env) {
        var si = new StatementSemantics(expressionSemantics);
        return statement.accept(si, env);
    }

    public Environment evaluate(Statement statement, Environment env) {
        return statement.accept(this, env);
    }

    public Environment visit(Skip node, Environment environment) {
        return environment;
    }

    public Environment visit(Assignment node, Environment environment) {
        var value = node.expression.accept(expressionSemantics, environment);
        environment.update(node.target.name, value);
        return environment;
    }

    public Environment visit(IfStatement node, Environment environment) {
        var cond = node.condition.accept(expressionSemantics, environment);
        var condV = expressionSemantics.ensureBoolean("if", cond);
        if (condV) {
            return node.thenStatement.accept(this, environment);
        }
        return node.elseStatement.accept(this, environment);
    }

    public Environment visit(Sequence node, Environment environment) {
        var afterLeft = node.left.accept(this, environment);
        return node.right.accept(this, afterLeft);
    }
}
