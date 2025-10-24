package soup.modelchecker;

import obp3.runtime.sli.DependentSemanticRelation;
import obp3.runtime.sli.SemanticRelation;
import obp3.runtime.sli.Step;
import obp3.traversal.dfs.DepthFirstTraversal;
import soup.semantics.base.Environment;
import soup.semantics.base.SoupSemantics;
import soup.semantics.dependent.SoupStepDependentSemantics;
import soup.semantics.diagnosis.DiagnosisExpressionSemantics;
import soup.syntax.Reader;
import soup.syntax.model.declarations.Soup;
import soup.syntax.model.declarations.pieces.AnonymousPiece;
import soup.syntax.model.expressions.Expression;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.function.Predicate;

public class SoupSoupModelChecker extends ModelChecker<AnonymousPiece, Environment, AnonymousPiece, Environment> {

    Soup modelSoup;
    Soup propertySoup;
    boolean isBuchi = false;

    Expression acceptingPredicateExpression;

    DepthFirstTraversal.Algorithm traversalAlgorithm;
    int depthBound;

    @Override
    public SemanticRelation<AnonymousPiece, Environment> getModelSemantics() {
        return new SoupSemantics(modelSoup).pureSemantics();
    }

    @Override
    public DependentSemanticRelation<Step<AnonymousPiece, Environment>, AnonymousPiece, Environment> getPropertySemantics() {
        return propertySoup == null ? null : new SoupStepDependentSemantics(propertySoup).pureSemantics();
    }

    @Override
    public boolean isBuchi() {
        return isBuchi;
    }

    @Override
    public Predicate<Environment> acceptingPredicate() {
        return this::acceptingPredicateImp;
    }

    DiagnosisExpressionSemantics evaluator = new DiagnosisExpressionSemantics();
    boolean acceptingPredicateImp(Environment c) {
        return (boolean) evaluator.evaluate(acceptingPredicateExpression, c);
    }

    public SoupSoupModelChecker(
            Soup modelSoup,
            Soup propertySoup,
            boolean isBuchi,
            Expression acceptingPredicateExpression,
            DepthFirstTraversal.Algorithm traversal,
            int depthBound) {
        super(traversal, depthBound);
        this.modelSoup = modelSoup;
        this.propertySoup = propertySoup;
        this.isBuchi = isBuchi;
        this.acceptingPredicateExpression = acceptingPredicateExpression;
    }

    public static void main(String[] args) throws IOException, ParseException {
        var modelCode = new BufferedReader(new FileReader(args[0]));
        var model = Reader.read(modelCode);
        var propertyCode = new BufferedReader(new FileReader(args[1]));
        var property = Reader.read(propertyCode);
        var isBuchi = true;
        var acceptingPredicateCode = new BufferedReader(new StringReader(args[2]));
        var acceptingPredicate = Reader.readExpression(acceptingPredicateCode);
        var traversal = DepthFirstTraversal.Algorithm.WHILE;
        var depthBound = -1;

        var checker = new SoupSoupModelChecker(
                model,
                property,
                isBuchi,
                acceptingPredicate,
                traversal,
                depthBound
        );

        var modelChecker = checker.modelChecker();
        var result = modelChecker.runAlone();
        System.out.println(result);
    }
}
