package soup.fx;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import obp3.fx.objectbrowser.api.ObjectView;
import obp3.fx.objectbrowser.api.ObjectViewFor;
import soup.semantics.base.Environment;
import soup.syntax.model.declarations.Soup;

@ObjectViewFor(Environment.class)
public class EnvironmentView implements ObjectView {
    VBox vbox = new VBox();
    SoupView soupView = new SoupView();

    @Override
    public String getName() {
        return "";
    }

    @Override
    public Node getView() {
        return soupView.getView();
    }

    @Override
    public void setObject(Object object) {
        var env = (Environment) object;
        var soup = (Soup)env.model;
        soupView.setObject(soup);

        for (var vv : env.environment.entrySet()) {
            soupView.setVariableValue(vv.getKey(), vv.getValue());
        }
    }
}
