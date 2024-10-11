package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.api.SolverException;

import ctwedge.ctWedge.Bool;
import ctwedge.ctWedge.CitModel;
import ctwedge.ctWedge.Constraint;
import ctwedge.ctWedge.Parameter;
import ctwedge.ctWedge.Range;
import ctwedge.generator.medici.MediciCITGenerator;
import ctwedge.modelanalyzer.AllInCNF;
import ctwedge.modelanalyzer.AllTheSameCardinality;
import ctwedge.modelanalyzer.AlsoEnumerativeParameters;
import ctwedge.modelanalyzer.AlsoIntegerParameters;
import ctwedge.modelanalyzer.BooleanOnlyParameters;
import ctwedge.modelanalyzer.CTWedgeModelAnalyzer;
import ctwedge.modelanalyzer.FobiddenTuples;
import ctwedge.util.ModelUtils;
import ctwedge.util.NotConvertableModel;
import ctwedge.util.ParameterElementsGetterAsStrings;
import ctwedge.util.Test;
import ctwedge.util.ext.Utility;
import ctwedge.util.validator.RuleEvaluator;
import generators.Track;

/**
 * This class offers a method for analyzing a CTWedge model and to compute the
 * configuration parameters needed for generating models similar to the analyzed
 * one
 * 
 * @author andrea
 *
 */
public class ModelConfigurationExtractor {

	/**
	 * The model to be analyzed
	 */
	CitModel model;

	/**
	 * Builds a new ModelConfigurationExtractor
	 * 
	 * @param path the path of the model
	 */
	public ModelConfigurationExtractor(String path) {
		model = Utility.loadModelFromPath(path);
	}

	/**
	 * Builds a new ModelConfigurationExtractor
	 * 
	 * @param model the CitModel
	 */
	public ModelConfigurationExtractor(CitModel model) {
		this.model = model;
	}

	/**
	 * Returns the number of parameters
	 * 
	 * @return the number of parameters
	 */
	public int getNumParams() {
		return model.getParameters().size();
	}

	/**
	 * Returns the number of constraints
	 * 
	 * @return the number of constraints
	 */
	public int getNumConstraints() {
		return model.getConstraints().size();
	}

	/**
	 * Returns the test validity ratio (i.e. how many tests are valid among those
	 * possible).
	 * 
	 * The method works for all the models except for those in the NUMC category,
	 * for which the exact solution cannot be applied
	 * 
	 * @return the test validity ratio
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public double getTestValidityRatio() throws InterruptedException, IOException {
		try {
			// If the model contains at least one integer, we cannot deal with it. Throw an
			// exception and manage it differently
			for (Parameter p : model.getParameters())
				if (p instanceof Range)
					throw new NotConvertableModel("Computation of the ratio interrupted");

			// First save the CTWedge file
			File m = new File(getModelName() + ".txt");
			FileWriter wf = new FileWriter(m);
			MediciCITGenerator gen = new MediciCITGenerator();
			MediciCITGenerator.OUTPUT_ON_STD_OUT_DURING_TRANSLATION = false;
			String translateModel = gen.translateModel(model, false);
			wf.write(translateModel);
			wf.close();

			// Now call MEDICI
			List<String> command = new ArrayList<String>();
			command.add(System.getProperty("user.dir") + "/medici");
			// --- Model
			command.add("--m");
			command.add(getModelName() + ".txt");
			// --- Do not generate
			command.add("--donotgenerate");

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
					System.out.println(line);
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
				m.delete();
				throw new NotConvertableModel("Computation of the ratio interrupted");
			}
			m.delete();
			// Compute ratio
			double ratio = sizeWConstraints.divide(sizeWoConstraints, MathContext.DECIMAL128).doubleValue();
			if (ratio < 0)
				throw new NotConvertableModel("Computation of the ratio failed");
			return ratio;
		} catch (NotConvertableModel ex) {
			// The model contains relational operators, thus a probabilistic approach has to
			// be used
			// It is based on extracting T tests and on checking whether they are applicable
			// or not
			int nValidTest = 0;
			int totalTests = 1000;
			ModelUtils mu = new ModelUtils(model);
			for (int i = 0; i < totalTests; i++) {
				Test t = mu.getRandomTestFromModel();
				RuleEvaluator evaluator = new RuleEvaluator(t);
				if (evaluator.evaluateModel(model)) {
					nValidTest++;
				} else {
				}
			}
			return (double) nValidTest / totalTests;
		}
	}

	/**
	 * Returns the tuple validity ratio (i.e. how many tuples [pairs] are valid
	 * among those possible)
	 * 
	 * @return the tuple validity ratio
	 * @throws InterruptedException
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 * @throws IOException
	 */
	public double getTupleValidityRatio()
			throws InterruptedException, InvalidConfigurationException, SolverException, IOException {
		if (getModelType() != Track.NUMC)
			try {
				// If the model contains at least one integer, we cannot deal with it. Throw an
				// exception and manage it differently
				for (Parameter p : model.getParameters())
					if (p instanceof Range)
						throw new NotConvertableModel("Computation of the ratio interrupted");

				// First save the CTWedge file
				File m = new File(getModelName() + ".txt");
				FileWriter wf = new FileWriter(m);
				MediciCITGenerator gen = new MediciCITGenerator();
				MediciCITGenerator.OUTPUT_ON_STD_OUT_DURING_TRANSLATION = false;
				String translateModel = gen.translateModel(model, false);
				wf.write(translateModel);
				wf.close();

				// Now call MEDICI
				List<String> command = new ArrayList<String>();
				command.add(System.getProperty("user.dir") + "/medici");
				// --- Model
				command.add("--m");
				command.add(getModelName() + ".txt");
				// --- Do not generate
				command.add("--donotgenerate");

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
						System.out.println(line);
						// save to file
						if (line.contains("Generated tuples for 2-wise with cardinality")) {
							sizeWoConstraints = new BigDecimal((line.split(" ")[6]));
							if (sizeWoConstraints.doubleValue() == 0.0)
								throw new NotConvertableModel("Computation of the ratio interrupted");
						}
						if (line.contains("size dopo controllo copribilita ")) {
							sizeWConstraints = new BigDecimal((line.split(" ")[4]));
							if (sizeWConstraints.doubleValue() == 0.0)
								throw new NotConvertableModel("Computation of the ratio interrupted");
						}
					}
					bri.close();
					p.waitFor();
					System.out.println("command finished ");
				} catch (InterruptedException e) {
					m.delete();
					throw new NotConvertableModel("Computation of the ratio interrupted");
				}
				m.delete();
				// Compute ratio
				double ratio = sizeWConstraints.divide(sizeWoConstraints, MathContext.DECIMAL128).doubleValue();
				if (ratio < 0)
					throw new NotConvertableModel("Computation of the ratio failed");
				return ratio;
			} catch (NotConvertableModel ex) {
				// The model contains relational operators, thus a probabilistic approach has to
				// be used
				// It is based on extracting T tests and on checking whether they are applicable
				// or not
				return kali.util.Operations.getTupleValidityRatioFromModel(model);
			}
		else
			return kali.util.Operations.getTupleValidityRatioFromModel(model);
	}

	/**
	 * Returns the minimum cardinality
	 * 
	 * @return the minimum cardinality
	 */
	public int getMinimumCardinality() {
		int cardinality = 0;
		for (Parameter p : model.getParameters()) {
			int thisCardinality = ParameterElementsGetterAsStrings.instance.doSwitch(p).size();
			if (cardinality == 0 || thisCardinality < cardinality)
				cardinality = thisCardinality;
		}

		return cardinality;
	}

	/**
	 * Returns the name of the analyzed model
	 * 
	 * @return the name of the analyzed model
	 */
	public String getModelName() {
		return model.getName();
	}

	/**
	 * Returns the maximum cardinality
	 * 
	 * @return the maximum cardinality
	 */
	public int getMaximumCardinality() {
		int cardinality = 0;
		for (Parameter p : model.getParameters()) {
			int thisCardinality = ParameterElementsGetterAsStrings.instance.doSwitch(p).size();
			if (cardinality == 0 || thisCardinality > cardinality)
				cardinality = thisCardinality;
		}

		return cardinality;
	}

	/**
	 * Returns the lower bound for integer parameters
	 * 
	 * @return the the lower bound for integer parameters. If no integer parameters
	 *         are available, return -1
	 */
	public int getLowerBoundForInts() {
		int bound = Integer.MAX_VALUE;
		boolean intFound = false;
		for (Parameter p : model.getParameters()) {
			if (p instanceof Range) {
				if (((Range) p).getBegin().intValue() < bound)
					bound = ((Range) p).getBegin().intValue();
				// Keep track that an integer has been found
				intFound = true;
			}
		}

		if (intFound)
			return bound;

		return -1;
	}

	/**
	 * Returns the upper bound for integer parameters
	 * 
	 * @return the the upper bound for integer parameters. If no integer parameters
	 *         are available, return -1
	 */
	public int getUpperBoundForInts() {
		int bound = Integer.MIN_VALUE;
		boolean intFound = false;
		for (Parameter p : model.getParameters()) {
			if (p instanceof Range) {
				if (((Range) p).getEnd().intValue() > bound)
					bound = ((Range) p).getEnd().intValue();
				// Keep track that an integer has been found
				intFound = true;
			}
		}

		if (intFound)
			return bound;

		return -1;
	}

	/**
	 * Returns the maximum cardinality of a constraint in the given model
	 * 
	 * @return the maximum cardinality of a constraint in the given model. If no
	 *         constraints are available in the model, 0 is returned
	 */
	public int getMaxConstraintComplexity() {
		int complexity = 0;
		ConstraintComplexityExtractor extractor = new ConstraintComplexityExtractor();
		for (Constraint c : model.getConstraints()) {
			int thisComplexity = extractor.doSwitch(c);
			if (thisComplexity > complexity)
				complexity = thisComplexity;
		}

		return complexity;
	}

	/**
	 * Returns the minimum cardinality of a constraint in the given model
	 * 
	 * @return the minimum cardinality of a constraint in the given model. If no
	 *         constraints are available in the model, 0 is returned
	 */
	public int getMinConstraintComplexity() {
		if (model.getConstraints().size() == 0)
			return 0;

		int complexity = Integer.MAX_VALUE;
		ConstraintComplexityExtractor extractor = new ConstraintComplexityExtractor();
		for (Constraint c : model.getConstraints()) {
			int thisComplexity = extractor.doSwitch(c);
			if (thisComplexity < complexity)
				complexity = thisComplexity;
		}

		return (complexity == 0 ? 1 : complexity);
	}

	public boolean useConstraintsBetweenParameters() {
		// TODO

		return false;
	}

	/**
	 * Return the track of the model, by analyzing it and check which kind of
	 * parameters are present.
	 * 
	 * It exploits the Model Analyzer embedded into CTWedge.Util
	 * 
	 * @return the corresponding track of the model
	 */
	public Track getModelType() {
		CTWedgeModelAnalyzer analyzer;

		// If no constraint is available, and the cardinality is the same...
		// If at least one is not a boolean, the track is UNIFORMALL, otherwise it is
		// UNIFORMBOOLEAN
		analyzer = new AllTheSameCardinality();
		if (model.getConstraints().size() == 0 && analyzer.process(model))
			if (model.getParameters().stream().filter(x -> x instanceof Bool).count() != model.getParameters().size())
				return Track.UNIFORM_ALL;
			else
				return Track.UNIFORM_BOOLEAN;

		// If the parameters are only booleans and we have constraints, the track is
		// BOOLC
		analyzer = new BooleanOnlyParameters();
		if (model.getConstraints().size() > 0 && analyzer.process(model))
			return Track.BOOLC;

		// If the parameters are also enumeratives and we have not constraints, the
		// track is MCA, otherwise it is MCAC
		analyzer = new AlsoEnumerativeParameters();
		if (analyzer.process(model) && !(new AlsoIntegerParameters().process(model)))
			if (model.getConstraints().size() == 0)
				return Track.MCA;
			else
				return Track.MCAC;

		// It is a NUMC model
		return Track.NUMC;
	}

	/**
	 * Returns whether the model expresses the constraints only as forbidden tuples
	 * 
	 * @return TRUE if the model expresses the constraints only as forbidden tuples,
	 *         FALSE otherwise
	 */
	public boolean hasForbiddenTuples() {
		CTWedgeModelAnalyzer analyzer = new FobiddenTuples();
		// If the constraints are all in FT form
		if (model.getConstraints().size() > 0 && analyzer.process(model))
			return true;
		return false;
	}

	/**
	 * Returns whether the model expresses the constraints only in CNF
	 * 
	 * @return TRUE if the model expresses the constraints only in CNF, FALSE
	 *         otherwise
	 */
	public boolean isCNF() {
		CTWedgeModelAnalyzer analyzer = new AllInCNF();
		// If the constraints are all in CNF form
		if (model.getConstraints().size() > 0 && analyzer.process(model))
			return true;
		return false;
	}
}
