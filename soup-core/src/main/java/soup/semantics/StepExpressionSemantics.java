package soup.semantics;

import soup.syntax.Reader;
import soup.syntax.model.dependent.EnabledExpression;
import soup.syntax.model.dependent.NamedPieceReference;
import soup.syntax.model.dependent.PrimedReference;
import soup.syntax.model.expressions.Expression;
import soup.syntax.model.expressions.Reference;

public class StepExpressionSemantics extends ExpressionSemantics {

    public static Object evaluate(Expression expression, StepRuntimeEnvironment environment) {
        return expression.accept(new StepExpressionSemantics(), environment);
    }

    public static Object evaluate(String expressionString, StepRuntimeEnvironment environment) throws Exception {
        var expression = Reader.readExpression(expressionString);
        return expression.accept(new StepExpressionSemantics(), environment);
    }

    @Override
    public Object visit(Reference node, RuntimeEnvironment environment) {
        var env = (StepRuntimeEnvironment) environment;
        //if the reference is "deadlock"
        if (node.name.equals("deadlock")) {
            //and the piece is not a soup action, then deadlock
            if (env.isStutter() && ((StepRuntimeEnvironment) environment).selfLoop()) return true;
            return false;
        }
        return environment.lookup(node.name);
    }

    @Override
    public Object visit(PrimedReference node, RuntimeEnvironment environment) {
        var env = (StepRuntimeEnvironment) environment;
        return env.targetLookup(node.name);
    }

    @Override
    public Object visit(NamedPieceReference node, RuntimeEnvironment environment) {
        var env = (StepRuntimeEnvironment) environment;
        return env.actionMatch(node.name);
    }

    @Override
    public Object visit(EnabledExpression node, RuntimeEnvironment environment) {
        return node.operand.accept(this, environment);
    }
}
