package soup.syntax;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import soup.parser.SoupBaseListener;
import soup.parser.SoupParser;
import soup.syntax.model.Cursor;
import soup.syntax.model.Position;
import soup.syntax.model.SyntaxTreeElement;
import soup.syntax.model.declarations.VariableDeclaration;
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

import java.util.IdentityHashMap;

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
}
