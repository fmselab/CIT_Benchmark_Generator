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

public class IWCT2025SIExperiments_ComparisonSearchNoSearch_RQ1 {

	BenchmarkGeneratorCLI generator = new BenchmarkGeneratorCLI();
	static int REPETITIONS = 10;
	static String OUTPUT_FILE = "Experiments.csv";
	GeneratorConfiguration config = new GeneratorConfiguration();

	@Before
	public void setUp() {
		config.N_BENCHMARKS = 100;
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
		config.PROBABILITY_PARSHR = 0.5f;
		// Ratio
		config.CHECK_TUPLE_RATIO = false;
		config.CHECK_TEST_RATIO = false;
		config.RATIO = 0.2;
		config.P = 0.1;
		config.EPSILON = 0.1;
		config.RATIO_TEST = 0.2;
		config.POPULATION_SIZE = 100;
	}

	@Test
	public void test_BOOLC_solvable()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		runTests(Track.BOOLC, false, false, 0.0, 0.0);
	}

	@Test
	public void test_BOOLC_tupleRatio()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		runTests(Track.BOOLC, false, true, 0.2, 0.2);
	}
	
	@Test
	public void test_NUMC_testRatio()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		runTests(Track.NUMC, true, false, 0.2, 0.2);
	}
	
	@Test
	public void test_NUMC_tupleRatio()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		runTests(Track.NUMC, false, true, 0.2, 0.2);
	}
	
	@Test
	public void test_BOOLC_testRatio()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		runTests(Track.BOOLC, true, false, 0.2, 0.2);
	}

	@Test
	public void test_MCAC_solvable()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		runTests(Track.MCAC, false, false, 0.0, 0.0);
	}

	@Test
	public void test_MCAC_tupleRatio()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		runTests(Track.MCAC, false, true, 0.2, 0.2);
	}
	
	@Test
	public void test_MCAC_testRatio()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		runTests(Track.MCAC, true, false, 0.2, 0.2);
	}

	@Test
	public void test_NUMC_solvable()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		runTests(Track.NUMC, false, false, 0.0, 0.0);
	}

	
	@Test
	public void test_MCAC_testtuple()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		runTests(Track.MCAC, true, true, 0.3, 0.3);
	}
	
	@Test
	public void test_NUMC_testtuple()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		runTests(Track.NUMC, true, true, 0.3, 0.3);
	}
	
	@Test
	public void test_BOOLC_testtuple()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		runTests(Track.BOOLC, true, true, 0.3, 0.3);
	}
	
	public void runTests(Track track, boolean useTestRatio, boolean useTupleRatio, double testRatio, double tupleRatio) throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		config.TRACK = track;
		String fileName = "";
		String entryName = "";
		
		if (useTestRatio && useTupleRatio) {
			fileName = "TestTuple_";
			entryName = "TUPLETESTRATIO";
		} else if (useTestRatio) {
			fileName = "Test_";
			entryName = "TESTRATIO";
		} else if (useTupleRatio) {
			fileName = "Tuple_";
			entryName = "TUPLERATIO";
		} else {
			fileName = "Solvability_";
			entryName = "SOLVABILITY";
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName + config.TRACK.name() + "_SI_" + OUTPUT_FILE)));
		
		// Check ratio tuple
		config.CHECK_TEST_RATIO = useTestRatio;
		config.CHECK_TUPLE_RATIO = useTupleRatio;
		config.RATIO = tupleRatio;
		config.RATIO_TEST = testRatio;

		for (int i = 0; i < REPETITIONS; i++) {
			config.USE_SEARCH = false;

			long timeOriginalApproach;
			long timeSearchBasedApproach;
			long end;
			generator = new BenchmarkGeneratorCLI();
			long start = System.currentTimeMillis();
			generator.generateIPMs(config);
			end = System.currentTimeMillis();
			int originalApproach = generator.getModelsList().size();
			timeOriginalApproach = end - start;
			config.USE_SEARCH = true;
			generator = new BenchmarkGeneratorCLI();
			writer.append(config.TRACK.name() + ";" + entryName + ";" + originalApproach + ";" + timeOriginalApproach + ";BENCIGEN;\n");
			generator.clearModelsList();
			writer.flush();
			start = System.currentTimeMillis();
			generator.generateIPMs(config);
			end = System.currentTimeMillis();
			timeSearchBasedApproach = end - start;
			int searchBasedApproach = generator.getModelsList().size();
			writer.append(config.TRACK.name() + ";" + entryName + ";" + searchBasedApproach + ";" + timeSearchBasedApproach + ";BENCIGENSMO;\n");
			generator.clearModelsList();
			writer.flush();
		}

		writer.close();
	}
	
	public static void main(String[] args) throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		IWCT2025SIExperiments_ComparisonSearchNoSearch_RQ1 tester = new IWCT2025SIExperiments_ComparisonSearchNoSearch_RQ1();
		tester.setUp();
		tester.test_MCAC_testtuple();
		tester = new IWCT2025SIExperiments_ComparisonSearchNoSearch_RQ1();
		tester.setUp();
		tester.test_NUMC_testtuple();
		tester = new IWCT2025SIExperiments_ComparisonSearchNoSearch_RQ1();
		tester.setUp();
		tester.test_BOOLC_testtuple();
	}
	
}
