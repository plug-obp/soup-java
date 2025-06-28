package soup.syntax.model.declarations.pieces;

import soup.syntax.model.FunctionalVisitor;
import soup.syntax.model.Position;
import soup.syntax.model.expressions.Expression;
import soup.syntax.model.statements.Statement;

import java.util.Objects;

public class NamedPiece extends AnonymousPiece {
    public String name;
    public NamedPiece(String name, Expression guard, Statement effect, Position position) {
        super(guard, effect, position);
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NamedPiece that)) return false;
        return Objects.equals(name, that.name) && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }

    @Override
    public <I, O> O accept(FunctionalVisitor<I, O> visitor, I input) {
        return visitor.visit(this, input);
    }
}
