package util.genetics.mutations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import generators.Track;
import models.Model;

public class ConstraintRemoverMutation implements EvolutionaryOperator<Model> {

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
	 * Apply the mutation to the selected candidates
	 * 
	 * @param selectedCandidates the selected candidates
	 * @param rng                the random
	 * @return the mutated population
	 */
	@Override
	public List<Model> apply(List<Model> selectedCandidates, Random rng) {

		List<Model> mutatedPopulation = new ArrayList<Model>(selectedCandidates.size());
		for (Model m : selectedCandidates) {
			mutatedPopulation.add(mutateModel(m, rng));
		}
		return mutatedPopulation;

	}

	/**
	 * Mutate the model by removing a constraint
	 * 
	 * @param m   the model to mutate
	 * @param rng the random number generator
	 * @return the mutated model
	 */
	public Model mutateModel(Model m, Random rng) {
		Track track = m.getGeneratorConfiguration().TRACK;
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
