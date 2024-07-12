package util.genetics;

import java.util.Random;

import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

import generators.GeneratorConfiguration;
import main.BenchmarkGeneratorCLI;
import models.Model;

public class ModelFactory extends AbstractCandidateFactory<Model>{

	@Override
	public Model generateRandomCandidate(Random rng) {
		BenchmarkGeneratorCLI cliGenerator = new BenchmarkGeneratorCLI();
		Model m = null;
		try {
			int oldNBenchmarks = GeneratorConfiguration.N_BENCHMARKS;
			GeneratorConfiguration.N_BENCHMARKS = 1;
			cliGenerator.generateIPMs();
			GeneratorConfiguration.N_BENCHMARKS = oldNBenchmarks;
			m = cliGenerator.getModelsList().get(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error in generating the model " + e.getMessage());
		}
		return m;
	}

}
