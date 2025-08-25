package util.genetics.mutations;

import java.util.List;
import java.util.Random;

import org.uma.jmetal.operator.mutation.MutationOperator;

import util.genetics.solution.ModelSolution;

public class CompositeMutation implements MutationOperator<ModelSolution> {
	
    private static final long serialVersionUID = 1L;
	private final List<MutationOperator<ModelSolution>> mutations;
    private final Random random = new Random();

    public CompositeMutation(List<MutationOperator<ModelSolution>> mutations) {
        this.mutations = mutations;
    }

    @Override
    public ModelSolution execute(ModelSolution solution) {
        // Pick one mutation randomly
        int idx = random.nextInt(mutations.size());
        return mutations.get(idx).execute(solution);
    }

	@Override
	public double mutationProbability() {
		return 1;
	}
}
