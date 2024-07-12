package models;

import java.util.ArrayList;
import java.util.List;

import generators.Randomizer;

/**
 * A class representing integer parameters, with a lower and upper bound
 * 
 * @author andrea
 *
 */
public class IntegerParameter extends Parameter {

	/**
	 * The lower bound
	 */
	int startFrom;

	/**
	 * The lower bound
	 */
	int endTo;

	/**
	 * Build an empty integer parameter, starting from 0 to 0
	 */
	public IntegerParameter() {
		startFrom = 0;
		endTo = 0;
	}

	/**
	 * Build a new IntegerParameter
	 * 
	 * @param name      the name
	 * @param startFrom the lower bound
	 * @param endTo     the upper bound
	 */
	public IntegerParameter(String name, int startFrom, int endTo) {
		this.startFrom = startFrom;
		this.endTo = endTo;
		this.name = name;
	}

	/**
	 * Get the parameter declaration in CTWedge format
	 * 
	 * @return the parameter declaration in CTWedge format
	 */
	@Override
	public String toString() {
		return this.name + ": [" + startFrom + " .. " + endTo + "]\n";
	}

	/**
	 * Returns a random value for the Integer parameter
	 * 
	 * @return a random value for the Integer parameter, expressed as string
	 */
	@SuppressWarnings({ "removal" })
	public String getRandomValue() {
		return new Integer(Randomizer.generate(startFrom, endTo)).toString();
	}

	/**
	 * Returns the cardinality of the parameter
	 * 
	 * @return the cardinality of the parameter
	 */
	@Override
	public int getCardinality() {
		return this.endTo - this.startFrom + 1;
	}
	
	/**
	 * Get the possible values
	 * 
	 * @return the list of possible values
	 */
	@Override
	public List<String> getValues() {
		ArrayList<String> values = new ArrayList<>();
		for (int i=startFrom; i<=endTo; i++) {
			values.add(Integer.toString(i));
		}		
		return values;
	}
}
