package models;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.sosy_lab.common.ShutdownManager;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.log.BasicLogManager;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.java_smt.SolverContextFactory;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.ProverEnvironment;
import org.sosy_lab.java_smt.api.SolverContext;
import org.sosy_lab.java_smt.api.SolverException;
import org.sosy_lab.java_smt.api.SolverContext.ProverOptions;

import ctwedge.ctWedge.CitModel;
import ctwedge.generator.acts.ACTSTranslator;
import ctwedge.util.ext.Utility;
import ctwedge.util.validator.SMTConstraintChecker;
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
		return Operations.getTestValidityRatioFromModel(loadModel);
	}
	
	public double getTupleValidityRatio() throws InterruptedException {
		// Define the model as a CitModel
		CitModel loadModel = Utility.loadModel(this.toString());
		return Operations.getTupleValidityRatioFromModel(loadModel);
	}
	
	public int getMaxValues() {
		int max = 0;
		for (Parameter p : paramsList) {
			if (p.getCardinality() > max)
				max = p.getCardinality();
		}
		return max;
	}
	
	/**
	 * Export the model in CTWedge format
	 */
	public void exportCTWedge() throws IOException {
		FileWriter fo = new FileWriter(new File(this.getName() + ".ctw"));
		fo.write(this.toString());
		fo.close();
	}

	/**
	 * Export the model in ACTS format
	 */
	public void exportACTS() {
		CitModel ctwedgeModel = Utility.loadModel(this.toString());
		ACTSTranslator translator = new ACTSTranslator();
		translator.convertModel(ctwedgeModel, true, 2, ".");
	}
	
	/**
	 * Verifies if a given model is solvable by exploiting the
	 * CTWedge.util.validator
	 * 
	 * @return TRUE if the model is solvable, FALSE otherwise
	 */
	public boolean isSolvable() {
		boolean isSolvable = false;
		try {
			// Build the CIT Model
			CitModel citModel = Utility.loadModel(toString());

			// Set up the SMT Solver context and parameters
			Configuration config = Configuration.defaultConfiguration();
			LogManager logger;
			logger = BasicLogManager.create(config);
			ShutdownManager shutdown = ShutdownManager.create();
			SolverContext ctx = SolverContextFactory.createSolverContext(config, logger, shutdown.getNotifier(),
					Solvers.SMTINTERPOL);
			ProverEnvironment prover = ctx.newProverEnvironment(ProverOptions.GENERATE_MODELS);
			Map<String, String> declaredElements = new HashMap<>();
			Map<ctwedge.ctWedge.Parameter, Formula> variables = new HashMap<ctwedge.ctWedge.Parameter, Formula>();

			// Create the context
			prover = SMTConstraintChecker.createCtxFromModel(citModel, citModel.getConstraints(), ctx, declaredElements,
					variables, prover);

			// Verify if it is empty
			if (prover.isUnsat())
				isSolvable = false;
			else
				isSolvable = true;
		} catch (InvalidConfigurationException | SolverException | InterruptedException e) {
			e.printStackTrace();
		}
		return isSolvable;
	}

}
