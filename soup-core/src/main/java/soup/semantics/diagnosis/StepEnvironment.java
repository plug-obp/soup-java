package soup.semantics.diagnosis;

import soup.semantics.base.Environment;
import soup.syntax.model.declarations.pieces.AnonymousPiece;
import soup.syntax.model.declarations.pieces.NamedPiece;

import java.util.Objects;
import java.util.Optional;

public class StepEnvironment extends Environment {
    public Optional<AnonymousPiece> action;
    public Environment target;

    public StepEnvironment(Environment source, AnonymousPiece action, Environment target) {
        super(source);
        this.action = Optional.of(action);
        this.target = target;
    }

    public StepEnvironment(Environment source, Environment target) {
        super(source);
        this.action = Optional.empty();
        this.target = target;
    }

    public StepEnvironment(StepEnvironment base) {
        super(base);
        this.action = base.action;
        this.target = new Environment(base.target);
    }

    public Object targetLookup(String key) {
        return target.lookup(key);
    }

    public boolean actionMatch(String name) {
        if (action.isEmpty()) return false;
        return action.get() instanceof NamedPiece piece && piece.name.equals(name);
    }

    public boolean isSoupAction() {
        return action != null && action.isPresent();
    }

    public boolean isStutter() {
        return action != null && action.isEmpty();
    }

    public boolean selfLoop() {
        if (target == null) return false;
        return target.equals(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StepEnvironment that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(action, that.action) && Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), action, target);
    }
}
