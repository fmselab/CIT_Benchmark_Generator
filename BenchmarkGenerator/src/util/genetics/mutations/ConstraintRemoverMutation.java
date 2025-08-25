package util.genetics.mutations;

import java.util.Random;

import org.uma.jmetal.operator.mutation.MutationOperator;

import generators.Track;
import models.Model;
import util.genetics.solution.ModelSolution;

public class ConstraintRemoverMutation implements MutationOperator<ModelSolution> {

	private static final long serialVersionUID = 1L;

	/**
	 * The probability for applying the mutation
	 */
	private float probability;

	/**
	 * Builds a new ConstraintRemoverMutation object
	 * 
	 * @param p the probability for applying the mutation
	 */
	public ConstraintRemoverMutation(float p) {
		this.probability = p;
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
	 * Mutate the model by removing a constraint
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
		Model mTemp = (Model) m.clone();

		if (m.getConstraints().size() > m.getGeneratorConfiguration().N_CONSTRAINTS_MIN) {
			int nConstraint = rng.nextInt(0, m.getConstraints().size() - 1);
			System.out.println("****** Removing the " + nConstraint + "-th constraint: Remaining "
					+ (m.getConstraints().size() - 1) + " constraints");

			mTemp.removeConstraint(nConstraint);
		}

		return mTemp;
	}
}
