package soup.syntax.model.expressions.unary;

import soup.syntax.model.FunctionalVisitor;
import soup.syntax.model.Position;
import soup.syntax.model.expressions.Expression;

public class MinusExpression extends UnaryExpression {
    public MinusExpression(Expression operand, Position position) {
        super("-", operand, position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MinusExpression)) return false;
        return super.equals(o);
    }

    @Override
    public <I, O> O accept(FunctionalVisitor<I, O> visitor, I input) {
        return visitor.visit(this, input);
    }
}
