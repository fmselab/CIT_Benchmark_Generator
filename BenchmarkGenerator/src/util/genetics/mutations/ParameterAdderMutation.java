package util.genetics.mutations;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

import org.uma.jmetal.operator.mutation.MutationOperator;

import models.Model;
import util.genetics.solution.ModelSolution;

/**
 * Mutation that adds a parameter to the model
 */
public class ParameterAdderMutation implements MutationOperator<ModelSolution> {

	private static final long serialVersionUID = 1L;
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
	 * Mutate the model by adding a parameter
	 * 
	 * @param m the model to mutate	 * 
	 * @return the mutated model
	 */
	public Model mutateModel(Model m) {
		Model mTemp = (Model) m.clone();
		Random rng = new Random();

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
