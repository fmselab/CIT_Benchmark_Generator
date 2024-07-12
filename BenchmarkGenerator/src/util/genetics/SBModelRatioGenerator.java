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

import models.Model;

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
	public Model mutate(double targetRatio) throws InterruptedException, InvalidConfigurationException, IOException {
		Model result = evolveModel(targetRatio);
		System.out.println("Evolution result: " + result.getTestValidityRatio());
		return result;
	}

	public static Model evolveModel(double targetRatio) {
		
		ModelFactory factory = new ModelFactory();
	
		List<EvolutionaryOperator<Model>> operators = new ArrayList<EvolutionaryOperator<Model>>(2);
		operators.add(new ParameterAdderMutation());
		operators.add(new ConstraintAdderMutation());
		EvolutionaryOperator<Model> pipeline = new EvolutionPipeline<Model>(operators);
		
		
		
		EvolutionEngine<Model> engine = new GenerationalEvolutionEngine<Model>(factory, pipeline,
				new ModelEvaluator(targetRatio), new RouletteWheelSelection(), new MersenneTwisterRNG());	
		
		engine.addEvolutionObserver(new EvolutionLogger());
		return engine.evolve(10, 				// 100 individuals in the population.
				5, 								// 5% elitism.
				new TargetFitness(targetRatio, true));
		
	}
	
	public static void main(String[] args) {
		System.out.println(evolveModel(0.3));
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