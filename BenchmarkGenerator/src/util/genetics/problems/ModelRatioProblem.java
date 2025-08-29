package util.genetics.problems;

import java.util.List;

import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import generators.GeneratorConfiguration;
import util.genetics.algorithm.NSGAIIRatio;
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

public class ModelRatioProblem extends ModelProblem {

	private static final long serialVersionUID = 1L;
	private final double targetTupleRatio;
	private final double targetTestRatio;

	public ModelRatioProblem(double targetTupleRatio, double targetTestRatio, GeneratorConfiguration config) {
		super(config, 2, 2);
		this.targetTupleRatio = targetTupleRatio;
		this.targetTestRatio = targetTestRatio;

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
				new ParameterRemoverMutation(config.PROBABILITY_PARREM))));

		// Set the algorithm to use in this problem
		this.algorithm = new NSGAIIRatio(this, config.MAX_EVALUATIONS, config.POPULATION_SIZE,
				config.MATING_POOL_SIZE, config.OFFSPRING_SIZE, new ModelCrossover(), this.getMutation(),
				new BinaryTournamentSelection<>(), new ModelDominanceComparator(),
				new SequentialSolutionListEvaluator<>(), config.TIMEOUT);
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
