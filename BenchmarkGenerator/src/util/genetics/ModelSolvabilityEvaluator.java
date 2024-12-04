//=============================================================================
package util.genetics;

import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import models.Model;

/**
 * Evaluates models and assigns a fitness score based on its solvability
 * @author Andrea Bombarda
 */
public class ModelSolvabilityEvaluator implements FitnessEvaluator<Model> {

	/**
	 * Creates a {@link ModelSolvabilityEvaluator} that calculates scores for Models based on
	 * their solvability
	 * 
	 * @param targetRatio The target of the evolution.
	 */
	public ModelSolvabilityEvaluator() {
	}

	/**
	 * Assigns one "penalty point" depending on the model solvability
	 * 
	 * 
	 * @param candidate  The evolved model to evaluate.
	 * @param population {@inheritDoc}
	 * @return The fitness score
	 */
	@Override
	public double getFitness(Model candidate, List<? extends Model> population) {
		try {
			if (!candidate.isSolvable()) {
				double unsatCoreSize = candidate.getUnsatCoreSize();
				System.out.println(unsatCoreSize);
				return unsatCoreSize;
			}
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}

	@Override
	public boolean isNatural() {
		return false;
	}
}