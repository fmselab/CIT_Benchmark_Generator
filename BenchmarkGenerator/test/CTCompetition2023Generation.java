import java.io.IOException;

import org.junit.Test;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.api.SolverException;

import generators.GeneratorConfiguration;
import main.BenchmarkGeneratorCLI;

/**
 * Class contained experiments used for generating benchmarks for the
 * CT-Competition
 * 
 * @author andrea
 *
 */
public class CTCompetition2023Generation {
	BenchmarkGeneratorCLI generator = new BenchmarkGeneratorCLI();

	@Test
	public void testBOOLC() throws IOException, InvalidConfigurationException, SolverException, InterruptedException {
		GeneratorConfiguration config = new GeneratorConfiguration();
		
		config.N_BENCHMARKS = 30;

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

		// Generate
		generator.generateBOOLC(config);
	}

	@Test
	public void testMCAC() throws IOException, InvalidConfigurationException, SolverException, InterruptedException {
		GeneratorConfiguration config = new GeneratorConfiguration();
		
		config.N_BENCHMARKS = 30;

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

		// Generate
		generator.generateMCAC(config);
	}

	@Test
	public void testNUMC() throws IOException, InvalidConfigurationException, SolverException, InterruptedException {
		GeneratorConfiguration config = new GeneratorConfiguration();
		
		config.N_BENCHMARKS = 30;

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

		// Generate
		generator.generateNUMC(config);
	}

	@Test
	public void testHIGHLYCONSTRAINED() throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		GeneratorConfiguration config = new GeneratorConfiguration();
		
		config.N_BENCHMARKS = 30;

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

		// Using ratio 0.01
		config.CHECK_TUPLE_RATIO = true;
		config.RATIO = 0.01;

		// Generate
		generator.generateNUMC(config);
	}

	@Test
	public void testCNF() throws IOException, InvalidConfigurationException, SolverException, InterruptedException {
		GeneratorConfiguration config = new GeneratorConfiguration();
		
		config.N_BENCHMARKS = 30;

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
		
		// Use CNF
		config.CNF = true;

		// Generate
		generator.generateMCAC(config);
	}

	@Test
	public void testUNIFORMALL() throws IOException, InvalidConfigurationException, SolverException, InterruptedException {
		GeneratorConfiguration config = new GeneratorConfiguration();
		
		config.N_BENCHMARKS = 15;

		// Using k in the range [6, 30]
		config.N_PARAMS_MAX = 30;
		config.N_PARAMS_MIN = 6;

		// Using v in the range [2, 15]
		config.MIN_CARDINALITY = 2;
		config.MAX_CARDINALITY = 15;

		// Generate
		generator.generateUNIFORM_ALL(config);
	}

	@Test
	public void testUNIFORMBOOLEAN() throws IOException, InvalidConfigurationException, SolverException, InterruptedException {
		GeneratorConfiguration config = new GeneratorConfiguration();
		
		config.N_BENCHMARKS = 15;

		// Using k in the range [6, 30]
		config.N_PARAMS_MAX = 30;
		config.N_PARAMS_MIN = 6;

		// Generate
		generator.generateUNIFORM_BOOLEAN(config);
	}

	@Test
	public void testMCA() throws IOException, InvalidConfigurationException, SolverException, InterruptedException {
		GeneratorConfiguration config = new GeneratorConfiguration();
		
		config.N_BENCHMARKS = 30;

		// Using k in the range [6, 30]
		config.N_PARAMS_MAX = 30;
		config.N_PARAMS_MIN = 6;

		// Using v in the range [2, 15]
		config.MIN_CARDINALITY = 2;
		config.MAX_CARDINALITY = 15;

		// Generate
		generator.generateMCA(config);
	}

}
