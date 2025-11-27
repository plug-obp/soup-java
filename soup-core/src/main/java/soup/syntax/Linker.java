package soup.syntax;

import soup.syntax.model.FunctionalVisitorBase;
import soup.syntax.model.declarations.Soup;
import soup.syntax.model.declarations.pieces.AnonymousPiece;
import soup.syntax.model.declarations.pieces.NamedPiece;
import soup.syntax.model.dependent.NamedPieceReference;
import soup.syntax.model.dependent.PrimedReference;
import soup.syntax.model.expressions.ConditionalExpression;
import soup.syntax.model.expressions.Reference;
import soup.syntax.model.expressions.binary.BinaryExpression;
import soup.syntax.model.expressions.unary.UnaryExpression;
import soup.syntax.model.statements.Assignment;
import soup.syntax.model.statements.IfStatement;
import soup.syntax.model.statements.Sequence;

public class Linker extends FunctionalVisitorBase<Environment, Void> {
    @Override
    @SuppressWarnings({"rawtypes"})
    public Void visit(Reference node, Environment input) {
        if (node.declaration != null) return null;
        node.declaration = input.variableScope.get(node.name);
        if (node.declaration == null)
            throw new RuntimeException("variable '" + node.name + "' not found");
        return null;
    }

    @Override
    public Void visit(PrimedReference node, Environment input) {
        if (node.declaration != null) return null;
        if (input.variableScope.get(node.name) == null)
            throw new RuntimeException("variable '" + node.name + "' not found");
        return null;
    }

    @Override
    public Void visit(NamedPieceReference node, Environment input) {
        if (node.declaration != null) return null;
        node.declaration = input.pieceScope.get(node.name);
        if (node.declaration == null)
            throw new RuntimeException("piece '" + node.name + "' not found");
        return null;
    }
    @Override
    public Void visit(Soup node, Environment input) {
        var environment = input;
        if (environment == null) {
            environment = new Environment();
        }
        for (var variable : node.variables) {
            variable.initial.accept(this, environment);
            environment.variableScope.put(variable.name, variable);
        }
        for (var piece : node.pieces) {
            piece.accept(this, environment);
        }
        return null;
    }

    @Override
    public Void visit(AnonymousPiece node, Environment input) {
        node.guard.accept(this, input);
        node.effect.accept(this, input);
        return null;
    }

    @Override
    public Void visit(NamedPiece node, Environment input) {
        input.pieceScope.put(node.name, node);
        visit((AnonymousPiece) node, input);
        return null;
    }

    @Override
    public Void visit(UnaryExpression node, Environment input) {
        node.operand.accept(this, input);
        return null;
    }

    @Override
    public Void visit(BinaryExpression node, Environment input) {
        node.left.accept(this, input);
        node.right.accept(this, input);
        return null;
    }


    @Override
    public Void visit(ConditionalExpression node, Environment input) {
        node.condition.accept(this, input);
        node.thenExpression.accept(this, input);
        node.elseExpression.accept(this, input);
        return null;
    }

    @Override
    public Void visit(Assignment node, Environment input) {
        node.target.accept(this, input);
        node.expression.accept(this, input);
        return null;
    }

    @Override
    public Void visit(IfStatement node, Environment input) {
        node.condition.accept(this, input);
        node.thenStatement.accept(this, input);
        node.elseStatement.accept(this, input);
        return null;
    }

    @Override
    public Void visit(Sequence node, Environment input) {
        node.left.accept(this, input);
        node.right.accept(this, input);
        return null;
    }
}
