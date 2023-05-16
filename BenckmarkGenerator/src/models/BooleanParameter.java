package models;

import java.util.ArrayList;
import java.util.List;

import util.Randomizer;

/**
 * A class representing boolean parameters that can be only TRUE or FALSE
 * 
 * @author andrea
 *
 */
public class BooleanParameter extends Parameter {

	/**
	 * Get the parameter declaration in CTWedge format
	 * 
	 * @return the parameter declaration in CTWedge format
	 */
	@Override
	public String toString() {
		return this.name + " : Boolean\n";
	}

	/**
	 * Build a new boolean parameter
	 * 
	 * @param name the name
	 */
	public BooleanParameter(String name) {
		this.name = name;
	}

	/**
	 * Returns a random value for the parameter
	 * 
	 * @return a random value for the parameter, expressed as String
	 */
	@Override
	public String getRandomValue() {
		if (Randomizer.generate(0, 1) == 0)
			return "false";

		return "true";
	}

	/**
	 * Returns the cardinality of the parameter
	 * 
	 * @return 2, i.e., the cardinality of a boolean
	 */
	@Override
	public int getCardinality() {
		return 2;
	}
	
	/**
	 * Get the possible values
	 * 
	 * @return the list of possible values
	 */
	@Override
	public List<String> getValues() {
		ArrayList<String> values = new ArrayList<>();
		values.add("true");
		values.add("false");
		return values;
	}

}
