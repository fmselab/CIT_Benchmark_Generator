package util.genetics.problems;

import generators.GeneratorConfiguration;
import util.genetics.solution.ModelSolution;

public class ModelRatioProblem extends ModelProblem {

	private static final long serialVersionUID = 1L;
	private final double targetTupleRatio;
	private final double targetTestRatio;

	public ModelRatioProblem(double targetTupleRatio, double targetTestRatio, GeneratorConfiguration config) {
		super(config);
		this.targetTupleRatio = targetTupleRatio;
		this.targetTestRatio = targetTestRatio;
	}

	@Override
	public int numberOfVariables() {
		return 2;
	}

	@Override
	public int numberOfObjectives() {
		return 2;
	}

	@Override
	public String name() {
		return "ModelRatioProblem";
	}

	@Override
	public ModelSolution evaluate(ModelSolution solution) {
		double fitnessTest = getTestFitness(solution, targetTestRatio);
		double fitnessTuple = getTupleFitness(solution, targetTupleRatio);
		setObjectives(solution, fitnessTest, fitnessTuple);
		return solution;
	}

}
