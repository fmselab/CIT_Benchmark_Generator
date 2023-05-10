package models.constraints;

/**
 * A lightweight implementation of an IMPLIES constraint
 * 
 * @author andrea
 *
 */
public class ImpliesConstraint extends Constraint {
	
	/**
	 * Builds a new ImpliesConstraint.
	 * 
	 * It simply sets "=>" as symbol
	 */
	public ImpliesConstraint() {
		this.symbol = "=>";
	}
}
