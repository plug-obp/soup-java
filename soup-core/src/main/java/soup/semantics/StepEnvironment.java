package soup.semantics;

import obp3.sli.core.MaybeStutter;
import soup.syntax.model.declarations.pieces.AnonymousPiece;
import soup.syntax.model.declarations.pieces.NamedPiece;

import java.util.Objects;

public class StepEnvironment extends Environment {
    MaybeStutter<AnonymousPiece> action;
    Environment target;

    public StepEnvironment(Environment source, MaybeStutter<AnonymousPiece> action, Environment target) {
        super(source);
        this.action = action;
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
        if (action.isStutter()) return false;
        return action.get() instanceof NamedPiece piece && piece.name.equals(name);
    }

    public boolean isSoupAction() {
        return action != null && action.isAction();
    }

    public boolean isStutter() {
        return action != null && action.isStutter();
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
