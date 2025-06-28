package soup.syntax.model.expressions.literals;

import soup.syntax.model.FunctionalVisitor;
import soup.syntax.model.Position;

public class IntegerLiteral extends Literal<Integer> {
    public IntegerLiteral(int value, Position position) {
        super(position);
        this.value = value;
    }

    @Override
    public <I, O> O accept(FunctionalVisitor<I, O> visitor, I input) {
        return visitor.visit(this, input);
    }
}
