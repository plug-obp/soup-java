package soup.syntax.model.statements;

import soup.syntax.model.FunctionalVisitor;
import soup.syntax.model.Position;
import soup.syntax.model.expressions.Expression;

import java.util.Objects;

public class IfStatement extends Statement {
    public Expression condition;
    public Statement thenStatement;
    public Statement elseStatement;
    public IfStatement(
            Expression condition,
            Statement thenStatement,
            Statement elseStatement,
            Position position) {
        super(position);
        this.condition = condition;
        this.thenStatement = thenStatement;
        this.elseStatement = elseStatement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IfStatement that)) return false;
        return Objects.equals(condition, that.condition) && Objects.equals(thenStatement, that.thenStatement) && Objects.equals(elseStatement, that.elseStatement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, thenStatement, elseStatement);
    }

    @Override
    public <I, O> O accept(FunctionalVisitor<I, O> visitor, I input) {
        return visitor.visit(this, input);
    }
}
