package util.genetics.problems;

import java.util.List;

import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.operator.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import generators.GeneratorConfiguration;
import util.genetics.algorithm.NSGAIISolvability;
import util.genetics.comparator.ModelDominanceComparator;
import util.genetics.crossover.ModelCrossover;
import util.genetics.mutations.CompositeMutation;
import util.genetics.mutations.ConstraintAdderMutation;
import util.genetics.mutations.ConstraintAndToOrMutation;
import util.genetics.mutations.ConstraintDblImpliesToImpliesMutation;
import util.genetics.mutations.ConstraintImpliesToDblImpliesMutation;
import util.genetics.mutations.ConstraintNotRemoverMutation;
import util.genetics.mutations.ConstraintOrToAndMutation;
import util.genetics.mutations.ConstraintRemoverMutation;
import util.genetics.mutations.ConstraintSubstitutionMutation;
import util.genetics.mutations.ConstraintToNotMutation;
import util.genetics.mutations.ParameterAdderMutation;
import util.genetics.mutations.ParameterExtenderMutation;
import util.genetics.mutations.ParameterRemoverMutation;
import util.genetics.mutations.ParameterShrinkerMutation;
import util.genetics.solution.ModelSolution;

public class ModelSolvabilityProblem extends ModelProblem {

	private static final long serialVersionUID = 1L;

	public ModelSolvabilityProblem(GeneratorConfiguration config) {
		super(config, 1, 1);

		// Set the mutations to use
		this.setMutation(new CompositeMutation(List.of(new ParameterAdderMutation(config.PROBABILITY_PARADD),
				new ConstraintAdderMutation(config.PROBABILITY_CNSTRADD),
				new ConstraintRemoverMutation(config.PROBABILITY_CNSTRDEL),
				new ConstraintSubstitutionMutation(config.PROBABILITY_CNSTRSUBST),
				new ConstraintAndToOrMutation(config.PROBABILITY_ANDTOOR),
				new ConstraintOrToAndMutation(config.PROBABILITY_ORTOAND),
				new ConstraintImpliesToDblImpliesMutation(config.PROBABILITY_IMPLTODBL),
				new ConstraintDblImpliesToImpliesMutation(config.PROBABILITY_DBLTOIMPL),
				new ConstraintNotRemoverMutation(config.PROBABILITY_NOTDEL),
				new ConstraintToNotMutation(config.PROBABILITY_NOTADD),
				new ParameterExtenderMutation(config.PROBABILITY_PAREXT),
				new ParameterShrinkerMutation(config.PROBABILITY_PARSHR),
				new ParameterRemoverMutation(config.PROBABILITY_PARREM)), this));

		// Set the algorithm to use in this problem
		this.algorithm = new NSGAIISolvability(this, config.MAX_EVALUATIONS, config.POPULATION_SIZE,
				config.MATING_POOL_SIZE, config.OFFSPRING_SIZE, new ModelCrossover(), this.getMutation(),
				new NaryTournamentSelection<>(config.MATING_POOL_SIZE, new ModelDominanceComparator()),
				new ModelDominanceComparator(), new SequentialSolutionListEvaluator<>(), config.TIMEOUT);
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
			System.out.println("----- Model is solvable -----");
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return candidate.getModel().getConstraints().size()
					* candidate.getModel().getGeneratorConfiguration().MAX_CONSTRAINTS_COMPLEXITY;
		}
	}
}
