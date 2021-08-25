package models;

import java.util.ArrayList;
import java.util.List;

import generators.Randomizer;

public class EnumerativeParameter extends Parameter{

	List<String> valuesList;
	
	public EnumerativeParameter() {
		this.valuesList = new ArrayList<>();
	}
	
	public EnumerativeParameter(String name) {
		this.valuesList = new ArrayList<>();
		this.name = name;
	}
	
	public void addValue(String v) {
		this.valuesList.add(v);
	}

	@Override
	public String toString() {
		String out = this.name + ": {";
		
		for (String v : valuesList) {
			out += v + ","; 
		}
		
		out = out.substring(0, out.length()-1) + "}\n";
		
		return out;
	}
	
	public String getRandomValue() {
		return valuesList.get(Randomizer.generate(0, valuesList.size()-1));
	}
	
}
