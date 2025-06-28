package soup.syntax.model.statements;

import soup.syntax.model.FunctionalVisitor;
import soup.syntax.model.Position;
import soup.syntax.model.declarations.VariableDeclaration;
import soup.syntax.model.expressions.Expression;
import soup.syntax.model.expressions.Reference;

import java.util.Objects;

public class Assignment extends Statement{
    public Reference<VariableDeclaration> target;
    public Expression expression;
    public Assignment(Reference<VariableDeclaration> target, Expression expression, Position position) {
        super(position);
        this.target = target;
        this.expression = expression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Assignment that)) return false;
        return Objects.equals(target, that.target) && Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, expression);
    }

    @Override
    public <I, O> O accept(FunctionalVisitor<I, O> visitor, I input) {
        return visitor.visit(this, input);
    }
}
