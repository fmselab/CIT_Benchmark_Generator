package models;

/**
 * An abstract class for parameters
 * 
 * Each parameter must have a name
 * 
 * @author andrea
 *
 */
public abstract class Parameter {
	/**
	 * The name of the parameter
	 */
	String name;

	/**
	 * The method giving the CTWedge description of the parameter
	 * 
	 * @return the CTWedge description of the parameter
	 */
	public abstract String toString();

	/**
	 * The method giving a random value for the parameter
	 * 
	 * @return the random value, as string
	 */
	public abstract String getRandomValue();

	/**
	 * Get the name of the parameter
	 * 
	 * @return the name of the parameter
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the cardinality of the parameter, i.e., the number of possible values
	 * 
	 * @return the cardinality
	 */
	public abstract int getCardinality();
}
