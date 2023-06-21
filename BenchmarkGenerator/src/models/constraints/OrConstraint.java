package models.constraints;

/**
 * A lightweight implementation of an OR constraint
 * 
 * @author andrea
 *
 */
public class OrConstraint extends Constraint {

	/**
	 * Builds a new OrConstraint.
	 * 
	 * It simply sets "OR" as symbol
	 */
	public OrConstraint() {
		this.symbol = "OR";
	}
}
