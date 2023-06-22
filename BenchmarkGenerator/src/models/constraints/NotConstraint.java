package models.constraints;

/**
 * A lightweight implementation of a NOT constraint
 * 
 * It is composed of an expression, and a NOT operator
 * 
 * @author andrea
 *
 */
public class NotConstraint extends AtomicConstraint {

	private static final String NOT = "not";

	/**
	 * Builds a new empty NOT constraint
	 */
	public NotConstraint() {
		this.expression = "";
		this.symbol = NOT;
	}

	/**
	 * Builds a new NOT constraint
	 * 
	 * @param expression the expression to be negated
	 */
	public NotConstraint(String expression) {
		this.expression = expression;
		this.symbol = NOT;
	}

	/**
	 * Get the expression
	 * 
	 * @return the expression
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * Sets the expression to be negated
	 * 
	 * @param expression the expression
	 */
	public void setExpression(String expression) {
		this.expression = expression;
	}

	/**
	 * Get the constraint in CTWEDGE format
	 * 
	 * @return a string representing the constraint in CTWEDGE format
	 */
	public String toString() {
		return "(" + NOT + " (" + expression + "))";
	}
}
