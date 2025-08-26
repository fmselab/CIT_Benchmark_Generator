package util.genetics.algorithm;

import java.util.Comparator;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import util.genetics.solution.ModelSolution;

public class NSGAIITestRatio extends NSGAII<ModelSolution> {

	private static final long serialVersionUID = 1L;

	// How much time to run iterations for
	private double timeout;
	// Time at which we started the algorithm
	private long initTime;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public NSGAIITestRatio(Problem<ModelSolution> problem, int maxEvaluations, int populationSize, int matingPoolSize,
			int offspringPopulationSize, CrossoverOperator<ModelSolution> crossoverOperator,
			MutationOperator<ModelSolution> mutationOperator, SelectionOperator selectionOperator,
			Comparator dominanceComparator, SolutionListEvaluator evaluator, double timeout) {
		super(problem, maxEvaluations, populationSize, matingPoolSize, offspringPopulationSize, crossoverOperator,
				mutationOperator, selectionOperator, dominanceComparator, evaluator);
		this.timeout = timeout;
	}

	/**
	 * The stopping condition is reached when either the maximum number of
	 * evaluations is reached, the timeout is reached, or at least one solution is
	 * found with fitness close to 0, i.e., it is with the desired test ratio
	 */
	@Override
	protected boolean isStoppingConditionReached() {

		long now = System.currentTimeMillis();
		long elapsedTime = now - initTime;

		boolean stoppingConditionReached = super.isStoppingConditionReached();
		boolean timedOut = elapsedTime > timeout;
		boolean isOneSolutionWithCorrectTestRatio = false;

		for (ModelSolution s : this.population) {
			try {
				double ratioDiff = Math
						.abs(s.getModel().getTestValidityRatio() - s.getModel().getGeneratorConfiguration().RATIO_TEST);
				if (ratioDiff <= s.getModel().getGeneratorConfiguration().EPSILON) {
					isOneSolutionWithCorrectTestRatio = true;
					break;
				}
			} catch (Exception e) { }
		}
		boolean condition = stoppingConditionReached || timedOut || (isOneSolutionWithCorrectTestRatio);

		return condition;
	}

	/**
	 * The main loop of the Evolutionary Algorithm. Here we just additionally keep
	 * track of the starting time.
	 */
	@Override
	public void run() {
		initTime = System.currentTimeMillis();
		super.run();
	}

}
