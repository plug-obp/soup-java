open module language.soup.core {
    requires obp.sli.runtime;
    requires obp.algos;
    requires org.antlr.antlr4.runtime;
    exports soup.syntax;
    exports soup.syntax.model;
    exports soup.semantics.base;
    exports soup.semantics.dependent;
    exports soup.semantics.diagnosis;
    exports soup.syntax.model.declarations;
    exports soup.syntax.model.declarations.pieces;
    exports soup.syntax.model.dependent;
    exports soup.syntax.model.expressions;
    exports soup.syntax.model.expressions.literals;
    exports soup.syntax.model.expressions.unary;
    exports soup.syntax.model.expressions.binary;
    exports soup.syntax.model.expressions.binary.arithmetic;
    exports soup.syntax.model.expressions.binary.propositional;
    exports soup.syntax.model.expressions.binary.relational;
    exports soup.syntax.model.statements;

}