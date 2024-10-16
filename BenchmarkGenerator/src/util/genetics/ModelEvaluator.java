//=============================================================================
package util.genetics;

import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import models.Model;

/**
 * Evaluates models and assigns a fitness score based on the distance between
 * its test validity ratio and the target one
 * 
 * @author Andrea Bombarda
 */
public class ModelEvaluator implements FitnessEvaluator<Model> {
	private final double targetRatio;

	/**
	 * Creates a {@link FitnessEvaluator} that calculates scores for Models based on
	 * how their ratio is close to a target ratio.
	 * 
	 * @param targetRatio The target of the evolution.
	 */
	public ModelEvaluator(double targetRatio) {
		this.targetRatio = targetRatio;
	}

	/**
	 * Assigns one "penalty point" depending on the distance of the model ratio from
	 * the target one.
	 * 
	 * If the computation of the test validity ratio fails, it returns 1 which is
	 * the maximum value reachable by the fitness
	 * 
	 * @param candidate  The evolved model to evaluate.
	 * @param population {@inheritDoc}
	 * @return The fitness score
	 */
	@Override
	public double getFitness(Model candidate, List<? extends Model> population) {
		try {
			if (!candidate.isSolvable())
				return 1;
			double distance = candidate.getApproximateTestValidityRatio() - targetRatio;
			if (distance < 0)
				return 0;
			return distance;
		} catch (Exception e) {
			return 1;
		}
	}

	@Override
	public boolean isNatural() {
		return false;
	}
}