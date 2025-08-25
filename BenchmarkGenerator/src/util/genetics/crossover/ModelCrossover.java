package util.genetics.crossover;

import java.util.List;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
import util.genetics.solution.ModelSolution;

public class ModelCrossover implements CrossoverOperator<ModelSolution>{

	private static final long serialVersionUID = 1L;

	@Override
	public List<ModelSolution> execute(List<ModelSolution> source) {
		return source;
	}

	@Override
	public double crossoverProbability() {
		return 0;
	}

	@Override
	public int numberOfRequiredParents() {
		return 1;
	}

	@Override
	public int numberOfGeneratedChildren() {
		return 1;
	}

}
