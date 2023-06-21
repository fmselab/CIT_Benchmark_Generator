package models.constraints;

/**
 * A lightweight implementation of a DOUBLE IMPLIES constraint
 * 
 * @author andrea
 *
 */
public class DoubleImpliesConstraint extends Constraint {

	/**
	 * Builds a new DoubleImpliesConstraint.
	 * 
	 * It simply sets "<=>" as symbol
	 */
	public DoubleImpliesConstraint() {
		this.symbol = "<=>";
	}
}
