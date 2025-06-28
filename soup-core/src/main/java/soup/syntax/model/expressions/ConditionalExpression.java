package soup.syntax.model.expressions;


import soup.syntax.model.FunctionalVisitor;
import soup.syntax.model.Position;

import java.util.Objects;

public class ConditionalExpression extends Expression {
    public Expression condition, thenExpression, elseExpression;
    public ConditionalExpression(
            Expression condition,
            Expression thenExpression,
            Expression elseExpression,
            Position position) {
        super(position);
        this.condition = condition;
        this.thenExpression = thenExpression;
        this.elseExpression = elseExpression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConditionalExpression that)) return false;
        return Objects.equals(condition, that.condition) && Objects.equals(thenExpression, that.thenExpression) && Objects.equals(elseExpression, that.elseExpression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, thenExpression, elseExpression);
    }

    @Override
    public <I, O> O accept(FunctionalVisitor<I, O> visitor, I input) {
        return visitor.visit(this, input);
    }
}
