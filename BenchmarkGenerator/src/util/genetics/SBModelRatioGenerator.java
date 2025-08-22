package util.genetics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.TargetFitness;

import generators.GeneratorConfiguration;
import generators.Track;
import models.Model;
import util.genetics.mutations.ConstraintAdderMutation;
import util.genetics.mutations.ConstraintAndToOrMutation;
import util.genetics.mutations.ConstraintDblImpliesToImpliesMutation;
import util.genetics.mutations.ConstraintImpliesToDblImpliesMutation;
import util.genetics.mutations.ConstraintNotRemoverMutation;
import util.genetics.mutations.ConstraintOrToAndMutation;
import util.genetics.mutations.ConstraintRemoverMutation;
import util.genetics.mutations.ParameterAdderMutation;
import util.genetics.mutations.ParameterExtenderMutation;
import util.genetics.mutations.ConstraintSubstitutionMutation;
import util.genetics.mutations.ConstraintToNotMutation;

/**
 * Simple evolutionary algorithm that evolves a population of randomly-generated
 * strings until at least one matches a specified target string.
 * 
 * @author Daniel Dyer
 */
public final class SBModelRatioGenerator {

	/**
	 * The main method that starts the evolution process.
	 */
	public Model mutate(GeneratorConfiguration config)
			throws InterruptedException, InvalidConfigurationException, IOException {
		Model result = evolveModel(config);
		System.out.println("Evolution result: " + result.getTestValidityRatio());
		return result;
	}

	public static Model evolveModel(GeneratorConfiguration config) {

		ModelFactory factory = new ModelFactory();
		factory.setGeneratorConfiguration(config);

		List<EvolutionaryOperator<Model>> operators = new ArrayList<EvolutionaryOperator<Model>>();
		operators.add(new ParameterAdderMutation(0.3f));
		operators.add(new ConstraintAdderMutation(0.2f));
		operators.add(new ConstraintRemoverMutation(0.4f));
		operators.add(new ConstraintSubstitutionMutation(0.4f));
		operators.add(new ConstraintAndToOrMutation(0.4f));
		operators.add(new ConstraintOrToAndMutation(0.4f));
		operators.add(new ConstraintImpliesToDblImpliesMutation(0.4f));
		operators.add(new ConstraintDblImpliesToImpliesMutation(0.4f));
		operators.add(new ConstraintNotRemoverMutation(0.4f));
		operators.add(new ConstraintToNotMutation(0.4f));
		operators.add(new ParameterExtenderMutation(0.4f));
		EvolutionaryOperator<Model> pipeline = new EvolutionPipeline<Model>(operators);

		EvolutionEngine<Model> engine = new GenerationalEvolutionEngine<Model>(factory, pipeline,
				new ModelTupleRatioEvaluator(config.RATIO_TEST), new RouletteWheelSelection(), new MersenneTwisterRNG());

		engine.addEvolutionObserver(new EvolutionLogger());
		return engine.evolve(10, // 100 individuals in the population.
				5, // 5% elitism.
				new TargetFitness(0, false));

	}

	public static void main(String[] args) {
		GeneratorConfiguration config = new GeneratorConfiguration();
		config.CHECK_TEST_RATIO = true;
		config.CHECK_TUPLE_RATIO = false;
		config.RATIO_TEST = 0.5;
		config.N_PARAMS_MIN = 2;
		config.P = 0.90;
		config.N_PARAMS_MAX = 30;
		config.TRACK = Track.MCAC;
		System.out.println(evolveModel(config));
	}

	/**
	 * Trivial evolution observer for displaying information at the end of each
	 * generation.
	 */
	private static class EvolutionLogger implements EvolutionObserver<Model> {
		public void populationUpdate(PopulationData<? extends Model> data) {
			try {
				System.out.printf("Generation %d: %s\n", data.getGenerationNumber(),
						data.getBestCandidate().getApproximateTestValidityRatio());
			} catch (Exception e) {
				System.err.println("Error in computing the Test Validity ratio for the given model");
			}
		}
	}
}