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
import soup.syntax.model.expressions.literals.BooleanLiteral;
import soup.syntax.model.expressions.literals.DoubleLiteral;
import soup.syntax.model.expressions.literals.IntegerLiteral;
import soup.syntax.model.expressions.unary.MinusExpression;
import soup.syntax.model.expressions.unary.NotExpression;
import soup.syntax.model.expressions.unary.PlusExpression;

public class Antrl4ToSyntax extends SoupBaseListener {
    ParseTreeProperty<Object> map = new ParseTreeProperty<>();

    public <T extends SyntaxTreeElement> T get(ParseTree tree, Class<T> type) {
        var node = map.get(tree);
        if (node == null) { return null; }
        if (type.isAssignableFrom(node.getClass())) {
            return type.cast(node);
        }
        return null;
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
            map.put(ctx, BooleanLiteral.TRUE);
            return;
        }
        if (ctx.FALSE() != null) {
            map.put(ctx, BooleanLiteral.FALSE);
            return;
        }
        if (ctx.NUMBER() != null) {
            var numberText = ctx.NUMBER().getText();
            //if dot then double
            if (numberText.indexOf('.') >= 0) {
                map.put(ctx, new DoubleLiteral(Double.parseDouble(numberText), getPosition(ctx)));
                return;
            }
            //else integer
            map.put(ctx, new IntegerLiteral(Integer.parseInt(numberText), getPosition(ctx)));
        }
    }

    @Override
    public void exitLiteralExp(SoupParser.LiteralExpContext ctx) {
        map.put(ctx, map.get(ctx.literal()));
    }

    @Override
    public void exitReferenceExp(SoupParser.ReferenceExpContext ctx) {
        map.put(ctx, new Reference<VariableDeclaration>(ctx.getText(), getPosition(ctx)));
    }

    @Override
    public void exitParenExp(SoupParser.ParenExpContext ctx) {
        map.put(ctx, map.get(ctx.expression()));
    }

    @Override
    public void exitUnaryExp(SoupParser.UnaryExpContext ctx) {
        var operator = ctx.operator;
        var operand = get(ctx.expression(), Expression.class);
        switch (operator.getType()) {
            case SoupParser.NOT:
                map.put(ctx, new NotExpression(operator.getText(), operand, getPosition(ctx)));
                return;
            case SoupParser.MINUS:
                map.put(ctx, new MinusExpression(operand, getPosition(ctx)));
                return;
            case SoupParser.PLUS:
                map.put(ctx, new PlusExpression(operand, getPosition(ctx)));
        }
    }
}
