package models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.sosy_lab.common.ShutdownManager;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.log.BasicLogManager;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.java_smt.SolverContextFactory;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;
import org.sosy_lab.java_smt.api.ProverEnvironment;
import org.sosy_lab.java_smt.api.SolverContext;
import org.sosy_lab.java_smt.api.SolverContext.ProverOptions;
import org.sosy_lab.java_smt.api.SolverException;

import ctwedge.ctWedge.CitModel;
import ctwedge.generator.pict.PICTGenerator;
import ctwedge.util.ModelUtils;
import ctwedge.util.NotConvertableModel;
import ctwedge.util.Test;
import ctwedge.util.ext.Utility;
import ctwedge.util.smt.SMTModelTranslator;
import ctwedge.util.smt.SMTParameterAdder.EnumTreatment;
import ctwedge.util.validator.RuleEvaluator;
import generators.Category;
import generators.GeneratorConfiguration;
import generators.Randomizer;
import generators.Track;
import kali.util.Operations;
import main.BenchmarkGeneratorCLI;
import models.constraints.Constraint;
import util.ACTSModelTranslator;
import util.ModelConfigurationExtractor;
import util.ParameterToModelAdder;

/**
 * A lightweight implementation of a combinatorial model, with methods useful
 * for extracting random parameters during model generation and other utility
 * methods (computing the cardinalities, test validity ratio, tuple validity
 * ratio, export in CTWedge and ACTS, check the solvability of the model, etc.)
 * 
 * @author andrea
 *
 */
public class Model {
	private String name;
	private List<Parameter> paramsList;
	private List<Constraint> constraintsList;
	private List<Integer> validityTests;
	private boolean isRatioExact;
	private Category category;
	Logger LOGGER = Logger.getLogger(BenchmarkGeneratorCLI.class);

	/**
	 * Get whether the ratio has been computed in an exact way or in an approximate
	 * way
	 * 
	 * @return TRUE if the ratio has been computed with MDDs, FALSE otherwise
	 */
	public boolean isRatioExact() {
		return isRatioExact;
	}

	/**
	 * Build an empty model.
	 */
	public Model(Category category) {
		paramsList = new ArrayList<>();
		constraintsList = new ArrayList<>();
		validityTests = new ArrayList<Integer>();
		name = "";
		isRatioExact = false;
		LOGGER.setLevel(Level.DEBUG);
		this.category = category;
	}

	/**
	 * Get the name of the model
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the model
	 * 
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Adds a parameter to the model
	 * 
	 * @param p the parameter
	 */
	public void addParameter(Parameter p) {
		this.paramsList.add(p);
	}

	/**
	 * Adds a new random parameter to the model, depending on the
	 * GeneratorConfiguration and on the chosen category
	 * 
	 * @param names the list of used names
	 */
	public void addNewRandomParameter(ArrayList<String> names) {
		int parameterNumber = getParameters() == null ? 0 : getParameters().size();
		int nValues = 0;
		int from = 100;

		switch (category) {
		case ONLY_BOOLEAN:
			// With only booleans
			ParameterToModelAdder.addBooleanParameter(this, names, parameterNumber);
			break;

		case ALSO_ENUMS:
			// With also enumerative
			// Probability 50% of extracting a boolean and 50% for enumerative
			if (Randomizer.generate(0, 1) == 0)
				ParameterToModelAdder.addBooleanParameter(this, names, parameterNumber);
			else {
				// Define a new enumerative parameter
				nValues = Randomizer.generate(GeneratorConfiguration.MIN_CARDINALITY,
						GeneratorConfiguration.MAX_CARDINALITY);
				ParameterToModelAdder.addEnumerativeParameter(this, nValues, names, parameterNumber);
			}
			break;

		case CONSTRAINTS_WITH_RELATIONAL:
			// With also enumerative and integers
			// Probability 33% of extracting a boolean and 33% for enumerative and 33% for
			// integers
			int parType = Randomizer.generate(0, 2);
			switch (parType) {
			case 0:
				ParameterToModelAdder.addBooleanParameter(this, names, parameterNumber);
				break;
			case 1:
				// Define a new enumerative parameter
				nValues = Randomizer.generate(GeneratorConfiguration.MIN_CARDINALITY,
						GeneratorConfiguration.MAX_CARDINALITY);
				ParameterToModelAdder.addEnumerativeParameter(this, nValues, names, parameterNumber);
				break;
			case 2:
				// Define a new integer parameter
				boolean computeParams = true;
				while (computeParams) {
					nValues = Randomizer.generate(GeneratorConfiguration.MIN_CARDINALITY,
							GeneratorConfiguration.MAX_CARDINALITY - 1);
					from = Randomizer.generate(GeneratorConfiguration.LOWER_BOUND_INT,
							GeneratorConfiguration.UPPER_BOUND_INT);

					if (from <= from + nValues && from + nValues <= GeneratorConfiguration.UPPER_BOUND_INT)
						computeParams = false;
				}

				// Define a new integer parameter
				ParameterToModelAdder.addIntegerParameter(this, nValues, from, names, parameterNumber);
				computeParams = true;
			}
			break;
		}

	}

	/**
	 * Remove a parameter from the model
	 * 
	 * @param p the parameter
	 */
	public void removeParameter(Parameter p) {
		this.paramsList.remove(p);
	}

	/**
	 * Adds a constraint to the model
	 * 
	 * @param p the constraint
	 */
	public void addConstraint(Constraint p) {
		this.constraintsList.add(p);
	}

	/**
	 * Removes a constraint and substitutes it with a new one
	 * 
	 * @param s the source constraint
	 * @param d the new constraint
	 */
	public void changeConstraint(Constraint s, Constraint d) {
		this.constraintsList.remove(s);
		this.constraintsList.add(d);
	}

	/**
	 * Removes a parameter and substitutes it with a new one
	 * 
	 * @param s the source parameter
	 * @param d the new parameter
	 */
	public void changeParameter(Parameter s, Parameter d) {
		this.paramsList.remove(s);
		this.paramsList.add(d);
	}

	/**
	 * Remove a constraint from the model
	 * 
	 * @param p the constraint
	 */
	public void removeConstraint(Constraint p) {
		this.constraintsList.remove(p);
	}

	/**
	 * Returns the model as a string in CTWedge format
	 * 
	 * @return the model as a string in CTWedge format
	 */
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

	/**
	 * Get a random parameter from the model
	 * 
	 * @return a random Parameter
	 */
	public Parameter getRandomParamenter() {
		return paramsList.get(Randomizer.generate(0, paramsList.size() - 1));
	}

	/**
	 * Get a random parameter from the model
	 * 
	 * @param similar a parameter
	 * @return a random Parameter, chosen between those of the same class of the
	 *         given argument
	 */
	public Parameter getRandomParamenterOfClass(Parameter similar) {
		List<Parameter> filtered = paramsList.stream().filter(x -> (x.getClass().equals(similar.getClass())))
				.collect(Collectors.toList());
		Parameter selected = filtered.get(Randomizer.generate(0, filtered.size() - 1));
		return selected;
	}

	/**
	 * Returns the test validity ratio (i.e. how many tests are valid among those
	 * possible)
	 * 
	 * @return the test validity ratio
	 * @throws InterruptedException
	 * @throws InvalidConfigurationException
	 * @throws IOException
	 */
	public double getTestValidityRatio() throws InterruptedException, InvalidConfigurationException, IOException {
		try {
			// If the model contains at least one integer, we cannot deal with it. Throw an
			// exception and manage it differently
			for (Parameter p : paramsList)
				if (p instanceof IntegerParameter)
					throw new NotConvertableModel("Computation of the ratio interrupted");

			// First save the CTWedge file
			File f = new File(getName() + ".ctw");
			FileWriter fo = new FileWriter(f);
			fo.write(toString());
			fo.close();
			LOGGER.debug("Test validity ratio computed using MEDICI. The model has been written in the " + getName()
					+ ".ctw file");

			// Now call MEDICI
			List<String> command = new ArrayList<String>();
			command.add(System.getProperty("user.dir") + "/medici");
			// --- Model
			command.add("--m");
			command.add(getName() + ".ctw");
			// --- Use CTWedge input format
			command.add("--ctw");
			// --- Do not generate
			command.add("--donotgenerate");
			LOGGER.debug("Executing command " + command);

			// Run
			ProcessBuilder pc = new ProcessBuilder(command);
			pc.command(command);
			pc.redirectError();
			Process p = pc.start();
			BigDecimal sizeWoConstraints = new BigDecimal(-1);
			BigDecimal sizeWConstraints = new BigDecimal(-1);
			try {
				BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				while ((line = bri.readLine()) != null) {
					LOGGER.debug(line);
					// save to file
					if (line.contains("Cardinalita di partenza")) {
						sizeWoConstraints = new BigDecimal((line.split(" ")[3]));
						if (sizeWoConstraints.doubleValue() == 0.0)
							throw new NotConvertableModel("Computation of the ratio interrupted");
					}
					if (line.contains("Cardinalita finale")) {
						sizeWConstraints = new BigDecimal((line.split(" ")[2]));
						if (sizeWConstraints.doubleValue() == 0.0)
							throw new NotConvertableModel("Computation of the ratio interrupted");
					}
				}
				bri.close();
				p.waitFor();
				System.out.println("command finished ");
			} catch (InterruptedException e) {
				f.delete();
				throw new NotConvertableModel("Computation of the ratio interrupted");
			}
			f.delete();
			// Compute ratio
			double ratio = sizeWConstraints.divide(sizeWoConstraints, MathContext.DECIMAL128).doubleValue();
			if (ratio < 0)
				throw new NotConvertableModel("Computation of the ratio failed");
			isRatioExact = true;
			LOGGER.debug("Ratio: " + ratio);
			return ratio;
		} catch (NotConvertableModel ex) {
			return getApproximateTestValidityRatio();
		}
	}

	/**
	 * Returns the test validity ratio (i.e. how many tests are valid among those
	 * possible)
	 * 
	 * @return the test validity ratio
	 */
	public double getApproximateTestValidityRatio() {
		// Define the model as a CitModel
		CitModel loadModel = Utility.loadModel(this.toString());
		// The model contains relational operators, thus a probabilistic approach has to
		// be used
		// It is based on extracting T tests and on checking whether they are applicable
		// or not
		int nValidTest = 0;
		validityTests.clear();
		LOGGER.debug("Test validity ratio computed using Monte Carlo Approximation");
		ModelUtils mu = new ModelUtils(loadModel);
		int T = (int) Math.ceil((1 / GeneratorConfiguration.RATIO_TEST)
				* ((4 * Math.log(2 / (1 - GeneratorConfiguration.P))) / (Math.pow(GeneratorConfiguration.EPSILON, 2))));
		for (int i = 0; i < T; i++) {
			Test t = mu.getRandomTestFromModel();
			RuleEvaluator evaluator = new RuleEvaluator(t);
			if (evaluator.evaluateModel(loadModel)) {
				nValidTest++;
				validityTests.add(1);
			} else {
				validityTests.add(0);
			}
		}
		isRatioExact = false;
		LOGGER.debug("--- Generated " + nValidTest + " valid tests out of " + T);
		return (double) nValidTest / T;
	}

	/**
	 * Returns the tuple validity ratio (i.e. how many tuples [pairs] are valid
	 * among those possible)
	 * 
	 * @return the tuple validity ratio
	 * @throws InterruptedException
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 */
	public double getTupleValidityRatio() throws InterruptedException, InvalidConfigurationException, SolverException {
		// Define the model as a CitModel
		CitModel loadModel = Utility.loadModel(this.toString());
		ModelConfigurationExtractor extractor = new ModelConfigurationExtractor(loadModel);
		if (extractor.getModelType() != Track.NUMC)
			return Operations.getTupleValidityRatioFromModel(loadModel);
		else
			return kali.util.Operations.getTupleValidityRatioFromModel(loadModel);
	}

	/**
	 * Get the highest cardinality in the model
	 * 
	 * @return the highest cardinality in the model
	 */
	public int getHighestCardinality() {
		int max = 0;
		for (Parameter p : paramsList) {
			if (p.getCardinality() > max)
				max = p.getCardinality();
		}
		return max;
	}

	/**
	 * Get the lowest cardinality in the model
	 * 
	 * @return the lowest cardinality in the model
	 */
	public int getLowestCardinality() {
		int min = Integer.MAX_VALUE;
		for (Parameter p : paramsList) {
			if (p.getCardinality() < min)
				min = p.getCardinality();
		}
		return min;
	}

	/**
	 * Export the model in CTWedge format in the current folder
	 */
	public void exportCTWedge() throws IOException {
		exportCTWedge(".");
	}

	/**
	 * Export the model in CTWedge format
	 * 
	 * @param destinationFolder the destination folder
	 */
	public void exportCTWedge(String destinationFolder) throws IOException {
		FileWriter fo = new FileWriter(new File(destinationFolder + "/" + this.getName() + ".ctw"));
		fo.write(this.toString());
		fo.close();
	}

	/**
	 * Export the model in ACTS format in the current folder
	 * 
	 * @throws IOException
	 */
	public void exportACTS() throws IOException {
		exportACTS(".");
	}

	/**
	 * Export the model in ACTS format
	 * 
	 * @param destinationFolder the destination folder
	 * @throws IOException
	 */
	public void exportACTS(String destinationFolder) throws IOException {
		ACTSModelTranslator translator = new ACTSModelTranslator();
		translator.saveActsTXTonlyModel(this, destinationFolder);
	}

	/**
	 * Export the model in PICT format in the current folder
	 * 
	 * @throws IOException
	 */
	public void exportPICT() throws IOException {
		exportPICT(".");
	}

	/**
	 * Export the model in PICT format
	 * 
	 * @param destinationFolder the destination folder
	 * @throws IOException
	 */
	public void exportPICT(String destinationFolder) throws IOException {
		CitModel model = Utility.loadModel(this.toString());
		PICTGenerator generator = new PICTGenerator();
		String modelStr = generator.translateModel(model, false);
		FileWriter fo = new FileWriter(new File(destinationFolder + "/" + this.getName() + "_pict.txt"));
		fo.write(modelStr);
		fo.close();
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
			ProverEnvironment prover = buildProverEnvironmentFromModel();

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

	/**
	 * Builds the prover environment from the model
	 * 
	 * @return the prover environment
	 * @throws InvalidConfigurationException
	 */
	private ProverEnvironment buildProverEnvironmentFromModel() throws InvalidConfigurationException {
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

		// Create the context
		SMTModelTranslator trans = new SMTModelTranslator(EnumTreatment.INTEGER);

		prover = trans.createCtxFromModel(citModel, citModel.getConstraints(), ctx, prover);
		return prover;
	}

	/**
	 * Access the list of constraints
	 * 
	 * @return the list of constraints
	 */
	public List<Constraint> getConstraints() {
		return this.constraintsList;
	}

	/**
	 * Access the list of parameters
	 * 
	 * @return the list of parameters
	 */
	public List<Parameter> getParameters() {
		return this.paramsList;
	}

}
