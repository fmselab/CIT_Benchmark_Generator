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
import util.genetics.mutations.ConstraintRemoverMutation;
import util.genetics.mutations.ParameterAdderMutation;
import util.genetics.mutations.ConstraintSubstitutionMutation;

/**
 * Simple evolutionary algorithm that evolves a population of randomly-generated
 * strings until at least one matches a specified target string.
 * 
 * @author Daniel Dyer
 */
public final class SBModelRatioGenerator {

	/**
	 * Entry point for the sample application. Any data specified on the command
	 * line is considered to be the target String. If no target is specified, a
	 * default of "HELLOW WORLD" is used instead.
	 * 
	 * @param args The target String (as an array of words).
	 * @throws IOException
	 * @throws InvalidConfigurationException
	 * @throws InterruptedException
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

		List<EvolutionaryOperator<Model>> operators = new ArrayList<EvolutionaryOperator<Model>>(2);
		operators.add(new ParameterAdderMutation(0.3f));
		operators.add(new ConstraintAdderMutation(0.2f));
		operators.add(new ConstraintRemoverMutation(0.4f));
		operators.add(new ConstraintSubstitutionMutation(0.4f));
		// operators.add(new ParameterRemoverMutation());
		EvolutionaryOperator<Model> pipeline = new EvolutionPipeline<Model>(operators);

		EvolutionEngine<Model> engine = new GenerationalEvolutionEngine<Model>(factory, pipeline,
				new ModelEvaluator(config.RATIO_TEST), new RouletteWheelSelection(), new MersenneTwisterRNG());

		engine.addEvolutionObserver(new EvolutionLogger());
		return engine.evolve(50, // 100 individuals in the population.
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