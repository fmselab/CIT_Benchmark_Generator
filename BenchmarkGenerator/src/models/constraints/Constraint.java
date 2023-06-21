package models.constraints;

/**
 * A simple implementation of a constraint.
 * 
 * It is composed of a Constraint on the right, a Constraint on the left, and an
 * operator
 * 
 * @author andrea
 *
 */
public class Constraint {
	/**
	 * The left part
	 */
	Constraint left;

	/**
	 * The rigth part
	 */
	Constraint right;

	/**
	 * The symbol
	 */
	String symbol;

	/**
	 * Build a new empty Constraint
	 */
	public Constraint() {
		this.left = null;
		this.right = null;
		this.symbol = "";
	}

	/**
	 * Build a new Constraint
	 * 
	 * @param left   the left part
	 * @param right  the right part
	 * @param symbol the symbol
	 */
	public Constraint(Constraint left, Constraint right, String symbol) {
		this.left = left;
		this.right = right;
		this.symbol = symbol;
	}

	/**
	 * Returns the left part of the constraint
	 * 
	 * @return the left part of the constraint
	 */
	public Constraint getLeft() {
		return left;
	}

	/**
	 * Sets the left part of the constraint
	 * 
	 * @param left the left part of the constraint
	 */
	public void setLeft(Constraint left) {
		this.left = left;
	}

	/**
	 * Returns the right part of the constraint
	 * 
	 * @return the right part of the constraint
	 */
	public Constraint getRight() {
		return right;
	}

	/**
	 * Sets the right part of the constraint
	 * 
	 * @param left the right part of the constraint
	 */
	public void setRight(Constraint right) {
		this.right = right;
	}

	/**
	 * Returns the symbol of the constraint
	 * 
	 * @return a string representing the constraint's symbol
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * Sets the symbol
	 * 
	 * @param symbol the symbol as string
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	/**
	 * Get the constraint in CTWEDGE format
	 * 
	 * @return a string representing the constraint in CTWEDGE format
	 */
	public String toString() {
		return "(" + left.toString() + " " + this.symbol + " " + right.toString() + ")";
	}
}
