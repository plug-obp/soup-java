package soup.syntax.model.expressions.literals;

import soup.syntax.model.FunctionalVisitor;
import soup.syntax.model.Position;

public class DoubleLiteral extends Literal<Double> {
    public DoubleLiteral(double value, Position position) {
        super(position);
        this.value = value;
    }

    @Override
    public <I, O> O accept(FunctionalVisitor<I, O> visitor, I input) {
        return visitor.visit(this, input);
    }
}
