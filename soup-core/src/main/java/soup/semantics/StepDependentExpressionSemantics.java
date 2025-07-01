package soup.semantics;

import soup.syntax.model.dependent.InputReference;
import soup.syntax.model.expressions.Expression;

public class StepDependentExpressionSemantics extends ExpressionSemantics {
    StepExpressionSemantics inputSemantics;
    public StepDependentExpressionSemantics(StepExpressionSemantics inputSemantics) {
        super();
        this.inputSemantics = inputSemantics;
    }

    public Object evaluate(Expression expression, StepDependentEnvironment environment) {
        return super.evaluate(expression, environment);
    }

    @Override
    public Object visit(InputReference node, Environment environment) {
        var step = ((StepDependentEnvironment)environment).input;
        var env = new StepEnvironment(step.start(), step.action(), step.end());
        return node.operand.accept(inputSemantics, env);
    }
}
