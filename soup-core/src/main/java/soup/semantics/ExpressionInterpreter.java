package soup.semantics;

import soup.syntax.model.FunctionalVisitorBase;
import soup.syntax.model.expressions.ConditionalExpression;
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

public class ExpressionInterpreter extends FunctionalVisitorBase<RuntimeEnvironment, Object> {

    @Override
    public Object visit(BooleanLiteral node, RuntimeEnvironment environment) {
        return node.value;
    }

    @Override
    public Object visit(IntegerLiteral node, RuntimeEnvironment environment) {
        return node.value;
    }

    @Override
    public Object visit(DoubleLiteral node, RuntimeEnvironment environment) {
        return node.value;
    }

    @Override
    public Object visit(Reference node, RuntimeEnvironment environment) {
        return environment.lookup(node.name);
    }

    boolean ensureBoolean(String operator, Object value) {
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
        throw new UnsupportedOperationException(operator + " expected a double but got " + value);
    }

    @Override
    public Object visit(NotExpression node, RuntimeEnvironment environment) {
        var operand = node.operand.accept(this, environment);
        var booleanValue = ensureBoolean(node.operator, operand);
        return !booleanValue;
    }

    @Override
    public Object visit(MinusExpression node, RuntimeEnvironment environment) {
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
    public Object visit(PlusExpression node, RuntimeEnvironment environment) {
        var operand = node.operand.accept(this, environment);
        try {
            return ensureInteger(node.operator, operand);
        } catch (UnsupportedOperationException e) {
            return ensureDouble(node.operator, operand);
        }
    }

    @Override
    public Object visit(ParenExpression node, RuntimeEnvironment environment) {
        return node.operand.accept(this, environment);
    }

    @Override
    public Object visit(Multiplication node, RuntimeEnvironment environment) {
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
    public Object visit(Division node, RuntimeEnvironment environment) {
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
    public Object visit(Modulus node, RuntimeEnvironment environment) {
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
    public Object visit(Addition node, RuntimeEnvironment environment) {
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
    public Object visit(Substraction node, RuntimeEnvironment environment) {
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
    public Object visit(LessThan node, RuntimeEnvironment environment) {
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
    public Object visit(LessThanOrEqual node, RuntimeEnvironment environment) {
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
    public Object visit(GreaterThan node, RuntimeEnvironment environment) {
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
    public Object visit(GreaterThanOrEqual node, RuntimeEnvironment environment) {
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
    public Object visit(Equal node, RuntimeEnvironment environment) {
        var left = node.left.accept(this, environment);
        var right = node.right.accept(this, environment);
        return left.equals(right);
    }

    @Override
    public Object visit(NotEqual node, RuntimeEnvironment environment) {
        var left = node.left.accept(this, environment);
        var right = node.right.accept(this, environment);
        return !left.equals(right);
    }

    @Override
    public Object visit(Conjunction node, RuntimeEnvironment environment) {
        var left = node.left.accept(this, environment);
        var right = node.right.accept(this, environment);
        var leftV = ensureBoolean(node.operator, left);
        var rightV = ensureBoolean(node.operator, right);
        return leftV && rightV;
    }

    @Override
    public Object visit(Disjunction node, RuntimeEnvironment environment) {
        var left = node.left.accept(this, environment);
        var right = node.right.accept(this, environment);
        var leftV = ensureBoolean(node.operator, left);
        var rightV = ensureBoolean(node.operator, right);
        return leftV || rightV;
    }

    @Override
    public Object visit(Implication node, RuntimeEnvironment environment) {
        var left = node.left.accept(this, environment);
        var right = node.right.accept(this, environment);
        var leftV = ensureBoolean(node.operator, left);
        var rightV = ensureBoolean(node.operator, right);
        return !leftV || rightV;
    }

    @Override
    public Object visit(Equivalence node, RuntimeEnvironment environment) {
        var left = node.left.accept(this, environment);
        var right = node.right.accept(this, environment);
        var leftV = ensureBoolean(node.operator, left);
        var rightV = ensureBoolean(node.operator, right);
        return (leftV && rightV) || (!leftV && !rightV);
    }

    @Override
    public Object visit(ExclusiveDisjunction node, RuntimeEnvironment environment) {
        var left = node.left.accept(this, environment);
        var right = node.right.accept(this, environment);
        var leftV = ensureBoolean(node.operator, left);
        var rightV = ensureBoolean(node.operator, right);
        return (leftV || rightV) && !(leftV && rightV);
    }

    @Override
    public Object visit(ConditionalExpression node, RuntimeEnvironment environment) {
        var cond = node.condition.accept(this, environment);
        var condV = ensureBoolean("?:", cond);
        if (condV) {
            return node.thenExpression.accept(this, environment);
        }
        return node.elseExpression.accept(this, environment);
    }
}
