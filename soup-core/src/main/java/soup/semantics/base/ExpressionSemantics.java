package soup.semantics.base;

import soup.syntax.Reader;
import soup.syntax.model.FunctionalVisitorBase;
import soup.syntax.model.declarations.Soup;
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

import java.util.Map;

public class ExpressionSemantics extends FunctionalVisitorBase<Environment, Object> {

    public static Object evaluate(Expression expression, Map<String, Object> environment) {
        var runtimeEnvironment = new Environment(expression, environment);
        return new ExpressionSemantics().evaluate(expression, runtimeEnvironment);
    }

    public static Object evaluate(String expression, Map<String, Object> environment) throws Exception {
        var model = Reader.readExpression(expression);
        return evaluate(model, environment);
    }

    public Object evaluate(Expression expression, Environment environment) {
        return expression.accept(this, environment);
    }

    @Override
    public Object visit(BooleanLiteral node, Environment environment) {
        return node.value;
    }

    @Override
    public Object visit(IntegerLiteral node, Environment environment) {
        return node.value;
    }

    @Override
    public Object visit(DoubleLiteral node, Environment environment) {
        return node.value;
    }

    @Override
    public Object visit(Reference<?> node, Environment environment) {
        return environment.lookup(node.name);
    }

    public boolean ensureBoolean(String operator, Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        throw new UnsupportedOperationException(operator + " expected a boolean but got " + value);
    }

    int ensureInteger(String operator, Object value) {
        if (value instanceof Integer) {
            return (int) value;
        }
        throw new UnsupportedOperationException(operator + " expected an integer but got " + value);
    }

    double ensureDouble(String operator, Object value) {
        if (value instanceof Double) {
            return (double) value;
        }
        if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        }
        throw new UnsupportedOperationException(operator + " expected a double but got " + value);
    }

    @Override
    public Object visit(NotExpression node, Environment environment) {
        var operand = node.operand.accept(this, environment);
        var booleanValue = ensureBoolean(node.operator, operand);
        return !booleanValue;
    }

    @Override
    public Object visit(MinusExpression node, Environment environment) {
        var operand = node.operand.accept(this, environment);
        try {
            var intValue = ensureInteger(node.operator, operand);
            return - intValue;
        } catch (UnsupportedOperationException e) {
            var doubleValue = ensureDouble(node.operator, operand);
            return -doubleValue;
        }
    }

    @Override
    public Object visit(PlusExpression node, Environment environment) {
        var operand = node.operand.accept(this, environment);
        try {
            return ensureInteger(node.operator, operand);
        } catch (UnsupportedOperationException e) {
            return ensureDouble(node.operator, operand);
        }
    }

    @Override
    public Object visit(ParenExpression node, Environment environment) {
        return node.operand.accept(this, environment);
    }

    @Override
    public Object visit(Multiplication node, Environment environment) {
        var left = node.left.accept(this, environment);
        var right = node.right.accept(this, environment);

        try {
            var leftV = ensureInteger(node.operator, left);
            var rightV = ensureInteger(node.operator, right);
            return leftV * rightV;
        } catch (UnsupportedOperationException e) {
            var leftV = ensureDouble(node.operator, left);
            var rightV = ensureDouble(node.operator, right);
            return leftV * rightV;
        }
    }

    @Override
    public Object visit(Division node, Environment environment) {
        var left = node.left.accept(this, environment);
        var right = node.right.accept(this, environment);

        try {
            var leftV = ensureInteger(node.operator, left);
            var rightV = ensureInteger(node.operator, right);
            return leftV / rightV;
        } catch (UnsupportedOperationException e) {
            var leftV = ensureDouble(node.operator, left);
            var rightV = ensureDouble(node.operator, right);
            return leftV / rightV;
        }
    }

    @Override
    public Object visit(Modulus node, Environment environment) {
        var left = node.left.accept(this, environment);
        var right = node.right.accept(this, environment);

        try {
            var leftV = ensureInteger(node.operator, left);
            var rightV = ensureInteger(node.operator, right);
            return leftV % rightV;
        } catch (UnsupportedOperationException e) {
            var leftV = ensureDouble(node.operator, left);
            var rightV = ensureDouble(node.operator, right);
            return leftV % rightV;
        }
    }

    @Override
    public Object visit(Addition node, Environment environment) {
        var left = node.left.accept(this, environment);
        var right = node.right.accept(this, environment);

        try {
            var leftV = ensureInteger(node.operator, left);
            var rightV = ensureInteger(node.operator, right);
            return leftV + rightV;
        } catch (UnsupportedOperationException e) {
            var leftV = ensureDouble(node.operator, left);
            var rightV = ensureDouble(node.operator, right);
            return leftV + rightV;
        }
    }

    @Override
    public Object visit(Substraction node, Environment environment) {
        var left = node.left.accept(this, environment);
        var right = node.right.accept(this, environment);

        try {
            var leftV = ensureInteger(node.operator, left);
            var rightV = ensureInteger(node.operator, right);
            return leftV - rightV;
        } catch (UnsupportedOperationException e) {
            var leftV = ensureDouble(node.operator, left);
            var rightV = ensureDouble(node.operator, right);
            return leftV - rightV;
        }
    }

    @Override
    public Object visit(LessThan node, Environment environment) {
        var left = node.left.accept(this, environment);
        var right = node.right.accept(this, environment);

        try {
            var leftV = ensureInteger(node.operator, left);
            var rightV = ensureInteger(node.operator, right);
            return leftV < rightV;
        } catch (UnsupportedOperationException e) {
            var leftV = ensureDouble(node.operator, left);
            var rightV = ensureDouble(node.operator, right);
            return leftV < rightV;
        }
    }

    @Override
    public Object visit(LessThanOrEqual node, Environment environment) {
        var left = node.left.accept(this, environment);
        var right = node.right.accept(this, environment);

        try {
            var leftV = ensureInteger(node.operator, left);
            var rightV = ensureInteger(node.operator, right);
            return leftV <= rightV;
        } catch (UnsupportedOperationException e) {
            var leftV = ensureDouble(node.operator, left);
            var rightV = ensureDouble(node.operator, right);
            return leftV <= rightV;
        }
    }

    @Override
    public Object visit(GreaterThan node, Environment environment) {
        var left = node.left.accept(this, environment);
        var right = node.right.accept(this, environment);

        try {
            var leftV = ensureInteger(node.operator, left);
            var rightV = ensureInteger(node.operator, right);
            return leftV > rightV;
        } catch (UnsupportedOperationException e) {
            var leftV = ensureDouble(node.operator, left);
            var rightV = ensureDouble(node.operator, right);
            return leftV > rightV;
        }
    }

    @Override
    public Object visit(GreaterThanOrEqual node, Environment environment) {
        var left = node.left.accept(this, environment);
        var right = node.right.accept(this, environment);

        try {
            var leftV = ensureInteger(node.operator, left);
            var rightV = ensureInteger(node.operator, right);
            return leftV >= rightV;
        } catch (UnsupportedOperationException e) {
            var leftV = ensureDouble(node.operator, left);
            var rightV = ensureDouble(node.operator, right);
            return leftV >= rightV;
        }
    }

    @Override
    public Object visit(Equal node, Environment environment) {
        var left = node.left.accept(this, environment);
        var right = node.right.accept(this, environment);
        return left.equals(right);
    }

    @Override
    public Object visit(NotEqual node, Environment environment) {
        var left = node.left.accept(this, environment);
        var right = node.right.accept(this, environment);
        return !left.equals(right);
    }

    @Override
    public Object visit(Conjunction node, Environment environment) {
        var left = node.left.accept(this, environment);
        var right = node.right.accept(this, environment);
        var leftV = ensureBoolean(node.operator, left);
        var rightV = ensureBoolean(node.operator, right);
        return leftV && rightV;
    }

    @Override
    public Object visit(Disjunction node, Environment environment) {
        var left = node.left.accept(this, environment);
        var right = node.right.accept(this, environment);
        var leftV = ensureBoolean(node.operator, left);
        var rightV = ensureBoolean(node.operator, right);
        return leftV || rightV;
    }

    @Override
    public Object visit(Implication node, Environment environment) {
        var left = node.left.accept(this, environment);
        var right = node.right.accept(this, environment);
        var leftV = ensureBoolean(node.operator, left);
        var rightV = ensureBoolean(node.operator, right);
        return !leftV || rightV;
    }

    @Override
    public Object visit(Equivalence node, Environment environment) {
        var left = node.left.accept(this, environment);
        var right = node.right.accept(this, environment);
        var leftV = ensureBoolean(node.operator, left);
        var rightV = ensureBoolean(node.operator, right);
        return (leftV && rightV) || (!leftV && !rightV);
    }

    @Override
    public Object visit(ExclusiveDisjunction node, Environment environment) {
        var left = node.left.accept(this, environment);
        var right = node.right.accept(this, environment);
        var leftV = ensureBoolean(node.operator, left);
        var rightV = ensureBoolean(node.operator, right);
        return (leftV || rightV) && !(leftV && rightV);
    }

    @Override
    public Object visit(ConditionalExpression node, Environment environment) {
        var cond = node.condition.accept(this, environment);
        var condV = ensureBoolean("?:", cond);
        if (condV) {
            return node.thenExpression.accept(this, environment);
        }
        return node.elseExpression.accept(this, environment);
    }
}
