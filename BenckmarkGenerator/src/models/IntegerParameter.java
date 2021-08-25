package models;

import generators.Randomizer;

public class IntegerParameter extends Parameter {
	
	int startFrom;
	int endTo;
	
	public IntegerParameter() {
		startFrom = 0;
		endTo = 0;
	}
	
	public IntegerParameter(String name, int startFrom, int endTo) {
		this.startFrom = startFrom;
		this.endTo = endTo;
		this.name = name;
	}
	

	@Override
	public String toString() {
		return this.name + ": [" + startFrom + " .. " + endTo + "]\n";
	}
	
	@SuppressWarnings("deprecation")
	public String getRandomValue() {
		return new Integer(Randomizer.generate(startFrom, endTo)).toString();
	}
}
