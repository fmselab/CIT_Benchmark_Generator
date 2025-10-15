package util.genetics.algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import util.genetics.solution.ModelSolution;

public class NSGAIISolvability extends NSGAII<ModelSolution> {

	private static final long serialVersionUID = 1L;

	// How much time to run iterations for
	private double timeout;
	// Time at which we started the algorithm
	private long initTime;
	
	private int stagnationCounter = 0;
    private double bestSoFar = Double.POSITIVE_INFINITY;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public NSGAIISolvability(Problem<ModelSolution> problem, int maxEvaluations, int populationSize, int matingPoolSize,
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
	 * found with fitness 0, i.e., it is solvable.
	 */
	@Override
	protected boolean isStoppingConditionReached() {

		long now = System.currentTimeMillis();
		long elapsedTime = now - initTime;

		boolean stoppingConditionReached = super.isStoppingConditionReached();
		boolean timedOut = elapsedTime > timeout;
		boolean isOneSolutionOk = this.population.stream().anyMatch(s -> s.objectives()[0] == 0.0);
		boolean condition = stoppingConditionReached || timedOut || isOneSolutionOk;		

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
	
	@Override
    protected void updateProgress() {
        super.updateProgress();

        double best = result().stream()
                .mapToDouble(s -> s.objectives()[0])
                .min()
                .orElse(Double.POSITIVE_INFINITY);

        if (best < bestSoFar - 1e-7) {
            bestSoFar = best;
            stagnationCounter = 0;
        } else {
            stagnationCounter++;
        }

        if (stagnationCounter >= 40) {
            System.out.println("Stagnation detected, reinitializing population");
            List<ModelSolution> newPopulation = new ArrayList<>();
            for (int i = 0; i < getMaxPopulationSize(); i++) {
                newPopulation.add(getProblem().createSolution());
            }
            setPopulation(newPopulation);

            stagnationCounter = 0;
        }
    }

}
