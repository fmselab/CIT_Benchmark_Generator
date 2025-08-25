package util.genetics.problems;

import generators.GeneratorConfiguration;
import util.genetics.solution.ModelSolution;

public class ModelTestRatioProblem extends ModelProblem {

	private static final long serialVersionUID = 1L;
	private final double targetRatio;

	public ModelTestRatioProblem(double targetRatio, GeneratorConfiguration config) {
		super(config);
		this.targetRatio = targetRatio;
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
		return "ModelTestRatioProblem";
	}

	@Override
	public ModelSolution evaluate(ModelSolution solution) {
		double fitness = getTestFitness(solution, targetRatio);
		setObjectives(solution, fitness);
		return solution;
	}

}
