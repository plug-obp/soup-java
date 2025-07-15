package soup.fx.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import obp3.fx.objectbrowser.ObjectViewContainer;
import soup.semantics.base.SoupSemantics;
import soup.syntax.Reader;
import soup.syntax.model.Position;
import soup.syntax.model.declarations.Soup;
import soup.syntax.model.declarations.VariableDeclaration;
import soup.syntax.model.expressions.literals.IntegerLiteral;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ObjectBrowser extends Application {
    private BorderPane root = new BorderPane();

    @Override
    public void start(Stage primaryStage) throws IOException, ParseException {
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Soup Object Browser");

        String modelPath = "../soup-models/alice-bob/";
        var soup = Reader.readSoup(new BufferedReader(new FileReader(modelPath + "alice-bob5.soup")));
        var sem = new SoupSemantics(soup);
        var env = sem.initial().getFirst();
        var act = sem.actions(env).getFirst();
        env = sem.execute(act, env).getFirst();

        //Soup soup = new Soup(new ArrayList<>(List.of(new VariableDeclaration("x", new IntegerLiteral(3, Position.ZERO), Position.ZERO))), List.of(), Position.ZERO);

        root.setCenter(new ObjectViewContainer( env ).getView());

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}