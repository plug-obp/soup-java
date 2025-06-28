package soup.syntax.model.expressions.binary.relational;

import soup.syntax.model.FunctionalVisitor;
import soup.syntax.model.Position;
import soup.syntax.model.expressions.Expression;
import soup.syntax.model.expressions.binary.BinaryExpression;

public class Equal extends BinaryExpression {
    public Equal(String operator, Expression left, Expression right, Position position) {
        super(operator, left, right, position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equal)) return false;
        return super.equals(o);
    }

    @Override
    public <I, O> O accept(FunctionalVisitor<I, O> visitor, I input) {
        return visitor.visit(this, input);
    }
}