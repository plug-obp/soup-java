package soup.syntax.model.expressions;

import soup.syntax.model.FunctionalVisitor;
import soup.syntax.model.Position;

import java.util.Objects;

public class Reference<Declaration> extends Expression {
    public String name;
    public Declaration declaration;
    public Reference(String name, Position position) {
        super(position);
        this.name = name;
    }

    public void setDeclaration(Declaration declaration) {
        this.declaration = declaration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reference<?> reference)) return false;
        return Objects.equals(name, reference.name) && Objects.equals(declaration, reference.declaration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, declaration);
    }

    @Override
    public <I, O> O accept(FunctionalVisitor<I, O> visitor, I input) {
        return visitor.visit(this, input);
    }
}
