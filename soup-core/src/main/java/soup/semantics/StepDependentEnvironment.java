package soup.semantics;

import soup.syntax.model.SyntaxTreeElement;

public class StepDependentEnvironment extends Environment {
    StepEnvironment input;

    public StepDependentEnvironment(SyntaxTreeElement model, StepEnvironment input) {
        super(model);
        this.input = input;
    }

    public StepDependentEnvironment(StepEnvironment input, Environment configuration) {
        super(configuration);
        this.input = input;
    }

    public StepDependentEnvironment(StepDependentEnvironment base) {
        super(base);
        this.input = base.input;
    }
}
