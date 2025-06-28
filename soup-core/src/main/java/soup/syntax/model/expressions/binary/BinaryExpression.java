package soup.syntax.model.expressions.binary;

import soup.syntax.model.FunctionalVisitor;
import soup.syntax.model.Position;
import soup.syntax.model.expressions.Expression;

import java.util.Objects;

public abstract class BinaryExpression extends Expression {
    public String operator;
    public Expression left, right;
    public BinaryExpression(String operator, Expression left, Expression right, Position position) {
        super(position);
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BinaryExpression that)) return false;
        return Objects.equals(left, that.left) && Objects.equals(right, that.right);
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
