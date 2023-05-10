package models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ctwedge.ctWedge.CitModel;
import ctwedge.generator.medici.MediciCITGenerator;
import ctwedge.util.ext.Utility;
import generators.Randomizer;
import models.constraints.Constraint;
import pMedici.util.Operations;

public class Model {
	private String name;
	private List<Parameter> paramsList;
	private List<Constraint> constraintsList;

	public Model() {
		paramsList = new ArrayList<>();
		constraintsList = new ArrayList<>();
		name = "";
	}

	public String getName() {
		return name;
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

		for (Parameter p : paramsList) {
			model += p.toString();
		}
		if (!constraintsList.isEmpty()) {
			model += "\nConstraints:\n";

			for (Constraint c : constraintsList) {
				model += "# " + c.toString() + " #\n";
			}
		}

		return model;
	}

	public Parameter getRandomParamenter() {
		return paramsList.get(Randomizer.generate(0, paramsList.size() - 1));
	}

	public Parameter getRandomParamenterOfClass(Parameter similar) {
		List<Parameter> filtered = paramsList.stream().filter(x -> (x.getClass().equals(similar.getClass())))
				.collect(Collectors.toList());
		Parameter selected = filtered.get(Randomizer.generate(0, filtered.size() - 1));
		return selected;
	}

	public BigInteger getModelSize() {
		// Product of all the cardinalities of the model
		BigInteger prod = new BigInteger("1");

		for (Parameter p : paramsList)
			prod = prod.multiply(BigInteger.valueOf(p.getCardinality()));

		return prod;
	}

	public double getTestValidityRatio() throws InterruptedException {
		// Define the model as a CitModel
		CitModel loadModel = Utility.loadModel(this.toString());
		return Operations.getRatioFromModel(loadModel);
	}

}
