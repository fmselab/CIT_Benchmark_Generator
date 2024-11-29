package util.genetics.mutations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import models.Model;

public class ParameterAdderMutation implements EvolutionaryOperator<Model> {

	/**
	 * The probability for applying the mutation
	 */
	private float probability;

	/**
	 * Builds a new ParameterAdderMutation object
	 * 
	 * @param p the probability for applying the mutation
	 */
	public ParameterAdderMutation(float p) {
		this.probability = p;
	}

	@Override
	public List<Model> apply(List<Model> selectedCandidates, Random rng) {

		List<Model> mutatedPopulation = new ArrayList<Model>(selectedCandidates.size());
		for (Model m : selectedCandidates) {
			mutatedPopulation.add(mutateModel(m, rng));
		}
		return mutatedPopulation;

	}

	private Model mutateModel(Model m, Random rng) {
		Model mTemp = m;

		// Check the probability
		if (rng.nextFloat(0, 1) < probability)
			return m;

		if (m.getParameters().size() < m.getGeneratorConfiguration().N_PARAMS_MAX) {
			int nParams = rng.nextInt(0, m.getGeneratorConfiguration().N_PARAMS_MAX - m.getParameters().size());
			System.out.println("****** Adding " + nParams + " parameters");
			for (int i = 0; i < nParams; i++)
				mTemp.addNewRandomParameter((ArrayList<String>) m.getParameters().stream().map(x -> x.getName())
						.collect(Collectors.toList()));
		}
		return mTemp;
	}

}
