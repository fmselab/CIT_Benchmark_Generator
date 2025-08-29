package util.genetics.mutations;
import java.io.IOException;

import org.junit.Test;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.api.SolverException;

import generators.GeneratorConfiguration;
import generators.Track;
import main.BenchmarkGeneratorCLI;
import models.Model;

public class TestParameterRemover {
	@Test
	public void test_mutateModel_MCAC()
			throws IOException, InvalidConfigurationException, SolverException, InterruptedException {
		GeneratorConfiguration config = new GeneratorConfiguration();
		Model m;
		// Using k in the range [6, 30]
		config.N_PARAMS_MAX = 30;
		config.N_PARAMS_MIN = 6;
		// Using c in the range [1, 100]
		config.N_CONSTRAINTS_MIN = 1;
		config.N_CONSTRAINTS_MAX = 100;
		// Using d in the range [1, 20]
		config.MIN_CONSTRAINTS_COMPLEXITY = 1;
		config.MAX_CONSTRAINTS_COMPLEXITY = 20;
		// Using v in the range [2, 15]
		config.MIN_CARDINALITY = 2;
		config.MAX_CARDINALITY = 15;
		// Do not export models as files
		config.ALWAYS_EXPORT = false;
		config.N_ATTEMPTS = 10;
		config.N_BENCHMARKS = 1;
		config.TRACK = Track.MCAC;

		BenchmarkGeneratorCLI generator = new BenchmarkGeneratorCLI();
		do {
			generator.generateIPMs(config);
		} while (generator.getModelsList().size() == 0);
		m = generator.getModelsList().get(0);
		ParameterRemoverMutation pam = new ParameterRemoverMutation(1.0f);
		Model mTemp = pam.mutateModel(m);
		assert(mTemp.getParameters().size() == m.getParameters().size() - 1);
	}
}
