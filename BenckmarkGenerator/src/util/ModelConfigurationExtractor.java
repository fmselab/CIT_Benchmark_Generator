package util;

import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.api.SolverException;

import ctwedge.ctWedge.Bool;
import ctwedge.ctWedge.CitModel;
import ctwedge.ctWedge.Constraint;
import ctwedge.ctWedge.Parameter;
import ctwedge.ctWedge.Range;
import ctwedge.modelanalyzer.AllInCNF;
import ctwedge.modelanalyzer.AllTheSameCardinality;
import ctwedge.modelanalyzer.AlsoEnumerativeParameters;
import ctwedge.modelanalyzer.AlsoIntegerParameters;
import ctwedge.modelanalyzer.BooleanOnlyParameters;
import ctwedge.modelanalyzer.CTWedgeModelAnalyzer;
import ctwedge.util.ParameterElementsGetterAsStrings;
import ctwedge.util.ext.Utility;
import generators.Track;
import pMedici.util.Operations;

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
	 * possible)
	 * 
	 * @return the test validity ratio
	 * @throws InterruptedException
	 */
	public double getTestValidityRatio() throws InterruptedException {
		return Operations.getTestValidityRatioFromModel(model);
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
		CTWedgeModelAnalyzer analyzer = new AllInCNF();
		// If the constraints are all in CNF form, then the track is CNF for sure
		if (model.getConstraints().size() > 0 && analyzer.process(model))
			return Track.CNF;

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
}
