package util;

import ctwedge.ctWedge.AndExpression;
import ctwedge.ctWedge.AtomicPredicate;
import ctwedge.ctWedge.EqualExpression;
import ctwedge.ctWedge.ImpliesExpression;
import ctwedge.ctWedge.ModMultDiv;
import ctwedge.ctWedge.NotExpression;
import ctwedge.ctWedge.OrExpression;
import ctwedge.ctWedge.PlusMinus;
import ctwedge.ctWedge.RelationalExpression;
import ctwedge.ctWedge.util.CtWedgeSwitch;

/**
 * This class provides a visitor for computing the complexity of a constraint
 * 
 * @author bombarda_andrea
 * 
 */
public class ConstraintComplexityExtractor extends CtWedgeSwitch<Integer> {

	/**
	 * Visit a not expression. Its complexity is 1 + that of the predicate
	 * 
	 * @param not the not expression
	 * @return the complexity
	 */
	@Override
	public Integer caseNotExpression(NotExpression not) {
		return 1 + doSwitch(not.getPredicate());
	}

	/**
	 * Visit an implies expression. Its complexity is 1 + those of the left and
	 * right expressions
	 * 
	 * @param ruleExpr the ImpliesExpression expression
	 * @return the complexity
	 */
	@Override
	public Integer caseImpliesExpression(ImpliesExpression ruleExpr) {
		return 1 + doSwitch(ruleExpr.getLeft()) + doSwitch(ruleExpr.getRight());
	}

	/**
	 * Visit an Or expression. Its complexity is 1 + those of the left and right
	 * expressions
	 * 
	 * @param orExpr the Or expression
	 * @return the complexity
	 */
	@Override
	public Integer caseOrExpression(OrExpression orExpr) {
		return 1 + doSwitch(orExpr.getLeft()) + doSwitch(orExpr.getRight());
	}

	/**
	 * Visit an And expression. Its complexity is 1 + those of the left and right
	 * expressions
	 * 
	 * @param andExpr the AndExpression expression
	 * @return the complexity
	 */
	@Override
	public Integer caseAndExpression(AndExpression andExpr) {
		return 1 + doSwitch(andExpr.getLeft()) + doSwitch(andExpr.getRight());
	}

	/**
	 * Visit an Equal expression. Its complexity is 0
	 * 
	 * @param object the EqualExpression expression
	 * @return the complexity
	 */
	@Override
	public Integer caseEqualExpression(EqualExpression object) {
		return 0;
	}

	/**
	 * Visit a Relational expression. Its complexity is 0
	 * 
	 * @param ineqExpr the Relational expression
	 * @return the complexity
	 */
	@Override
	public Integer caseRelationalExpression(RelationalExpression ineqExpr) {
		return 0;
	}

	/**
	 * Visit an CasePlusMinus expression. Its complexity is 0
	 * 
	 * @param pm the PlusMinus expression
	 * @return the complexity
	 */
	@Override
	public Integer casePlusMinus(PlusMinus pm) {
		return 0;
	}

	/**
	 * Visit a ModMultDiv expression. Its complexity is 0
	 * 
	 * @param not the ModMultDiv expression
	 * @return the complexity
	 */
	@Override
	public Integer caseModMultDiv(ModMultDiv md) {
		return 0;
	}

	/**
	 * Visit an atomic predicate expression. Its complexity is 1
	 * 
	 * @param atom the atomic predicate
	 * @return the complexity
	 */
	@Override
	public Integer caseAtomicPredicate(AtomicPredicate atom) {
		return 1;
	}

}
