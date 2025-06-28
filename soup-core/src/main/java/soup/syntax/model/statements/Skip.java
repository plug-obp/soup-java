package soup.syntax.model.statements;

import soup.syntax.model.FunctionalVisitor;
import soup.syntax.model.Position;

public class Skip extends Statement {
    public static final Skip INSTANCE = new Skip(Position.ZERO);
    private Skip(Position position) {
        super(position);
    }

    @Override
    public <I, O> O accept(FunctionalVisitor<I, O> visitor, I input) {
        return visitor.visit(this, input);
    }
}
