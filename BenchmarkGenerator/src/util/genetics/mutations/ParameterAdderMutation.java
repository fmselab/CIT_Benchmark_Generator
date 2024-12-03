package util.genetics.mutations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import models.Model;

/**
 * Mutation that adds a parameter to the model
 */
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

	/**
	 * Apply the mutation to the selected candidates
	 * 
	 * @param selectedCandidates the selected candidates
	 * @param rng                the random number generator
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
	 * Mutate the model by adding a parameter
	 * 
	 * @param m   the model to mutate
	 * @param rng the random number generator
	 * @return the mutated model
	 */
	public Model mutateModel(Model m, Random rng) {
		Model mTemp = (Model) m.clone();

		// Check the probability
		if (rng.nextFloat(0, 1) > probability)
			return m;

		if (m.getParameters().size() < mTemp.getGeneratorConfiguration().N_PARAMS_MAX) {
			System.out.println("****** Adding a parameter");
			mTemp.addNewRandomParameter((ArrayList<String>) mTemp.getParameters().stream().map(x -> x.getName())
					.collect(Collectors.toList()));
		}
		return mTemp;
	}

}
