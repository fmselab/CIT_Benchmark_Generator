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

public class IWCT2025Experiments_ComparisonSearchNoSearch_RQ2 {

	BenchmarkGeneratorCLI generator = new BenchmarkGeneratorCLI();
	static int REPETITIONS = 10;
	static int TARGET = 50;
	static int TIMEOUT = 600000; // 10 minutes
	static String OUTPUT_FILE = "experiments_RQ2.csv";
	GeneratorConfiguration config = new GeneratorConfiguration();

	@Before
	public void setUp() {
		config.N_BENCHMARKS = 1;
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
		config.P = 0.1;
		config.EPSILON = 0.05;
		config.RATIO_TEST = 0.1;
	}

	@Test
	public void test_BOOLC_solvable()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		solvable(Track.BOOLC);

	}

	@Test
	public void test_MCAC_solvable()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		solvable(Track.MCAC);
	}

	@Test
	public void test_NUMC_solvable()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		solvable(Track.NUMC);
	}

	public void solvable(Track track)
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(track.name() + "_" + OUTPUT_FILE)));
		config.TRACK = track;
		for (int i = 0; i < REPETITIONS; i++) {
			config.USE_SEARCH = false;
			config.CHECK_SOLVABLE = true;

			long timeOriginalApproach;
			long timeSearchBasedApproach;
			long end;
			long start = System.currentTimeMillis();
			int countOriginal = 0;
			do {
				do {
					generator = new BenchmarkGeneratorCLI();
					generator.generateIPMs(config);
				} while (generator.getModelsList().size() == 0);
				countOriginal++;
			} while (countOriginal < TARGET && System.currentTimeMillis() - start < TIMEOUT * TARGET);
			end = System.currentTimeMillis();
			timeOriginalApproach = end - start;
			config.USE_SEARCH = true;
			config.PROBABILITY_CNSTRADD = 0;
			config.CHECK_SOLVABLE = false;
			int countSearch = 0;
			start = System.currentTimeMillis();
			do {
				do {
					generator = new BenchmarkGeneratorCLI();
					generator.generateIPMs(config);
				} while (generator.getModelsList().size() == 0);
				countSearch++;
			} while (countSearch < TARGET && System.currentTimeMillis() - start < TIMEOUT * TARGET);
			end = System.currentTimeMillis();
			timeSearchBasedApproach = end - start;
			writer.append(config.TRACK.name() + ";SOLVABILITY;" + countOriginal + ";" + timeOriginalApproach + ";"
					+ countSearch + ";" + timeSearchBasedApproach + ";\n");
			generator.clearModelsList();
			writer.flush();
		}

		writer.close();
	}

	@Test
	public void test_BOOLC_tupleRatio()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		tupleRatio(Track.BOOLC);
	}

	@Test
	public void test_MCAC_tuple()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		tupleRatio(Track.MCAC);
	}

	@Test
	public void test_NUMC_tupleRatio()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		tupleRatio(Track.NUMC);
	}

	public void tupleRatio(Track track)
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		BufferedWriter writer = new BufferedWriter(
				new FileWriter(new File("Tuple_" + track.name() + "_" + OUTPUT_FILE)));
		config.TRACK = track;
		// Check ratio tuple
		config.CHECK_TUPLE_RATIO = true;
		for (int i = 0; i < REPETITIONS; i++) {
			config.USE_SEARCH = false;
			config.CHECK_SOLVABLE = true;

			long timeOriginalApproach;
			long timeSearchBasedApproach;
			long end;
			long start = System.currentTimeMillis();
			int countOriginal = 0;
			do {
				do {
					generator = new BenchmarkGeneratorCLI();
					generator.generateIPMs(config);
				} while (generator.getModelsList().size() == 0
						&& System.currentTimeMillis() - start < TIMEOUT * TARGET);
				if (generator.getModelsList().size() > 0)
					countOriginal++;
			} while (countOriginal < TARGET && System.currentTimeMillis() - start < TIMEOUT * TARGET);
			end = System.currentTimeMillis();
			timeOriginalApproach = end - start;
			config.USE_SEARCH = true;
			int countSearch = 0;
			start = System.currentTimeMillis();
			do {
				do {
					generator = new BenchmarkGeneratorCLI();
					generator.generateIPMs(config);
				} while (generator.getModelsList().size() == 0
						&& System.currentTimeMillis() - start < TIMEOUT * TARGET);
				if (generator.getModelsList().size() > 0)
					countSearch++;
			} while (countSearch < TARGET && System.currentTimeMillis() - start < TIMEOUT * TARGET);
			end = System.currentTimeMillis();
			timeSearchBasedApproach = end - start;
			writer.append(config.TRACK.name() + ";TUPLERATIO;" + countOriginal + ";" + timeOriginalApproach + ";"
					+ countSearch + ";" + timeSearchBasedApproach + ";\n");
			generator.clearModelsList();
			writer.flush();
		}

		writer.close();
	}

	@Test
	public void test_NUMC_testRatio()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		testRatio(Track.NUMC);
	}

	@Test
	public void test_BOOLC_testRatio()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		testRatio(Track.BOOLC);
	}

	@Test
	public void test_MCAC_test()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		testRatio(Track.MCAC);
	}

	public void testRatio(Track track)
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		BufferedWriter writer = new BufferedWriter(
				new FileWriter(new File("Test_" + track.name() + "_" + OUTPUT_FILE)));
		config.TRACK = track;
		// Check ratio tuple
		config.CHECK_TEST_RATIO = true;
		for (int i = 0; i < REPETITIONS; i++) {
			config.USE_SEARCH = false;
			config.CHECK_SOLVABLE = true;

			long timeOriginalApproach;
			long timeSearchBasedApproach;
			long end;
			long start = System.currentTimeMillis();
			int countOriginal = 0;
			do {
				do {
					generator = new BenchmarkGeneratorCLI();
					generator.generateIPMs(config);
				} while (generator.getModelsList().size() == 0);
				countOriginal++;
			} while (countOriginal < TARGET && System.currentTimeMillis() - start < TIMEOUT * TARGET);
			end = System.currentTimeMillis();
			timeOriginalApproach = end - start;
			config.USE_SEARCH = true;
			int countSearch = 0;
			start = System.currentTimeMillis();
			do {
				do {
					generator = new BenchmarkGeneratorCLI();
					generator.generateIPMs(config);
				} while (generator.getModelsList().size() == 0);
				countSearch++;
			} while (countSearch < TARGET && System.currentTimeMillis() - start < TIMEOUT * TARGET);
			end = System.currentTimeMillis();
			timeSearchBasedApproach = end - start;
			writer.append(config.TRACK.name() + ";TESTRATIO;" + countOriginal + ";" + timeOriginalApproach + ";"
					+ countSearch + ";" + timeSearchBasedApproach + ";\n");
			generator.clearModelsList();
			writer.flush();
		}

		writer.close();
	}

}
