package util.genetics.problems;

import org.uma.jmetal.problem.Problem;

import generators.GeneratorConfiguration;
import main.BenchmarkGeneratorCLI;
import models.Model;
import util.genetics.solution.ModelSolution;

public abstract class ModelProblem implements Problem<ModelSolution> {

	private static final long serialVersionUID = 1L;
	GeneratorConfiguration config;

	public ModelProblem(GeneratorConfiguration config) {
		this.config = config;
	}

	@Override
	public int numberOfConstraints() {
		return 0;
	}

	@Override
	public ModelSolution createSolution() {
		BenchmarkGeneratorCLI cliGenerator = new BenchmarkGeneratorCLI();
		Model m = null;
		try {
			int oldNBenchmarks = this.config.N_BENCHMARKS;
			int nAttempts = this.config.N_ATTEMPTS;
			this.config.CHECK_TEST_RATIO = false;
			this.config.N_BENCHMARKS = 1;
			this.config.N_ATTEMPTS = 1;
			this.config.USE_SEARCH = false;
			this.config.CHECK_SOLVABLE = false;
			do {
				cliGenerator.generateIPMs(this.config);
			} while (cliGenerator.getModelsList().size() == 0);
			this.config.USE_SEARCH = true;
			this.config.CHECK_SOLVABLE = true;
			this.config.N_BENCHMARKS = oldNBenchmarks;
			this.config.N_ATTEMPTS = nAttempts;
			m = cliGenerator.getModelsList().get(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error in generating the model " + e.getMessage());
		}

		// Return the model solution
		ModelSolution solution = new ModelSolution(m, 1, 1);
		return solution;
	}

	/**
	 * Attributes the given fitness value(s) to the given solution. This effectively
	 * tells the search how good this solution is.
	 *
	 * @param solution   the solution
	 * @param objectives (vararg) the values to attribute.
	 */
	void setObjectives(ModelSolution solution, Double... objectives) {
		for (int i = 0; i < objectives.length; i++) {
			solution.objectives()[i] = objectives[i];
		}
	}

	/**
	 * Assigns one "penalty point" depending on the distance of the model ratio from
	 * the target one.
	 * 
	 * If the computation of the test validity ratio fails, it returns 1 which is
	 * the maximum value reachable by the fitness
	 * 
	 * @param candidate        The evolved model to evaluate.
	 * @param targetTupleRatio the target tuple ratio
	 * @return The fitness score
	 */
	public double getTupleFitness(ModelSolution candidate, double targetTupleRatio) {
		try {
			if (!candidate.getModel().isSolvable()) {
				double fitness = candidate.getModel().getUnsatCoreSize();
				System.out.println("Fitness: " + fitness);
				return fitness;
			}
			double ratio = candidate.getModel().getTupleValidityRatio();
			System.out.println("Tuple Ratio: " + ratio);
			double distance = Math.abs(ratio - targetTupleRatio);
			System.out.println("Fitness: " + distance);
			return distance;
		} catch (Exception e) {
			return 1;
		}
	}

	/**
	 * Assigns one "penalty point" depending on the distance of the model ratio from
	 * the target one.
	 * 
	 * If the computation of the test validity ratio fails, it returns 1 which is
	 * the maximum value reachable by the fitness
	 * 
	 * @param candidate       The evolved model to evaluate.
	 * @param targetTestRatio the target test ratio
	 * @return The fitness score
	 */
	public double getTestFitness(ModelSolution candidate, double targetTestRatio) {
		try {
			if (!candidate.getModel().isSolvable()) {
				double fitness = candidate.getModel().getUnsatCoreSize();
				System.out.println("Fitness: " + fitness);
				return fitness;
			}
			double ratio = candidate.getModel().getTestValidityRatio();
			System.out.println("Tuple Ratio: " + ratio);
			double distance = Math.abs(ratio - targetTestRatio);
			System.out.println("Fitness: " + distance);
			return distance;
		} catch (Exception e) {
			return 1;
		}
	}

}
