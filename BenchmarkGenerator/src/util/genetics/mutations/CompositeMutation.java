package util.genetics.mutations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;

import util.genetics.solution.ModelSolution;

public class CompositeMutation implements MutationOperator<ModelSolution> {

	private static final long serialVersionUID = 1L;
	private final List<MutationOperator<ModelSolution>> mutations;
	private final Random random = new Random();
	private Problem<ModelSolution> problem;

	public CompositeMutation(List<MutationOperator<ModelSolution>> mutations, Problem<ModelSolution> problem) {
		this.problem = problem;
		this.mutations = mutations;
	}

	@Override
	public ModelSolution execute(ModelSolution solution) {

		ArrayList<MutationOperator<ModelSolution>> mutationsList = new ArrayList<>(mutations);

		if (problem.evaluate(solution).objectives()[0] >= 1) {
			mutationsList.removeIf(x -> x.getClass().isInstance(ConstraintAdderMutation.class)
					|| x.getClass().isInstance(ParameterRemoverMutation.class) 
					|| x.getClass().isInstance(ParameterShrinkerMutation.class));
		}

		// Pick one mutation randomly
		int idx = random.nextInt(mutationsList.size());
		return mutationsList.get(idx).execute(solution);
	}

	@Override
	public double mutationProbability() {
		return 1;
	}
}
