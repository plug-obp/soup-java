package soup.syntax.model.expressions.literals;

import soup.syntax.model.FunctionalVisitor;
import soup.syntax.model.Position;
import soup.syntax.model.expressions.Expression;

import java.util.Objects;

public abstract class Literal<V> extends Expression {
    public V value;
    public Literal(Position position) {
        super(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Literal<?> literal)) return false;
        return Objects.equals(value, literal.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public <I, O> O accept(FunctionalVisitor<I, O> visitor, I input) {
        return visitor.visit(this, input);
    }
}
