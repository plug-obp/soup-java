package soup.syntax.model.statements;

import soup.syntax.model.FunctionalVisitor;
import soup.syntax.model.Position;

import java.util.Objects;

public class Sequence extends Statement {
    public Statement left;
    public Statement right;
    public Sequence(
            Statement left,
            Statement right,
            Position position) {
        super(position);
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sequence sequence)) return false;
        return Objects.equals(left, sequence.left) && Objects.equals(right, sequence.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public <I, O> O accept(FunctionalVisitor<I, O> visitor, I input) {
        return visitor.visit(this, input);
    }
}
