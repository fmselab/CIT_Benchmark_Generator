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

	public float getTupleValidityRatio() {
		float ratio = 0;

		MediciCITGenerator gen = new MediciCITGenerator();
		// Define the model as a CitModel
		CitModel loadModel = Utility.loadModel(this.toString());

		// Convert the model to medici
		try {
			File model = new File("model.txt");
			FileWriter wf = new FileWriter(model);
			String translateModel = gen.translateModel(loadModel, false);
			wf.write(translateModel);
			wf.close();

			// Call Medici
			List<String> command = new ArrayList<String>();
			command.add("./medici.exe");
			// Model
			command.add("--m");
			command.add("model.txt");

			// Do not generate
			command.add("--donotgenerate");

			// Run
			ProcessBuilder pc = new ProcessBuilder(command);
			pc.command(command);
			pc.redirectError();
			Process p;
			p = pc.start();
			try {
				BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				while ((line = bri.readLine()) != null) {
					if (line.contains("Cardinalita di partenza"))
						ratio = Float.parseFloat(line.substring("Cardinalita di partenza".length() + 1));

					if (line.contains("Cardinalita finale"))
						ratio = Float.parseFloat(line.substring("Cardinalita finale".length() + 1)) / ratio;
				}

				bri.close();
				p.waitFor();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return ratio;
	}

	public float getTestValidityRatio() {
		float ratio = 0;

		MediciCITGenerator gen = new MediciCITGenerator();
		// Define the model as a CitModel
		CitModel loadModel = Utility.loadModel(this.toString());

		// Convert the model to medici
		try {
			File model = new File("model.txt");
			FileWriter wf = new FileWriter(model);
			String translateModel = gen.translateModel(loadModel, false);
			wf.write(translateModel);
			wf.close();

			// Call Medici
			List<String> command = new ArrayList<String>();
			command.add("./medici.exe");
			// Model
			command.add("--m");
			command.add("model.txt");

			// Do not generate
			command.add("--donotgenerate");

			// Run
			ProcessBuilder pc = new ProcessBuilder(command);
			pc.command(command);
			pc.redirectError();
			Process p;
			p = pc.start();
			try {
				BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				while ((line = bri.readLine()) != null) {
					if (line.contains("Generated tuples for 2-wise"))
						ratio = Float.parseFloat(
								line.substring("Generated tuples for 2-wise with cardinality".length() + 1));

					if (line.contains("size dopo controllo copribilita"))
						ratio = Float.parseFloat(line.substring("size dopo controllo copribilita".length() + 1))
								/ ratio;
				}

				bri.close();
				p.waitFor();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return ratio;
	}

}
