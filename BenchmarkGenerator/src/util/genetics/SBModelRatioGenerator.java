package util.genetics;

import java.io.IOException;
import java.util.List;

import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;

import generators.GeneratorConfiguration;
import generators.Track;
import models.Model;
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
import util.genetics.problems.ModelProblem;
import util.genetics.problems.ModelTestRatioProblem;
import util.genetics.solution.ModelSolution;

public final class SBModelRatioGenerator {

	public static Model evolveModel(GeneratorConfiguration config, ModelProblem problem) {

		MutationOperator<ModelSolution> mutation = new CompositeMutation(List.of(
				new ParameterAdderMutation(config.PROBABILITY_PARADD),
				new ConstraintAdderMutation(config.PROBABILITY_CNSTRADD),
				new ConstraintRemoverMutation(config.PROBABILITY_CNSTRDEL),
				new ConstraintSubstitutionMutation(config.PROBABILITY_CNSTRSUBST),
				new ConstraintAndToOrMutation(config.PROBABILITY_ANDTOOR),
				new ConstraintOrToAndMutation(config.PROBABILITY_ORTOAND),
				new ConstraintImpliesToDblImpliesMutation(config.PROBABILITY_IMPLTODBL),
				new ConstraintDblImpliesToImpliesMutation(config.PROBABILITY_DBLTOIMPL),
				new ConstraintNotRemoverMutation(config.PROBABILITY_NOTDEL),
				new ConstraintToNotMutation(config.PROBABILITY_NOTADD),
				new ParameterExtenderMutation(config.PROBABILITY_PAREXT)
			));

		CrossoverOperator<ModelSolution> crossover = new ModelCrossover();
		
		int populationSize = 100;
		int maxEvaluations = 50;
		NSGAII<ModelSolution> algorithm =
			    new NSGAIIBuilder<>(problem, crossover, mutation, populationSize)
			        .setMaxEvaluations(maxEvaluations)
			        .setOffspringPopulationSize(1)
			        .build();		
		
		@SuppressWarnings("unused")
		AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();
		List<ModelSolution> population = algorithm.result();
		return population.get(0).getModel();

	}

	public static void main(String[] args) throws InterruptedException, InvalidConfigurationException, IOException {
		GeneratorConfiguration config = new GeneratorConfiguration();
		config.CHECK_TEST_RATIO = true;
		config.CHECK_TUPLE_RATIO = false;
		config.N_PARAMS_MIN = 2;
		config.RATIO = 0.20;
		config.RATIO_TEST = 0.20;
		config.P = 0.90;
		config.N_PARAMS_MAX = 30;
		config.TRACK = Track.BOOLC;
		ModelProblem problem = new ModelTestRatioProblem(config.RATIO, config);
		Model evolvedModel = evolveModel(config, problem);
		System.out.println(evolvedModel);
		System.out.println(evolvedModel.getTestValidityRatio());
		System.out.println(evolvedModel.isSolvable());
	}
}