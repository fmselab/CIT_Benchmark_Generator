package models;

import java.util.ArrayList;
import java.util.List;

import generators.Randomizer;

/**
 * A simple class describing enumeratives.
 * 
 * Each parameter contain a list of the possible values
 * 
 * @author andrea
 *
 */
public class EnumerativeParameter extends Parameter {

	/**
	 * The list of possible values
	 */
	List<String> valuesList;

	/**
	 * Build a new empty enumerative
	 */
	public EnumerativeParameter() {
		this.valuesList = new ArrayList<>();
	}

	/**
	 * Build a new enumerative
	 * 
	 * @param name the name
	 */
	public EnumerativeParameter(String name) {
		this.valuesList = new ArrayList<>();
		this.name = name;
	}

	/**
	 * Adds a value to the enumerative
	 * 
	 * @param v the value
	 */
	public void addValue(String v) {
		this.valuesList.add(v);
	}

	/**
	 * Get the parameter declaration in CTWedge format
	 * 
	 * @return the parameter declaration in CTWedge format
	 */
	@Override
	public String toString() {
		String out = this.name + ": {";

		for (String v : valuesList) {
			out += v + ",";
		}

		out = out.substring(0, out.length() - 1) + "}\n";

		return out;
	}

	/**
	 * Returns a random value for the Enumerative parameter
	 * 
	 * @return a random value for the Enumerative parameter, expressed as string
	 */
	public String getRandomValue() {
		return valuesList.get(Randomizer.generate(0, valuesList.size() - 1));
	}

	/**
	 * Returns the cardinality of the parameter
	 * 
	 * @return the cardinality of the parameter
	 */
	@Override
	public int getCardinality() {
		return this.valuesList.size();
	}

	/**
	 * Get the possible values
	 * 
	 * @return the list of possible values
	 */
	@Override
	public List<String> getValues() {
		return valuesList;
	}
}
