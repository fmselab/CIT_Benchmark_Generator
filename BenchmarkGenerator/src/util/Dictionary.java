package util;

import java.util.ArrayList;

/**
 * This class represent the entry in the dictionary to be used in the generated
 * IPMs.
 */
public class Dictionary {

	/** The name of the parameter */
	String name;

	/** The type of parameter (Boolean, Enum, Integer) */
	String type;

	/** The lower bound, in the case of integers */
	int lowerBound;

	/** The upper bound, in the case of integers */
	int upperBound;

	/** The list of possible values, in the case of enums */
	ArrayList<String> values;

	/**
	 * Instantiates a new dictionary.
	 *
	 * @param name the name of the parameter
	 * @param type the type of the parameter
	 * @param lowerBound the lower bound of the parameter
	 * @param upperBound the upper bound of the parameter
	 * @param values the values of the parameter
	 */
	public Dictionary(String name, String type, int lowerBound, int upperBound, ArrayList<String> values) {
		super();
		this.name = name;
		this.type = type;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.values = values;
	}
	
	/**
	 * Instantiates a new dictionary - boolean version.
	 *
	 * @param name the name of the parameter
	 */
	public Dictionary(String name) {
		super();
		this.name = name;
		this.type = "Boolean";
		this.values = null;
	}
	
	/**
	 * Instantiates a new dictionary - enum version.
	 *
	 * @param name the name of the parameter
	 * @param values the values of the parameter
	 */
	public Dictionary(String name, ArrayList<String> values) {
		super();
		this.name = name;
		this.type = "Enum";
		this.lowerBound = -1;
		this.upperBound = -1;
		this.values = values;
	}
	/**
	 * Instantiates a new dictionary - Integer version.
	 *
	 * @param name the name of the parameter
	 * @param lowerBound the lower bound of the parameter
	 * @param upperBound the upper bound of the parameter
	 */
	public Dictionary(String name, int lowerBound, int upperBound) {
		super();
		this.name = name;
		this.type = "Integer";
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.values = null;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the lower bound.
	 *
	 * @return the lower bound
	 */
	public int getLowerBound() {
		return lowerBound;
	}

	/**
	 * Sets the lower bound.
	 *
	 * @param lowerBound the new lower bound
	 */
	public void setLowerBound(int lowerBound) {
		this.lowerBound = lowerBound;
	}

	/**
	 * Gets the upper bound.
	 *
	 * @return the upper bound
	 */
	public int getUpperBound() {
		return upperBound;
	}

	/**
	 * Sets the upper bound.
	 *
	 * @param upperBound the new upper bound
	 */
	public void setUpperBound(int upperBound) {
		this.upperBound = upperBound;
	}

	/**
	 * Gets the values.
	 *
	 * @return the values
	 */
	public ArrayList<String> getValues() {
		return values;
	}

	/**
	 * Sets the values.
	 *
	 * @param values the new values
	 */
	public void setValues(ArrayList<String> values) {
		this.values = values;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "Dictionary [name=" + name + ", type=" + type + ", lowerBound=" + lowerBound + ", upperBound="
				+ upperBound + ", values=" + values + "]";
	}

	/** 
	 * Adds a new value to the possible ones
	 * @param value the value
	 */
	public void addValue(String value) {
		if (this.values == null)
			this.values = new ArrayList<>();
		
		this.values.add(value);
		
	}

}
