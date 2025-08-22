package util.genetics.mutations;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Random;

import org.junit.Test;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.api.SolverException;

import ctwedge.util.ParameterSize;
import generators.GeneratorConfiguration;
import generators.Track;
import main.BenchmarkGeneratorCLI;
import models.Model;

public class TestParameterExtender {
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
		Random rng = new Random();
		ParameterExtenderMutation pam = new ParameterExtenderMutation(1.0f);
		Model mTemp = pam.mutateModel(m, rng);
		int bigger = 0;
		for (int i = 0; i < m.getParameters().size(); i++) {
			// Names of the parameters must be the same
			assertEquals(m.getParameters().get(i).getName(), mTemp.getParameters().get(i).getName());
			// Sizes of the parameters must be the same except for one parameter
			int oldParameterSize = ParameterSize.eInstance.doSwitch(m.getParameters().get(i));
			int newParameterSize = ParameterSize.eInstance.doSwitch(mTemp.getParameters().get(i));
			if (newParameterSize > oldParameterSize)
				bigger++;
		}
		assertEquals(1, bigger);
	}

	
	@Test
	public void test_mutateModel_BOOLC()
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
		config.TRACK = Track.BOOLC;

		BenchmarkGeneratorCLI generator = new BenchmarkGeneratorCLI();
		do {
			generator.generateIPMs(config);
		} while (generator.getModelsList().size() == 0);
		m = generator.getModelsList().get(0);
		Random rng = new Random();
		ParameterExtenderMutation pam = new ParameterExtenderMutation(1.0f);
		Model mTemp = pam.mutateModel(m, rng);
		for (int i = 0; i < m.getParameters().size(); i++) {
			// Names of the parameters must be the same
			assertEquals(m.getParameters().get(i).getName(), mTemp.getParameters().get(i).getName());
			// Sizes of the parameters must be the same
			int oldParameterSize = ParameterSize.eInstance.doSwitch(m.getParameters().get(i));
			int newParameterSize = ParameterSize.eInstance.doSwitch(mTemp.getParameters().get(i));
			assertEquals(oldParameterSize, newParameterSize);
		}
	}
	
	@Test
	public void test_mutateModel_NUMC()
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
		config.TRACK = Track.NUMC;

		BenchmarkGeneratorCLI generator = new BenchmarkGeneratorCLI();
		do {
			generator.generateIPMs(config);
		} while (generator.getModelsList().size() == 0);
		m = generator.getModelsList().get(0);
		System.out.println(m.toString());
		Random rng = new Random();
		ParameterExtenderMutation pam = new ParameterExtenderMutation(1.0f);
		Model mTemp = pam.mutateModel(m, rng);
		int bigger = 0;
		for (int i = 0; i < m.getParameters().size(); i++) {
			// Names of the parameters must be the same
			assertEquals(m.getParameters().get(i).getName(), mTemp.getParameters().get(i).getName());
			// Sizes of the parameters must be the same except for one parameter
			int oldParameterSize = ParameterSize.eInstance.doSwitch(m.getParameters().get(i));
			int newParameterSize = ParameterSize.eInstance.doSwitch(mTemp.getParameters().get(i));
			if (newParameterSize > oldParameterSize)
				bigger++;
		}
		assertEquals(1, bigger);
	}
}
