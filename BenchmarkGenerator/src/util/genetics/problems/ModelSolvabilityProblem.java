package util.genetics.problems;

import generators.GeneratorConfiguration;
import util.genetics.solution.ModelSolution;

public class ModelSolvabilityProblem extends ModelProblem {
	
	private static final long serialVersionUID = 1L;
	
	public ModelSolvabilityProblem(GeneratorConfiguration config) {
		super(config);
	}

	@Override
	public int numberOfVariables() {
		return 1;
	}

	@Override
	public int numberOfObjectives() {
		return 1;
	}

	@Override
	public String name() {
		return "ModelSolvabilityProblem";
	}

	@Override
	public ModelSolution evaluate(ModelSolution solution) {
		double fitness = getFitness(solution);
		setObjectives(solution, fitness);
		return solution;
	}
	
	/**
	 * Assigns one "penalty point" depending on the model solvability
	 * 
	 * 
	 * @param candidate  The evolved model to evaluate.
	 * @param population {@inheritDoc}
	 * @return The fitness score
	 */
	public double getFitness(ModelSolution candidate) {
		try {
			if (!candidate.getModel().isSolvable()) {
				double unsatCoreSize = candidate.getModel().getUnsatCoreSize();
				System.out.println(unsatCoreSize);
				return unsatCoreSize;
			}
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}
}
