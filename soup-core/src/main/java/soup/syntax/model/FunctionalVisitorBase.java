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

public class FunctionalVisitorBase<I, O> implements FunctionalVisitor<I, O> {
    @Override
    public O visit(SyntaxTreeElement node, I input) {
        return null;
    }

    @Override
    public O visit(Expression node, I input) {
        return visit((SyntaxTreeElement) node, input);
    }

    @Override
    public <X> O visit(Literal<X> node, I input) {
        return visit((Expression) node, input);
    }

    @Override
    public O visit(BooleanLiteral node, I input) {
        return visit((Literal<Boolean>) node, input);
    }

    @Override
    public O visit(IntegerLiteral node, I input) {
        return visit((Literal<Integer>) node, input);
    }

    @Override
    public O visit(DoubleLiteral node, I input) {
        return visit((Literal<Double>) node, input);
    }

    @Override
    public <X> O visit(Reference<X> node, I input) {
        return visit((Expression) node, input);
    }

    @Override
    public O visit(UnaryExpression node, I input) {
        return visit((Expression) node, input);
    }

    @Override
    public O visit(NotExpression node, I input) {
        return visit((UnaryExpression) node, input);
    }

    @Override
    public O visit(PlusExpression node, I input) {
        return visit((UnaryExpression) node, input);
    }

    @Override
    public O visit(MinusExpression node, I input) {
        return visit((UnaryExpression) node, input);
    }

    @Override
    public O visit(ParenExpression node, I input) {
        return visit((UnaryExpression) node, input);
    }

    @Override
    public O visit(BinaryExpression node, I input) {
        return visit((Expression) node, input);
    }

    @Override
    public O visit(Multiplication node, I input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public O visit(Division node, I input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public O visit(Modulus node, I input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public O visit(Addition node, I input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public O visit(Substraction node, I input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public O visit(LessThan node, I input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public O visit(LessThanOrEqual node, I input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public O visit(GreaterThan node, I input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public O visit(GreaterThanOrEqual node, I input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public O visit(Equal node, I input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public O visit(NotEqual node, I input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public O visit(Conjunction node, I input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public O visit(Disjunction node, I input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public O visit(Implication node, I input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public O visit(Equivalence node, I input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public O visit(ExclusiveDisjunction node, I input) {
        return visit((BinaryExpression) node, input);
    }

    @Override
    public O visit(ConditionalExpression node, I input) {
        return visit((Expression) node, input);
    }

    @Override
    public O visit(Statement node, I input) {
        return visit((SyntaxTreeElement) node, input);
    }

    @Override
    public O visit(Skip node, I input) {
        return visit((Statement) node, input);
    }

    @Override
    public O visit(Assignment node, I input) {
        return visit((Statement) node, input);
    }

    @Override
    public O visit(IfStatement node, I input) {
        return visit((Statement) node, input);
    }

    @Override
    public O visit(Sequence node, I input) {
        return visit((Statement) node, input);
    }

    @Override
    public O visit(AnonymousPiece node, I input) {
        return visit((SyntaxTreeElement) node, input);
    }

    @Override
    public O visit(NamedPiece node, I input) {
        return visit((AnonymousPiece) node, input);
    }

    @Override
    public O visit(VariableDeclaration node, I input) {
        return visit((SyntaxTreeElement) node, input);
    }

    @Override
    public O visit(Soup node, I input) {
        return visit((SyntaxTreeElement) node, input);
    }

    @Override
    public O visit(PrimedReference node, I input) {
        return visit((Expression) node, input);
    }

    @Override
    public O visit(NamedPieceReference node, I input) {
        return visit((Expression) node, input);
    }

    @Override
    public O visit(EnabledExpression node, I input) {
        return visit((Expression) node, input);
    }

    @Override
    public O visit(InputReference node, I input) {
        return visit((Expression) node, input);
    }
}
