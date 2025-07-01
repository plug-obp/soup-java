package soup.semantics;

import obp3.sli.core.MaybeStutter;
import soup.syntax.model.declarations.pieces.AnonymousPiece;
import soup.syntax.model.declarations.pieces.NamedPiece;

import java.util.Objects;

public class StepRuntimeEnvironment extends RuntimeEnvironment {
    MaybeStutter<AnonymousPiece> action;
    RuntimeEnvironment target;

    public StepRuntimeEnvironment(RuntimeEnvironment source, MaybeStutter<AnonymousPiece> action, RuntimeEnvironment target) {
        super(source);
        this.action = action;
        this.target = target;
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
        if (!(o instanceof StepRuntimeEnvironment that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(action, that.action) && Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), action, target);
    }
}
