package soup.syntax.model;

public abstract class SyntaxTreeElement {
    public Position position;
    public SyntaxTreeElement(Position position) {
        this.position = position;
    }

    public <I, O> O accept(FunctionalVisitor<I, O> visitor, I input) {
        return visitor.visit(this, input);
    }

    @Override
    public String toString() {
        return this.accept(new ToString(), null);
    }
}
