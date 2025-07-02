package soup.semantics.diagnosis;

import soup.semantics.base.Environment;
import soup.semantics.base.ExpressionSemantics;
import soup.semantics.base.SoupSemantics;
import soup.syntax.model.declarations.Soup;
import soup.syntax.model.expressions.Reference;

public class DiagnosisExpressionSemantics extends ExpressionSemantics {
    @Override
    public Object visit(Reference<?> node, Environment environment) {
        if (node.name.equals("deadlock")) {
            if (environment.model instanceof Soup soup) {
                var semantics = new SoupSemantics(soup);
                return semantics.actions(environment).isEmpty();
            }
        }
        return environment.lookup(node.name);
    }
}
