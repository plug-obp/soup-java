package soup.fx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import obp3.fx.objectbrowser.api.ObjectView;
import obp3.fx.objectbrowser.api.ObjectViewFor;
import soup.syntax.model.declarations.Soup;
import soup.syntax.model.expressions.Expression;

import java.util.List;
import java.util.stream.Collectors;

@ObjectViewFor(Soup.class)
public class SoupView implements ObjectView {
    VBox vbox = new VBox();
    ObservableList<VariableData> vars;

    @Override
    public String getName() {
        return "Soup View";
    }

    @Override
    public Node getView() {
        return vbox;
    }

    @Override
    public void setObject(Object object) {
        var soup = (Soup) object;
        vars = soup.variables.stream()
                .map(
                e -> new VariableData(e.name, e.initial, null))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        var variables = new ListView<>(vars);
        variables.setCellFactory(param -> new VariableDataListCell());
        vbox.getChildren().add(variables);

        ObservableList<Text> pieces = soup.pieces.stream()
                        .map(p -> new Text(p.toString()))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));
        vbox.getChildren().add(new Label("Pieces:"));
        vbox.getChildren().add(new ListView<>(pieces));
    }

    public void setVariableValue(String name, Object value) {
        if (vars == null || vars.isEmpty()) return;
        for (var v : vars) {
            if (v.name.equals(name)) {
                v.value = value;
                break;
            }
        }
    }

    public class VariableData {
        public String name;
        public Expression defaultValue;
        public Object value;
        public VariableData(String name, Expression defaultValue, Object value) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.value = value;
        }
    }

    public class VariableDataListCell extends ListCell<VariableData> {
        @Override
        protected void updateItem(VariableData item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                Text name = new Text(item.name);
                name.setFont(Font.font("Menlo", FontPosture.REGULAR, 12));
                name.setFill(Color.DARKSLATEGRAY);

                Text defaultValue = new Text("=<" + item.defaultValue.toString() + ">");
                defaultValue.setFont(Font.font("Menlo", FontPosture.ITALIC, 12));
                defaultValue.setFill(Color.SLATEGRAY);

                if (item.value == null) {
                    setGraphic(new TextFlow(name, defaultValue));
                    return;
                }
                Text value = new Text(" = " + item.value);
                value.setFont(Font.font("Menlo", FontPosture.REGULAR, 12));
                name.setFill(Color.DARKSLATEGRAY);

                TextFlow flow = new TextFlow(name, defaultValue, value);
                setGraphic(flow);
            }
        }
    }
}
