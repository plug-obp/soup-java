package soup.semantics;

import soup.syntax.Reader;
import soup.syntax.model.dependent.EnabledExpression;
import soup.syntax.model.dependent.NamedPieceReference;
import soup.syntax.model.dependent.PrimedReference;
import soup.syntax.model.expressions.Expression;
import soup.syntax.model.expressions.Reference;

public class StepExpressionSemantics extends ExpressionSemantics {

    public static Object evaluate(String expressionString, StepEnvironment environment) throws Exception {
        var expression = Reader.readExpression(expressionString);
        return expression.accept(new StepExpressionSemantics(), environment);
    }

    public Object evaluate(Expression expression, StepEnvironment environment) {
        return super.evaluate(expression, environment);
    }

    @Override
    public Object visit(Reference node, Environment environment) {
        var env = (StepEnvironment) environment;
        //if the reference is "deadlock"
        if (node.name.equals("deadlock")) {
            //and the piece is not a soup action, then deadlock
            if (env.isStutter() && ((StepEnvironment) environment).selfLoop()) return true;
            return false;
        }
        return environment.lookup(node.name);
    }

    @Override
    public Object visit(PrimedReference node, Environment environment) {
        var env = (StepEnvironment) environment;
        return env.targetLookup(node.name);
    }

    @Override
    public Object visit(NamedPieceReference node, Environment environment) {
        var env = (StepEnvironment) environment;
        return env.actionMatch(node.name);
    }

    @Override
    public Object visit(EnabledExpression node, Environment environment) {
        return node.operand.accept(this, environment);
    }
}
