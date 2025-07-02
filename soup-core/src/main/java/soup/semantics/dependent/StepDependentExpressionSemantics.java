package soup.semantics.dependent;

import soup.semantics.base.Environment;
import soup.semantics.base.ExpressionSemantics;
import soup.semantics.diagnosis.StepEnvironment;
import soup.semantics.diagnosis.StepExpressionSemantics;
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
        var env = step.action()
                .map((action) -> new StepEnvironment(step.start(), action, step.end()))
                .orElse(new StepEnvironment(step.start(), step.end()));
        return node.operand.accept(inputSemantics, env);
    }
}
