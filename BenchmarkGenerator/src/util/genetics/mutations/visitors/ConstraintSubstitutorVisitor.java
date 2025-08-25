package util.genetics.mutations.visitors;

import org.eclipse.emf.ecore.util.EcoreUtil;

import ctwedge.ctWedge.AndExpression;
import ctwedge.ctWedge.AtomicPredicate;
import ctwedge.ctWedge.Constraint;
import ctwedge.ctWedge.EqualExpression;
import ctwedge.ctWedge.Expression;
import ctwedge.ctWedge.ImpliesExpression;
import ctwedge.ctWedge.ModMultDiv;
import ctwedge.ctWedge.NotExpression;
import ctwedge.ctWedge.OrExpression;
import ctwedge.ctWedge.PlusMinus;
import ctwedge.ctWedge.RelationalExpression;
import ctwedge.ctWedge.impl.CtWedgeFactoryImpl;
import ctwedge.ctWedge.util.CtWedgeSwitch;

/**
 * This class returns a new constraint that has some substitution w.r.t. to the
 * visited one. Depending on the the class of the sourceExpr and that of the
 * destExpr, the new constraint will have some substitution in the type of each
 * sub-constraint
 * 
 * @author Andrea Bombarda
 */
public class ConstraintSubstitutorVisitor extends CtWedgeSwitch<Expression> {

	Constraint sourceExpr;
	Constraint destExpr;
	CtWedgeFactoryImpl factory;

	/**
	 * Builds a new ConstraintSubstitutorVisitor
	 * 
	 * @param sourceExpr the source type
	 * @param destExpr   the destination type
	 */
	public ConstraintSubstitutorVisitor(Constraint sourceExpr, Constraint destExpr) {
		this.sourceExpr = sourceExpr;
		this.destExpr = destExpr;
		this.factory = new CtWedgeFactoryImpl();
	}

	/**
	 * Visit the not expression
	 * 
	 * @param not the not expression
	 * @return the visited not expression
	 */
	@Override
	public Expression caseNotExpression(NotExpression not) {
		NotExpression c = factory.createNotExpression();
		c.setPredicate(doSwitch(not.getPredicate()));
		return c;
	}

	/**
	 * Visit the equal expression
	 * 
	 * @param not the equal expression
	 * @return the visited equal expression
	 */
	@Override
	public Expression caseEqualExpression(EqualExpression eq) {
		EqualExpression c = factory.createEqualExpression();
		c.setOp(eq.getOp());
		c.setRight(doSwitch(eq.getRight()));
		c.setLeft(doSwitch(eq.getLeft()));
		return c;
	}

	/**
	 * Visit the atomic predicate
	 * 
	 * @param not the atomic predicate
	 * @return the visited atomic predicate
	 */
	@Override
	public Expression caseAtomicPredicate(AtomicPredicate atom) {
		AtomicPredicate newAtom = EcoreUtil.copy(atom);
		return newAtom;
	}

	/**
	 * Visit the PlusMinus expression
	 * 
	 * @param not the PlusMinus expression
	 * @return the visited PlusMinus expression
	 */
	@Override
	public Expression casePlusMinus(PlusMinus pm) {
		PlusMinus c = factory.createPlusMinus();
		c.setOp(pm.getOp());
		c.setRight(doSwitch(pm.getRight()));
		c.setLeft(doSwitch(pm.getLeft()));
		return c;
	}

	/**
	 * Visit the ModMultDiv expression
	 * 
	 * @param not the ModMultDiv expression
	 * @return the visited ModMultDiv expression
	 */
	@Override
	public Expression caseModMultDiv(ModMultDiv md) {
		ModMultDiv c = factory.createModMultDiv();
		c.setOp(md.getOp());
		c.setRight(doSwitch(md.getRight()));
		c.setLeft(doSwitch(md.getLeft()));
		return c;
	}

	/**
	 * Visit the Relational expression
	 * 
	 * @param not the Relational expression
	 * @return the visited Relational expression
	 */
	@Override
	public Expression caseRelationalExpression(RelationalExpression ineqExpr) {
		RelationalExpression c = factory.createRelationalExpression();
		c.setOp(ineqExpr.getOp());
		c.setRight(doSwitch(ineqExpr.getRight()));
		c.setLeft(doSwitch(ineqExpr.getLeft()));
		return c;
	}

	/**
	 * Visit the And expression
	 * 
	 * @param not the And expression
	 * @return the visited And expression
	 */
	@Override
	public Expression caseAndExpression(AndExpression andExpr) {
		Expression c;
		if (sourceExpr instanceof AndExpression) {
			// Substitute
			c = (Expression) factory.create(destExpr.eClass());
			if (destExpr instanceof OrExpression) {
				((OrExpression) c).setRight(doSwitch(andExpr.getRight()));
				((OrExpression) c).setLeft(doSwitch(andExpr.getLeft()));
			} else {
				throw new RuntimeException("Class non implemented");
			}
		} else {
			// Do not substitute
			c = factory.createAndExpression();
			((AndExpression) c).setRight(doSwitch(andExpr.getRight()));
			((AndExpression) c).setLeft(doSwitch(andExpr.getLeft()));
		}

		return c;
	}

	/**
	 * Visit the Or expression
	 * 
	 * @param not the Or expression
	 * @return the visited Or expression
	 */
	@Override
	public Expression caseOrExpression(OrExpression orExpr) {
		Expression c;
		if (sourceExpr instanceof OrExpression) {
			// Substitute
			c = (Expression) factory.create(destExpr.eClass());
			if (destExpr instanceof AndExpression) {
				((AndExpression) c).setRight(doSwitch(orExpr.getRight()));
				((AndExpression) c).setLeft(doSwitch(orExpr.getLeft()));
			} else {
				throw new RuntimeException("Class non implemented");
			}
		} else {
			// Do not substitute
			c = factory.createOrExpression();
			((OrExpression) c).setRight(doSwitch(orExpr.getRight()));
			((OrExpression) c).setLeft(doSwitch(orExpr.getLeft()));
		}

		return c;
	}

	/**
	 * Visit the Implies expression
	 * 
	 * @param not the Implies expression
	 * @return the visited Implies expression
	 */
	@Override
	public Expression caseImpliesExpression(ImpliesExpression ruleExpr) {
		ImpliesExpression c = factory.createImpliesExpression();
		if (sourceExpr instanceof ImpliesExpression) {
			if (destExpr instanceof ImpliesExpression)
				c.setOp(((ImpliesExpression) destExpr).getOp());
			else
				throw new RuntimeException("Class non implemented");
		} else {
			c.setOp(ruleExpr.getOp());
		}
		c.setRight(doSwitch(ruleExpr.getRight()));
		c.setLeft(doSwitch(ruleExpr.getLeft()));
		return c;
	}

}
