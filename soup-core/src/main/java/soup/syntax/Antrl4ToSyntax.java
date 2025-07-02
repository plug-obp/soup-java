package soup.syntax;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import soup.parser.SoupBaseListener;
import soup.parser.SoupParser;
import soup.syntax.model.Cursor;
import soup.syntax.model.Position;
import soup.syntax.model.SyntaxTreeElement;
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
import soup.syntax.model.expressions.binary.arithmetic.*;
import soup.syntax.model.expressions.binary.propositional.*;
import soup.syntax.model.expressions.binary.relational.*;
import soup.syntax.model.expressions.literals.BooleanLiteral;
import soup.syntax.model.expressions.literals.DoubleLiteral;
import soup.syntax.model.expressions.literals.IntegerLiteral;
import soup.syntax.model.expressions.unary.MinusExpression;
import soup.syntax.model.expressions.unary.NotExpression;
import soup.syntax.model.expressions.unary.ParenExpression;
import soup.syntax.model.expressions.unary.PlusExpression;
import soup.syntax.model.statements.*;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;

public class Antrl4ToSyntax extends SoupBaseListener {
    ParseTreeProperty<Object> model = new ParseTreeProperty<>();
    IdentityHashMap<SyntaxTreeElement, Position> positions = new IdentityHashMap<>();

    public <T extends SyntaxTreeElement> T get(ParseTree tree, Class<T> type) {
        var node = model.get(tree);
        if (node == null) { return null; }
        if (type.isAssignableFrom(node.getClass())) {
            return type.cast(node);
        }
        return null;
    }

    public IdentityHashMap<SyntaxTreeElement, Position> getPositions() {
        return positions;
    }

    public Position getPosition(SyntaxTreeElement modelElement) {
        var position = positions.get(modelElement);
        if (position == null) {
            return Position.ZERO;
        }
        return position;
    }

    Position getPosition(ParserRuleContext ctx) {
        return new Position(
                    new Cursor(ctx.start.getLine(), ctx.start.getStartIndex()),
                    new Cursor(ctx.stop.getLine(), ctx.stop.getStopIndex())
                );
    }

    @Override
    public void exitLiteral(SoupParser.LiteralContext ctx) {
        if (ctx.TRUE() != null) {
            model.put(ctx, BooleanLiteral.TRUE);
            positions.put(BooleanLiteral.TRUE, Position.ZERO);
            return;
        }
        if (ctx.FALSE() != null) {
            model.put(ctx, BooleanLiteral.FALSE);
            positions.put(BooleanLiteral.FALSE, Position.ZERO);
            return;
        }
        if (ctx.NUMBER() != null) {
            var numberText = ctx.NUMBER().getText();
            //if dot then double
            if (numberText.indexOf('.') >= 0) {
                var node = new DoubleLiteral(Double.parseDouble(numberText), getPosition(ctx));
                model.put(ctx, node);
                positions.put(node, getPosition(ctx));
                return;
            }
            //else integer
            var node = new IntegerLiteral(Integer.parseInt(numberText), getPosition(ctx));
            model.put(ctx, node);
            positions.put(node, getPosition(ctx));
        }
    }

    @Override
    public void exitLiteralExp(SoupParser.LiteralExpContext ctx) {
        model.put(ctx, model.get(ctx.literal()));
    }

    @Override
    public void exitReferenceExp(SoupParser.ReferenceExpContext ctx) {
        var node = new Reference<VariableDeclaration>(ctx.getText(), getPosition(ctx));
        model.put(ctx, node);
        positions.put(node, getPosition(ctx));
    }

    @Override
    public void exitParenExp(SoupParser.ParenExpContext ctx) {
        var node = new ParenExpression(get(ctx.expression(), Expression.class), getPosition(ctx));
        model.put(ctx, node);
        positions.put(node, getPosition(ctx));
    }

    @Override
    public void exitUnaryExp(SoupParser.UnaryExpContext ctx) {
        var operator = ctx.operator;
        var operand = get(ctx.expression(), Expression.class);
        Expression node = switch (operator.getType()) {
            case SoupParser.NOT -> new NotExpression(operator.getText(), operand, getPosition(ctx));
            case SoupParser.MINUS -> new MinusExpression(operand, getPosition(ctx));
            case SoupParser.PLUS -> new PlusExpression(operand, getPosition(ctx));
            default -> null;
        };
        model.put(ctx, node);
        positions.put(node, getPosition(ctx));
    }

    @Override
    public void exitBinaryExpression(SoupParser.BinaryExpressionContext ctx) {
        var operator = ctx.operator;
        var left = get(ctx.expression(0), Expression.class);
        var right = get(ctx.expression(1), Expression.class);
        Expression node = switch (operator.getType()) {
            case SoupParser.MULT -> new Multiplication(left, right, getPosition(ctx));
            case SoupParser.DIV -> new Division(left, right, getPosition(ctx));
            case SoupParser.MOD -> new Modulus(left, right, getPosition(ctx));
            case SoupParser.PLUS -> new Addition(left, right, getPosition(ctx));
            case SoupParser.MINUS -> new Substraction(left, right, getPosition(ctx));
            case SoupParser.LT -> new LessThan(ctx.operator.getText(), left, right, getPosition(ctx));
            case SoupParser.LE -> new LessThanOrEqual(ctx.operator.getText(), left, right, getPosition(ctx));
            case SoupParser.GT -> new GreaterThan(ctx.operator.getText(), left, right, getPosition(ctx));
            case SoupParser.GE -> new GreaterThanOrEqual(ctx.operator.getText(), left, right, getPosition(ctx));
            case SoupParser.BEQ -> new Equal(ctx.operator.getText(), left, right, getPosition(ctx));
            case SoupParser.NEQ -> new NotEqual(ctx.operator.getText(), left, right, getPosition(ctx));
            case SoupParser.AND -> new Conjunction(ctx.operator.getText(), left, right, getPosition(ctx));
            case SoupParser.OR -> new Disjunction(ctx.operator.getText(), left, right, getPosition(ctx));
            case SoupParser.XOR -> new ExclusiveDisjunction(ctx.operator.getText(), left, right, getPosition(ctx));
            case SoupParser.IMPLICATION -> new Implication(ctx.operator.getText(), left, right, getPosition(ctx));
            case SoupParser.EQUIVALENCE -> new Equivalence(ctx.operator.getText(), left, right, getPosition(ctx));
            default -> null;
        };
        model.put(ctx, node);
        positions.put(node, getPosition(ctx));
    }

    @Override
    public void exitConditionalExp(SoupParser.ConditionalExpContext ctx) {
        var condition = get(ctx.expression(0), Expression.class);
        var thenExpression = get(ctx.expression(1), Expression.class);
        var elseExpression = get(ctx.expression(2), Expression.class);
        var node = new ConditionalExpression(condition, thenExpression, elseExpression, getPosition(ctx));
        model.put(ctx, node);
        positions.put(node, getPosition(ctx));
    }

    @Override
    public void exitSkipStatement(SoupParser.SkipStatementContext ctx) {
        model.put(ctx, Skip.INSTANCE);
        positions.put(Skip.INSTANCE, Position.ZERO);
    }

    @Override
    public void exitAssign(SoupParser.AssignContext ctx) {
        var target = new Reference<VariableDeclaration>(ctx.IDENTIFIER().getText(), getPosition(ctx));
        var expression = get(ctx.expression(), Expression.class);
        var node = new Assignment(target, expression, getPosition(ctx));
        model.put(ctx, node);
        positions.put(node, getPosition(ctx));
    }

    @Override
    public void exitAssignStatement(SoupParser.AssignStatementContext ctx) {
        model.put(ctx, get(ctx.assign(), Statement.class));
    }

    @Override
    public void exitIfStatement(SoupParser.IfStatementContext ctx) {
        var condition = get(ctx.expression(), Expression.class);
        var thenStmt = get(ctx.statement(0), Statement.class);
        var elseStmt = get(ctx.statement(1), Statement.class);
        var node = elseStmt == null ?
                  new IfStatement(condition, thenStmt, Skip.INSTANCE, getPosition(ctx))
                : new IfStatement(condition, thenStmt, elseStmt, getPosition(ctx));
        model.put(ctx, node);
        positions.put(node, getPosition(ctx));
    }

    @Override
    public void exitSequenceStatement(SoupParser.SequenceStatementContext ctx) {
        var left = get(ctx.statement(0), Statement.class);
        var right = get(ctx.statement(1), Statement.class);
        var node = new Sequence(left, right, getPosition(ctx));
        model.put(ctx, node);
        positions.put(node, getPosition(ctx));
    }

    @Override
    public void exitAnonymousPiece(SoupParser.AnonymousPieceContext ctx) {
        var guard = get(ctx.guard(), Expression.class);
        var effect = get(ctx.effect(), Statement.class);
        var node = new AnonymousPiece(
                guard != null ? guard : BooleanLiteral.TRUE,
                effect,
                getPosition(ctx));
        model.put(ctx, node);
        positions.put(node, getPosition(ctx));
    }

    @Override
    public void exitGuard(SoupParser.GuardContext ctx) {
        model.put(ctx, get(ctx.expression(), Expression.class));
    }

    @Override
    public void exitEffect(SoupParser.EffectContext ctx) {
        model.put(ctx, get(ctx.statement(), Statement.class));
    }

    @Override
    public void exitNamedPiece(SoupParser.NamedPieceContext ctx) {
        var name = ctx.IDENTIFIER().getText();
        var guard = get(ctx.guard(), Expression.class);
        var effect = get(ctx.effect(), Statement.class);
        var node = new NamedPiece(
                name,
                guard != null ? guard :BooleanLiteral.TRUE,
                effect != null ? effect : Skip.INSTANCE,
                getPosition(ctx)
        );
        model.put(ctx, node);
        positions.put(node, getPosition(ctx));
    }

    @Override
    public void exitVariables(SoupParser.VariablesContext ctx) {
        var variables = ctx.assign().stream().map(assignCtx -> {
            var assign = get(assignCtx, Assignment.class);
            var node = new VariableDeclaration(assign.target.name, assign.expression, getPosition(assignCtx));
            positions.put(node, getPosition(ctx));
            return node;
        }).toList();
        model.put(ctx, variables);
    }

    @Override
    public void exitSoup(SoupParser.SoupContext ctx) {
        var variables = (List<VariableDeclaration>)model.get(ctx.variables());
        var pieces = ctx.piece()
                .stream()
                .map(pieceContext -> get(pieceContext, AnonymousPiece.class))
                .toList();
        var node = new Soup(variables == null ? Collections.emptyList() : variables, pieces, getPosition(ctx));
        model.put(ctx, node);
        positions.put(node, getPosition(ctx));
    }

    @Override
    public void exitInputReferenceExp(SoupParser.InputReferenceExpContext ctx) {
        var expression = get(ctx.expression(), Expression.class);
        var node = new InputReference(expression, getPosition(ctx));
        model.put(ctx, node);
        positions.put(node, getPosition(ctx));
    }

    @Override
    public void exitPrimedReferenceExp(SoupParser.PrimedReferenceExpContext ctx) {
        var name = ctx.IDENTIFIER().getText();
        var node = new PrimedReference(name, getPosition(ctx));
        model.put(ctx, node);
        positions.put(node, getPosition(ctx));
    }

    @Override
    public void exitNamedPieceReferenceExp(SoupParser.NamedPieceReferenceExpContext ctx) {
        var name = ctx.IDENTIFIER().getText();
        var node = new NamedPieceReference(name, getPosition(ctx));
        model.put(ctx, node);
        positions.put(node, getPosition(ctx));
    }

    @Override
    public void exitEnabledExp(SoupParser.EnabledExpContext ctx) {
        var expression = get(ctx.expression(), Expression.class);
        var node = new EnabledExpression(expression, getPosition(ctx));
        model.put(ctx, node);
        positions.put(node, getPosition(ctx));
    }
}
