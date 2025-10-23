package soup.semantics.dependent;

import obp3.runtime.sli.Step;
import soup.semantics.base.Environment;
import soup.syntax.model.SyntaxTreeElement;
import soup.syntax.model.declarations.pieces.AnonymousPiece;

public class StepDependentEnvironment extends Environment {
    Step<AnonymousPiece, Environment> input;

    public StepDependentEnvironment(SyntaxTreeElement model, Step<AnonymousPiece, Environment> input) {
        super(model);
        this.input = input;
    }

    public StepDependentEnvironment(Step<AnonymousPiece, Environment> input, Environment configuration) {
        super(configuration);
        this.input = input;
    }

    public StepDependentEnvironment(StepDependentEnvironment base) {
        super(base);
        this.input = base.input;
    }
}
