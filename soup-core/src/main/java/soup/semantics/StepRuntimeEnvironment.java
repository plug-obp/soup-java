package soup.semantics;

import soup.syntax.model.declarations.Soup;
import soup.syntax.model.declarations.pieces.AnonymousPiece;
import soup.syntax.model.declarations.pieces.NamedPiece;

public class StepRuntimeEnvironment extends RuntimeEnvironment {
    AnonymousPiece action;
    RuntimeEnvironment target;
    public StepRuntimeEnvironment(Soup model, AnonymousPiece action, RuntimeEnvironment target) {
        super(model);
        this.action = action;
        this.target = target;
    }

    public StepRuntimeEnvironment(RuntimeEnvironment source, AnonymousPiece action, RuntimeEnvironment target) {
        super(source);
        this.action = action;
        this.target = target;
    }

    public Object targetLookup(String key) {
        return target.lookup(key);
    }

    public boolean actionMatch(String name) {
        return action instanceof NamedPiece && ((NamedPiece) action).name.equals(name);
    }

    public boolean isSoupAction() {
        return action != null;
    }


}
