package soup.syntax.model.declarations;

import soup.syntax.model.FunctionalVisitor;
import soup.syntax.model.Position;
import soup.syntax.model.SyntaxTreeElement;
import soup.syntax.model.declarations.pieces.AnonymousPiece;

import java.util.List;
import java.util.Objects;

public class Soup extends SyntaxTreeElement {
    public List<VariableDeclaration> variables;
    public List<AnonymousPiece> pieces;
    public Soup(
            List<VariableDeclaration> variables,
            List<AnonymousPiece> pieces,
            Position position) {
        super(position);
        this.variables = variables;
        this.pieces = pieces;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Soup soup)) return false;
        return Objects.equals(variables, soup.variables) && Objects.equals(pieces, soup.pieces);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variables, pieces);
    }

    @Override
    public <I, O> O accept(FunctionalVisitor<I, O> visitor, I input) {
        return visitor.visit(this, input);
    }
}
