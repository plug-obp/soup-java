package soup.syntax.model.declarations;

import soup.syntax.model.FunctionalVisitor;
import soup.syntax.model.Position;
import soup.syntax.model.SyntaxTreeElement;
import soup.syntax.model.expressions.Expression;

import java.util.Objects;

public class VariableDeclaration extends SyntaxTreeElement {
    public String name;
    public Expression initial;
    public VariableDeclaration(
            String name,
            Expression initial,
            Position position) {
        super(position);
        this.name = name;
        this.initial = initial;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VariableDeclaration that)) return false;
        return Objects.equals(name, that.name) && Objects.equals(initial, that.initial);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, initial);
    }

    @Override
    public <I, O> O accept(FunctionalVisitor<I, O> visitor, I input) {
        return visitor.visit(this, input);
    }
}
