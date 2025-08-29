package util.genetics.mutations;

import java.util.Random;

import org.uma.jmetal.operator.mutation.MutationOperator;

import ctwedge.util.ModelUtils;
import models.Model;
import util.genetics.solution.ModelSolution;

/**
 * Mutation that removes a parameter of the model
 */
public class ParameterRemoverMutation implements MutationOperator<ModelSolution> {

	private static final long serialVersionUID = 1L;
	/**
	 * The probability for applying the mutation
	 */
	private float probability;

	/**
	 * Builds a new ParameterRemoverMutation object
	 * 
	 * @param p the probability for applying the mutation
	 */
	public ParameterRemoverMutation(float p) {
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
	 * Mutate the model by removing a value from a parameter
	 * 
	 * @param m the model to mutate *
	 * @return the mutated model
	 */
	public Model mutateModel(Model m) {
		Model mTemp = (Model) m.clone();
		Random rng = new Random();

		// Check the probability
		if (rng.nextFloat(0, 1) > probability)
			return m;

		// Index of the parameter to be removed
		int index = rng.nextInt(0, m.getParameters().size());

		// Check that the parameter is not used in constraints
		String paramName = mTemp.getParameters().get(index).getName();
		ModelUtils utils = new ModelUtils(m);
		boolean found = false;
		String modelConstraintsAsString = utils.serializeToString().split(" Constraints :")[1];
		if (modelConstraintsAsString.contains(" " + paramName + " "))
				found = true;


		// If the parameter is used in constraints, do nothing, otherwise remove the
		// parameter
		if (!found) {
			System.out.println("****** Removing the parameter " + mTemp.getParameters().get(index).getName());
			mTemp.removeParameter(mTemp.getParameters().get(index));
		}

		return mTemp;
	}

}
