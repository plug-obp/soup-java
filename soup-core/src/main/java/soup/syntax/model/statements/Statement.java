package soup.syntax.model.statements;

import soup.syntax.model.FunctionalVisitor;
import soup.syntax.model.Position;
import soup.syntax.model.SyntaxTreeElement;

public abstract class Statement extends SyntaxTreeElement {
    public Statement(Position position) {
        super(position);
    }

    @Override
    public <I, O> O accept(FunctionalVisitor<I, O> visitor, I input) {
        return visitor.visit(this, input);
    }
}
