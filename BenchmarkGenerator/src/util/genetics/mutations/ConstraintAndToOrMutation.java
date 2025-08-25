package util.genetics.mutations;

import java.util.Random;

import ctwedge.ctWedge.impl.CtWedgeFactoryImpl;
import generators.Track;
import models.Model;
import util.genetics.solution.ModelSolution;

public class ConstraintAndToOrMutation extends ConstraintOperatorSubstitutionMutation {

	private static final long serialVersionUID = 1L;

	/**
	 * Builds a new ConstraintImpliesToDblImpliesMutation object
	 * 
	 * @param p the probability for applying the mutation
	 */
	public ConstraintAndToOrMutation(float p) {
		super(p);
	}
	
	/**
	 * Executes the mutation
	 * 
	 * @param solution the solution to be mutated
	 * @return the mutation solution
	 */
	@Override
	public ModelSolution execute(ModelSolution solution) {
		Model m = solution.getModel();
		Model mutated = mutateModel(m);
		ModelSolution mutatedSolution = new ModelSolution(mutated, solution.variables().size(),
				solution.objectives().length);
		return mutatedSolution;
	}

	/**
	 * Gets the mutation probability
	 * 
	 * @return the probability
	 */
	@Override
	public double mutationProbability() {
		return probability;
	}

	/**
	 * Mutate the model by changing an AND into an OR
	 * 
	 * @param m the model to mutate	 * 
	 * @return the mutated model
	 */
	public Model mutateModel(Model m) {
		Track track = m.getGeneratorConfiguration().TRACK;
		Random rng = new Random();
		
		// Unconstrained tracks do not support this mutation
		if (track == Track.MCA || track == Track.UNIFORM_ALL || track == Track.UNIFORM_BOOLEAN) {
			return m;
		}

		// Check the probability
		if (rng.nextFloat(0, 1) > probability)
			return m;

		// Constrained tracks
		CtWedgeFactoryImpl factory = new CtWedgeFactoryImpl();
		System.out.println("****** Changing AND in OR");
		Model mTemp = changeOperator(m, rng, factory.createAndExpression(), factory.createOrExpression());

		return mTemp;
	}
}
