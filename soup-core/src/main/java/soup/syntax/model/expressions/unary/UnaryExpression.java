package soup.syntax.model.expressions.unary;

import soup.syntax.model.FunctionalVisitor;
import soup.syntax.model.expressions.Expression;
import soup.syntax.model.Position;

import java.util.Objects;

public abstract class UnaryExpression extends Expression {
    public String operator;
    public Expression operand;
    public UnaryExpression(String operator, Expression operand, Position position) {
        super(position);
        this.operator = operator;
        this.operand = operand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnaryExpression that)) return false;
        return Objects.equals(operand, that.operand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operand);
    }

    @Override
    public <I, O> O accept(FunctionalVisitor<I, O> visitor, I input) {
        return visitor.visit(this, input);
    }
}
