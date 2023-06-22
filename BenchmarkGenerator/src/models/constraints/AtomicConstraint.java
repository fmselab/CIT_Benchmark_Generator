package models.constraints;

/**
 * An atomic constraint class, with a single expression in
 * 
 * @author andrea
 *
 */
public class AtomicConstraint extends Constraint {

	/**
	 * The expression
	 */
	String expression;

	/**
	 * Build a new empty atomic constraint, with empty expression
	 */
	public AtomicConstraint() {
		this.expression = "";
	}

	/**
	 * Build a new atomic constraint
	 * 
	 * @param expression the expression
	 */
	public AtomicConstraint(String expression) {
		this.expression = expression;
	}

	/**
	 * Get the expression of the constraint
	 * 
	 * @return the expression
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * Set the expression of the constraint
	 * 
	 * @param expression the expression
	 */
	public void setExpression(String expression) {
		this.expression = expression;
	}

	/**
	 * Get the atomic constraint, simply by accessing its expression
	 * 
	 * @return the expression
	 */
	public String toString() {
		return expression;
	}
}
