package soup.semantics;

import soup.syntax.model.dependent.EnabledExpression;
import soup.syntax.model.dependent.NamedPieceReference;
import soup.syntax.model.dependent.PrimedReference;
import soup.syntax.model.expressions.Reference;

public class StepExpressionSemantics extends ExpressionSemantics {
    @Override
    public Object visit(Reference node, RuntimeEnvironment environment) {
        var env = (StepRuntimeEnvironment) environment;
        //if the reference is "deadlock"
        if (node.name.equals("deadlock")) {
            //and the piece is not a soup action, then deadlock
            if (!env.isSoupAction()) return true;
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
