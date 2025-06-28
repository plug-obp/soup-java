package soup.syntax.model.declarations.pieces;

import soup.syntax.model.FunctionalVisitor;
import soup.syntax.model.Position;
import soup.syntax.model.SyntaxTreeElement;
import soup.syntax.model.expressions.Expression;
import soup.syntax.model.statements.Statement;

import java.util.Objects;

public class AnonymousPiece extends SyntaxTreeElement {
    public Expression guard;
    public Statement effect;
    public AnonymousPiece(
            Expression guard,
            Statement effect,
            Position position) {
        super(position);
        this.guard = guard;
        this.effect = effect;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnonymousPiece that)) return false;
        return Objects.equals(guard, that.guard) && Objects.equals(effect, that.effect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guard, effect);
    }

    @Override
    public <I, O> O accept(FunctionalVisitor<I, O> visitor, I input) {
        return visitor.visit(this, input);
    }
}
