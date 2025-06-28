package soup.syntax.model.expressions.literals;

import soup.syntax.model.FunctionalVisitor;
import soup.syntax.model.Position;

public class BooleanLiteral extends Literal<Boolean> {
    public static final BooleanLiteral TRUE = new BooleanLiteral(true, Position.ZERO);
    public static final BooleanLiteral FALSE = new BooleanLiteral(false, Position.ZERO);

    private BooleanLiteral(boolean value, Position position) {
        super(position);
        this.value = value;
    }

    @Override
    public <I, O> O accept(FunctionalVisitor<I, O> visitor, I input) {
        return visitor.visit(this, input);
    }
}
