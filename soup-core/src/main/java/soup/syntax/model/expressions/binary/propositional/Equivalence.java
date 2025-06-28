package soup.syntax.model.expressions.binary.propositional;

import soup.syntax.model.FunctionalVisitor;
import soup.syntax.model.Position;
import soup.syntax.model.expressions.Expression;
import soup.syntax.model.expressions.binary.BinaryExpression;

public class Equivalence extends BinaryExpression {
    public Equivalence(String operator, Expression left, Expression right, Position position) {
        super(operator, left, right, position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equivalence)) return false;
        return super.equals(o);
    }

    @Override
    public <I, O> O accept(FunctionalVisitor<I, O> visitor, I input) {
        return visitor.visit(this, input);
    }
}
