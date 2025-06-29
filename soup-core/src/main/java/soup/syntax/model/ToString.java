package soup.syntax.model;

import soup.syntax.model.declarations.Soup;
import soup.syntax.model.declarations.VariableDeclaration;
import soup.syntax.model.declarations.pieces.AnonymousPiece;
import soup.syntax.model.declarations.pieces.NamedPiece;
import soup.syntax.model.dependent.EnabledExpression;
import soup.syntax.model.dependent.InputReference;
import soup.syntax.model.dependent.NamedPieceReference;
import soup.syntax.model.dependent.PrimedReference;
import soup.syntax.model.expressions.ConditionalExpression;
import soup.syntax.model.expressions.Expression;
import soup.syntax.model.expressions.Reference;
import soup.syntax.model.expressions.binary.BinaryExpression;
import soup.syntax.model.expressions.binary.arithmetic.*;
import soup.syntax.model.expressions.binary.propositional.*;
import soup.syntax.model.expressions.binary.relational.*;
import soup.syntax.model.expressions.literals.BooleanLiteral;
import soup.syntax.model.expressions.literals.DoubleLiteral;
import soup.syntax.model.expressions.literals.IntegerLiteral;
import soup.syntax.model.expressions.literals.Literal;
import soup.syntax.model.expressions.unary.*;
import soup.syntax.model.statements.*;

import java.util.stream.Collectors;

public class ToString implements FunctionalVisitor<Void, String>{
    @Override
    public String visit(SyntaxTreeElement node, Void input) {
        return "";
    }

    @Override
    public String visit(Expression node, Void input) {
        return "";
    }

    @Override
    public <X> String visit(Literal<X> node, Void input) {
        return "";
    }

    @Override
    public String visit(BooleanLiteral node, Void input) {
        return node == BooleanLiteral.TRUE ? "true" : "false";
    }

    @Override
    public String visit(IntegerLiteral node, Void input) {
        return node.value.toString();
    }

    @Override
    public String visit(DoubleLiteral node, Void input) {
        return node.value.toString();
    }

    @Override
    public <X> String visit(Reference<X> node, Void input) {
        return node.name;
    }

    @Override
    public String visit(UnaryExpression node, Void input) {
        return node.operator + node.operand.accept(this, input);
    }

    @Override
    public String visit(NotExpression node, Void input) {
        return visit((UnaryExpression) node, input);
    }

    @Override
    public String visit(PlusExpression node, Void input) {
        return visit((UnaryExpression) node, input);
    }

    @Override
    public String visit(MinusExpression node, Void input) {
        return visit((UnaryExpression) node, input);
    }

    @Override
    public String visit(ParenExpression node, Void input) {
        return "(" + node.operand.accept(this, input) + ")";
    }

    @Override
    public String visit(BinaryExpression node, Void input) {
        return node.left.accept(this, input) + " " + node.operator + " " + node.right.accept(this, input);
    }

    @Override
    public String visit(Multiplication node, Void input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public String visit(Division node, Void input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public String visit(Modulus node, Void input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public String visit(Addition node, Void input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public String visit(Substraction node, Void input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public String visit(LessThan node, Void input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public String visit(LessThanOrEqual node, Void input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public String visit(GreaterThan node, Void input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public String visit(GreaterThanOrEqual node, Void input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public String visit(Equal node, Void input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public String visit(NotEqual node, Void input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public String visit(Conjunction node, Void input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public String visit(Disjunction node, Void input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public String visit(Implication node, Void input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public String visit(Equivalence node, Void input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public String visit(ExclusiveDisjunction node, Void input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public String visit(ConditionalExpression node, Void input) {
        return      node.condition.accept(this, input)
            + "?" + node.thenExpression.accept(this, input)
            + ":" + node.elseExpression.accept(this, input);
    }

    @Override
    public String visit(Statement node, Void input) {
        return "";
    }

    @Override
    public String visit(Skip node, Void input) {
        return "skip";
    }

    @Override
    public String visit(Assignment node, Void input) {
        return node.target.accept(this, input) + " = " + node.expression.accept(this, input);
    }

    @Override
    public String visit(IfStatement node, Void input) {
        return "if " + node.condition.accept(this, input) + " { " + node.thenStatement.accept(this, input) + " } else {" + node.elseStatement.accept(this, input) + "}";
    }

    @Override
    public String visit(Sequence node, Void input) {
        return node.left.accept(this, input) + "; " + node.right.accept(this, input);
    }

    @Override
    public String visit(AnonymousPiece node, Void input) {
        return "["+node.guard.accept(this, input) +"]/" + node.effect.accept(this, input);
    }

    @Override
    public String visit(NamedPiece node, Void input) {
        return node.name + "≜" + visit((AnonymousPiece) node, input);
    }

    @Override
    public String visit(VariableDeclaration node, Void input) {
        return node.name + " = " + node.initial.accept(this, input);
    }

    @Override
    public String visit(Soup node, Void input) {
        var vars = node.variables
                .stream()
                .map(vd -> vd.name + " = " + vd.initial.accept(this, input))
                .collect(Collectors.joining("; "));
        var pieces = node.pieces
                .stream()
                .map(p -> p.accept(this, input))
                .collect(Collectors.joining(";\n"));
        return vars + " " + pieces;
    }

    @Override
    public String visit(PrimedReference node, Void input) {
        return node.name + "'";
    }

    @Override
    public String visit(NamedPieceReference node, Void input) {
        return "p:" + node.name;
    }

    @Override
    public String visit(EnabledExpression node, Void input) {
        return "enabled (" + node.operand.accept(this, input) + ")";
    }

    @Override
    public String visit(InputReference node, Void input) {
        return "@(" + node.operand.accept(this, input) + ")";
    }
}
