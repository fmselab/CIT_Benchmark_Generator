import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.api.SolverException;

import generators.GeneratorConfiguration;
import generators.Track;
import main.BenchmarkGeneratorCLI;
import util.genetics.ModelSolvabilityEvaluator;

public class IWCT2025Experiments_ComparisonSearchNoSearch {

	BenchmarkGeneratorCLI generator = new BenchmarkGeneratorCLI();
	static int REPETITIONS = 10;
	static String OUTPUT_FILE = "Experiments.csv";

	@Test
	public void test_BOOLC_solvable()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		GeneratorConfiguration config = new GeneratorConfiguration();
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("BOOLC_" + OUTPUT_FILE)));
		config.TRACK = Track.BOOLC;
		config.N_BENCHMARKS = 50;
		// Using k in the range [6, 30]
		config.N_PARAMS_MAX = 30;
		config.N_PARAMS_MIN = 6;
		// Using c in the range [1, 100]
		config.N_CONSTRAINTS_MIN = 1;
		config.N_CONSTRAINTS_MAX = 100;
		// Using d in the range [1, 20]
		config.MIN_CONSTRAINTS_COMPLEXITY = 1;
		config.MAX_CONSTRAINTS_COMPLEXITY = 20;
		// Do not export models as files
		config.ALWAYS_EXPORT = false;
		config.N_ATTEMPTS = 10;

		for (int i = 0; i < REPETITIONS; i++) {
			config.USE_SEARCH = false;

			long timeOriginalApproach;
			long timeSearchBasedApproach;
			long end;
			long start = System.currentTimeMillis();
			generator.generateIPMs(config);
			end = System.currentTimeMillis();
			int originalApproach = generator.getModelsList().size();
			timeOriginalApproach = end - start;
			config.USE_SEARCH = true;
			config.PROBABILITY_MUTATION = 0.5f;
			config.FITNESS = new ModelSolvabilityEvaluator();
			generator.clearModelsList();
			start = System.currentTimeMillis();
			generator.generateIPMs(config);
			end = System.currentTimeMillis();
			timeSearchBasedApproach = end - start;
			int searchBasedApproach = generator.getModelsList().size();
			writer.append(config.TRACK.name() + ";SOLVABILITY;" + originalApproach + ";" +  timeOriginalApproach 
					+ ";" +	searchBasedApproach + ";" + timeSearchBasedApproach + ";\n");
		}

		writer.close();

	}

	@Test
	public void test_MCAC_solvable() throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		GeneratorConfiguration config = new GeneratorConfiguration();
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("MCAC_" + OUTPUT_FILE)));
		config.TRACK = Track.MCAC;
		config.N_BENCHMARKS = 50;
		// Using k in the range [6, 30]
		config.N_PARAMS_MAX = 30;
		config.N_PARAMS_MIN = 6;
		// Using v in the range [2, 15]
		config.MIN_CARDINALITY = 2;
		config.MAX_CARDINALITY = 15;
		// Using c in the range [1, 100]
		config.N_CONSTRAINTS_MIN = 1;
		config.N_CONSTRAINTS_MAX = 100;
		// Using d in the range [1, 20]
		config.MIN_CONSTRAINTS_COMPLEXITY = 1;
		config.MAX_CONSTRAINTS_COMPLEXITY = 20;
		// Do not export models as files
		config.ALWAYS_EXPORT = false;
		config.N_ATTEMPTS = 10;

		for (int i = 0; i < REPETITIONS; i++) {
			config.USE_SEARCH = false;

			long timeOriginalApproach;
			long timeSearchBasedApproach;
			long end;
			long start = System.currentTimeMillis();
			generator.generateIPMs(config);
			end = System.currentTimeMillis();
			int originalApproach = generator.getModelsList().size();
			timeOriginalApproach = end - start;
			config.USE_SEARCH = true;
			config.PROBABILITY_MUTATION = 0.5f;
			config.FITNESS = new ModelSolvabilityEvaluator();
			generator.clearModelsList();
			start = System.currentTimeMillis();
			generator.generateIPMs(config);
			end = System.currentTimeMillis();
			timeSearchBasedApproach = end - start;
			int searchBasedApproach = generator.getModelsList().size();
			writer.append(config.TRACK.name() + ";SOLVABILITY;" + originalApproach + ";" +  timeOriginalApproach 
					+ ";" +	searchBasedApproach + ";" + timeSearchBasedApproach + ";\n");
		}

		writer.close();
	}

	@Test
	public void test_NUMC_solvable() throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		GeneratorConfiguration config = new GeneratorConfiguration();
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("NUMC_" + OUTPUT_FILE)));
		config.TRACK = Track.NUMC;
		config.N_BENCHMARKS = 50;
		// Using k in the range [6, 30]
		config.N_PARAMS_MAX = 30;
		config.N_PARAMS_MIN = 6;
		// Using v in the range [2, 15]
		config.MIN_CARDINALITY = 2;
		config.MAX_CARDINALITY = 15;
		// Using c in the range [1, 100]
		config.N_CONSTRAINTS_MIN = 1;
		config.N_CONSTRAINTS_MAX = 100;
		// Using d in the range [1, 20]
		config.MIN_CONSTRAINTS_COMPLEXITY = 1;
		config.MAX_CONSTRAINTS_COMPLEXITY = 20;
		// Do not export models as files
		config.ALWAYS_EXPORT = false;
		config.N_ATTEMPTS = 10;

		for (int i = 0; i < REPETITIONS; i++) {
			config.USE_SEARCH = false;

			long timeOriginalApproach;
			long timeSearchBasedApproach;
			long end;
			long start = System.currentTimeMillis();
			generator.generateIPMs(config);
			end = System.currentTimeMillis();
			int originalApproach = generator.getModelsList().size();
			timeOriginalApproach = end - start;
			config.USE_SEARCH = true;
			config.PROBABILITY_MUTATION = 0.5f;
			config.FITNESS = new ModelSolvabilityEvaluator();
			generator.clearModelsList();
			start = System.currentTimeMillis();
			generator.generateIPMs(config);
			end = System.currentTimeMillis();
			timeSearchBasedApproach = end - start;
			int searchBasedApproach = generator.getModelsList().size();
			writer.append(config.TRACK.name() + ";SOLVABILITY;" + originalApproach + ";" +  timeOriginalApproach 
					+ ";" +	searchBasedApproach + ";" + timeSearchBasedApproach + ";\n");
		}

		writer.close();

	}
}
