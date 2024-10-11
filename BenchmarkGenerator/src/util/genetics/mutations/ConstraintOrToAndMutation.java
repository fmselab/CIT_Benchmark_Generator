package util.genetics.mutations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import generators.Track;
import models.Model;

public class ConstraintOrToAndMutation extends ConstraintOperatorSubstitutionMutation {

	/**
	 * Builds a new ConstraintImpliesToDblImpliesMutation object
	 * 
	 * @param p the probability for applying the mutation
	 */
	public ConstraintOrToAndMutation(float p) {
		super(p);
	}

	@Override
	public List<Model> apply(List<Model> selectedCandidates, Random rng) {

		List<Model> mutatedPopulation = new ArrayList<Model>(selectedCandidates.size());
		for (Model m : selectedCandidates) {
			mutatedPopulation.add(mutateModel(m, rng));
		}
		return mutatedPopulation;

	}

	Model mutateModel(Model m, Random rng) {
		Track track = m.getGeneratorConfiguration().TRACK;
		// Unconstrained tracks do not support this mutation
		if (track == Track.MCA || track == Track.UNIFORM_ALL || track == Track.UNIFORM_BOOLEAN) {
			return m;
		}

		// Check the probability
		if (rng.nextFloat(0, 1) < probability)
			return m;

		// Constrained tracks
		Model mTemp = changeOperator(m, rng, " OR ", " AND ");

		return mTemp;
	}

}
