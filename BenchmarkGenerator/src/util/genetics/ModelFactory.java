package util.genetics;

import java.util.Random;

import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

import generators.GeneratorConfiguration;
import main.BenchmarkGeneratorCLI;
import models.Model;

public class ModelFactory extends AbstractCandidateFactory<Model> {

	GeneratorConfiguration config;

	public void setGeneratorConfiguration(GeneratorConfiguration config) {
		this.config = config;
	}

	@Override
	public Model generateRandomCandidate(Random rng) {
		BenchmarkGeneratorCLI cliGenerator = new BenchmarkGeneratorCLI();
		Model m = null;
		try {
			int oldNBenchmarks = this.config.N_BENCHMARKS;
			this.config.CHECK_TEST_RATIO = false;
			this.config.N_BENCHMARKS = 1;
			do {
				cliGenerator.generateIPMs(this.config);
			} while (cliGenerator.getModelsList().size() == 0);
			this.config.N_BENCHMARKS = oldNBenchmarks;
			System.out.println("*******************" + cliGenerator.getModelsList().size());
			m = cliGenerator.getModelsList().get(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error in generating the model " + e.getMessage());
		}
		return m;
	}

}
