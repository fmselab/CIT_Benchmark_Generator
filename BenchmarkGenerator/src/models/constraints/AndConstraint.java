package models.constraints;

/**
 * A lightweight implementation of an AND constraint
 * 
 * @author andrea
 *
 */
public class AndConstraint extends Constraint {

	/**
	 * Builds a new AndConstraint.
	 * 
	 * It simply sets "AND" as symbol
	 */
	public AndConstraint() {
		this.symbol = "AND";
	}
}
