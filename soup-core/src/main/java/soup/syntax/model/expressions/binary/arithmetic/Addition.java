package soup.syntax.model.expressions.binary.arithmetic;

import soup.syntax.model.FunctionalVisitor;
import soup.syntax.model.Position;
import soup.syntax.model.expressions.Expression;
import soup.syntax.model.expressions.binary.BinaryExpression;

public class Addition extends BinaryExpression {
    public Addition(Expression left, Expression right, Position position) {
        super("+", left, right, position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Addition)) return false;
        return super.equals(o);
    }

    @Override
    public <I, O> O accept(FunctionalVisitor<I, O> visitor, I input) {
        return visitor.visit(this, input);
    }
}
