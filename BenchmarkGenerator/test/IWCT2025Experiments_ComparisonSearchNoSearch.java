import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.api.SolverException;

import generators.GeneratorConfiguration;
import generators.Track;
import main.BenchmarkGeneratorCLI;
import util.genetics.ModelSolvabilityEvaluator;
import util.genetics.ModelTupleRatioEvaluator;

public class IWCT2025Experiments_ComparisonSearchNoSearch {

	BenchmarkGeneratorCLI generator = new BenchmarkGeneratorCLI();
	static int REPETITIONS = 10;
	static String OUTPUT_FILE = "Experiments.csv";
	GeneratorConfiguration config = new GeneratorConfiguration();

	@Before
	public void setUp() {
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
		// Using v in the range [2, 15]
		config.MIN_CARDINALITY = 2;
		config.MAX_CARDINALITY = 15;
		// Do not export models as files
		config.ALWAYS_EXPORT = false;
		config.N_ATTEMPTS = 10;
		// Probability of applying each genetic operator
		config.PROBABILITY_PARADD = 0.5f;
		config.PROBABILITY_PAREXT = 0.5f;
		config.PROBABILITY_CNSTRADD = 0.5f;
		config.PROBABILITY_CNSTRDEL = 0.5f;
		config.PROBABILITY_CNSTRSUBST = 0.5f;
		config.PROBABILITY_ANDTOOR = 0.5f;
		config.PROBABILITY_ORTOAND = 0.5f;
		config.PROBABILITY_IMPLTODBL = 0.5f;
		config.PROBABILITY_DBLTOIMPL = 0.5f;
		config.PROBABILITY_NOTADD = 0.5f;
		config.PROBABILITY_NOTDEL = 0.5f;
		// Ratio
		config.CHECK_TUPLE_RATIO = false;
		config.RATIO = 0.1;
	}

	@Test
	public void test_BOOLC_solvable()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("BOOLC_" + OUTPUT_FILE)));
		config.TRACK = Track.BOOLC;
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
			config.PROBABILITY_PAREXT = 0;
			config.PROBABILITY_CNSTRADD = 0;
			config.FITNESS = new ModelSolvabilityEvaluator();
			generator.clearModelsList();
			start = System.currentTimeMillis();
			generator.generateIPMs(config);
			end = System.currentTimeMillis();
			timeSearchBasedApproach = end - start;
			int searchBasedApproach = generator.getModelsList().size();
			writer.append(config.TRACK.name() + ";SOLVABILITY;" + originalApproach + ";" + timeOriginalApproach + ";"
					+ searchBasedApproach + ";" + timeSearchBasedApproach + ";\n");
			writer.flush();
		}

		writer.close();

	}

	@Test
	public void test_BOOLC_tupleRatio()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("Tuple_BOOLC_" + OUTPUT_FILE)));
		config.TRACK = Track.BOOLC;
		// Check ratio tuple
		config.CHECK_TUPLE_RATIO = true;

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
			config.PROBABILITY_PARADD = 0;
			config.PROBABILITY_PAREXT = 0;
			config.FITNESS = new ModelTupleRatioEvaluator(config.RATIO);
			generator.clearModelsList();
			start = System.currentTimeMillis();
			generator.generateIPMs(config);
			end = System.currentTimeMillis();
			timeSearchBasedApproach = end - start;
			int searchBasedApproach = generator.getModelsList().size();
			writer.append(config.TRACK.name() + ";TUPLERATIO;" + originalApproach + ";" + timeOriginalApproach + ";"
					+ searchBasedApproach + ";" + timeSearchBasedApproach + ";\n");
		}

		writer.close();

	}

	@Test
	public void test_MCAC_solvable()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("MCAC_" + OUTPUT_FILE)));
		config.TRACK = Track.MCAC;

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
			config.PROBABILITY_CNSTRADD = 0;
			config.FITNESS = new ModelSolvabilityEvaluator();
			generator.clearModelsList();
			start = System.currentTimeMillis();
			generator.generateIPMs(config);
			end = System.currentTimeMillis();
			timeSearchBasedApproach = end - start;
			int searchBasedApproach = generator.getModelsList().size();
			writer.append(config.TRACK.name() + ";SOLVABILITY;" + originalApproach + ";" + timeOriginalApproach + ";"
					+ searchBasedApproach + ";" + timeSearchBasedApproach + ";\n");
		}

		writer.close();
	}

	@Test
	public void test_MCAC_tuple()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("Tuple_MCAC_" + OUTPUT_FILE)));
		config.TRACK = Track.MCAC;
		// Check ratio tuple
		config.CHECK_TUPLE_RATIO = true;

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
			config.FITNESS = new ModelTupleRatioEvaluator(config.RATIO);
			generator.clearModelsList();
			start = System.currentTimeMillis();
			generator.generateIPMs(config);
			end = System.currentTimeMillis();
			timeSearchBasedApproach = end - start;
			int searchBasedApproach = generator.getModelsList().size();
			writer.append(config.TRACK.name() + ";SOLVABILITY;" + originalApproach + ";" + timeOriginalApproach + ";"
					+ searchBasedApproach + ";" + timeSearchBasedApproach + ";\n");
		}

		writer.close();
	}

	@Test
	public void test_NUMC_solvable()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("NUMC_" + OUTPUT_FILE)));
		config.TRACK = Track.NUMC;

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
			config.FITNESS = new ModelSolvabilityEvaluator();
			generator.clearModelsList();
			start = System.currentTimeMillis();
			generator.generateIPMs(config);
			end = System.currentTimeMillis();
			timeSearchBasedApproach = end - start;
			int searchBasedApproach = generator.getModelsList().size();
			writer.append(config.TRACK.name() + ";SOLVABILITY;" + originalApproach + ";" + timeOriginalApproach + ";"
					+ searchBasedApproach + ";" + timeSearchBasedApproach + ";\n");
		}

		writer.close();

	}

}
