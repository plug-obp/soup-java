package soup.syntax.model.dependent;

import soup.syntax.model.FunctionalVisitor;
import soup.syntax.model.Position;
import soup.syntax.model.declarations.pieces.NamedPiece;
import soup.syntax.model.expressions.Reference;

public class NamedPieceReference extends Reference<NamedPiece> {
    public NamedPieceReference(String name, Position position) {
        super(name, position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NamedPieceReference)) return false;
        return super.equals(o);
    }

    @Override
    public <I, O> O accept(FunctionalVisitor<I, O> visitor, I input) {
        return visitor.visit(this, input);
    }
}
