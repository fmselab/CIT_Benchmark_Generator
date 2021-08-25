package models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import generators.Randomizer;
import models.constraints.Constraint;

public class Model {
	private String name;
	private List<Parameter> paramsList;
	private List<Constraint> constraintsList;
	
	public Model() {
		paramsList = new ArrayList<>();
		constraintsList = new ArrayList<>();
		name = "";
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void addParameter(Parameter p) {
		this.paramsList.add(p);
	}
	
	public void addConstraint(Constraint p) {
		this.constraintsList.add(p);
	}
	
	public String toString() {
		String model;
		
		model = "Model " + this.name + "\nParameters:\n";
		
		for(Parameter p : paramsList) {
			model += p.toString();
		}
		
		model += "\nConstraints:\n";
		
		for(Constraint c : constraintsList) {
			model += "# " + c.toString() + " #\n";
		}
		
		return model;		
	}
	
	public Parameter getRandomParamenter() {
		return paramsList.get(Randomizer.generate(0, paramsList.size()-1));
	}
	
	public Parameter getRandomParamenterOfClass(Parameter similar) {
		List<Parameter> filtered = paramsList.stream().filter(x -> (x.getClass().equals(similar.getClass()))).collect(Collectors.toList());
		Parameter selected = filtered.get(Randomizer.generate(0, filtered.size()-1));
		return selected;
	}
		
}
