package models;

import generators.Randomizer;

public class BooleanParameter extends Parameter{

	@Override
	public String toString() {
		return this.name + " : Boolean\n";
	}
	
	public BooleanParameter(String name) {
		this.name = name;
	}

	@Override
	public String getRandomValue() {
		if (Randomizer.generate(0, 1) == 0)
			return "false";
		
		return "true";
	}

	@Override
	public int getCardinality() {
		return 2;
	}
	
	
}
