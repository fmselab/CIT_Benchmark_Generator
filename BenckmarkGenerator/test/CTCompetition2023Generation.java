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
	public void testBOOLC() throws IOException {
		GeneratorConfiguration.N_BENCHMARKS = 30;

		// Using k in the range [6, 30]
		GeneratorConfiguration.N_PARAMS_MAX = 30;
		GeneratorConfiguration.N_PARAMS_MIN = 6;

		// Using c in the range [1, 100]
		GeneratorConfiguration.N_CONSTRAINTS_MIN = 1;
		GeneratorConfiguration.N_CONSTRAINTS_MAX = 100;

		// Using d in the range [1, 20]
		GeneratorConfiguration.MIN_CONSTRAINTS_COMPLEXITY = 1;
		GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY = 20;

		// Using v in the range [2, 15]
		GeneratorConfiguration.MIN_CARDINALITY = 2;
		GeneratorConfiguration.MAX_CARDINALITY = 15;

		// Generate
		generator.generateBOOLC();
	}

	@Test
	public void testMCAC() throws IOException {
		GeneratorConfiguration.N_BENCHMARKS = 30;

		// Using k in the range [6, 30]
		GeneratorConfiguration.N_PARAMS_MAX = 30;
		GeneratorConfiguration.N_PARAMS_MIN = 6;

		// Using c in the range [1, 100]
		GeneratorConfiguration.N_CONSTRAINTS_MIN = 1;
		GeneratorConfiguration.N_CONSTRAINTS_MAX = 100;

		// Using d in the range [1, 20]
		GeneratorConfiguration.MIN_CONSTRAINTS_COMPLEXITY = 1;
		GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY = 20;

		// Using v in the range [2, 15]
		GeneratorConfiguration.MIN_CARDINALITY = 2;
		GeneratorConfiguration.MAX_CARDINALITY = 15;

		// Generate
		generator.generateMCAC();
	}

	@Test
	public void testNUMC() throws IOException {
		GeneratorConfiguration.N_BENCHMARKS = 30;

		// Using k in the range [6, 30]
		GeneratorConfiguration.N_PARAMS_MAX = 30;
		GeneratorConfiguration.N_PARAMS_MIN = 6;

		// Using c in the range [1, 100]
		GeneratorConfiguration.N_CONSTRAINTS_MIN = 1;
		GeneratorConfiguration.N_CONSTRAINTS_MAX = 100;

		// Using d in the range [1, 20]
		GeneratorConfiguration.MIN_CONSTRAINTS_COMPLEXITY = 1;
		GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY = 20;

		// Using v in the range [2, 15]
		GeneratorConfiguration.MIN_CARDINALITY = 2;
		GeneratorConfiguration.MAX_CARDINALITY = 15;

		// Generate
		generator.generateNUMC();
	}

	@Test
	public void testHIGHLYCONSTRAINED() throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		GeneratorConfiguration.N_BENCHMARKS = 30;

		// Using k in the range [6, 30]
		GeneratorConfiguration.N_PARAMS_MAX = 30;
		GeneratorConfiguration.N_PARAMS_MIN = 6;

		// Using c in the range [1, 100]
		GeneratorConfiguration.N_CONSTRAINTS_MIN = 1;
		GeneratorConfiguration.N_CONSTRAINTS_MAX = 100;

		// Using d in the range [1, 20]
		GeneratorConfiguration.MIN_CONSTRAINTS_COMPLEXITY = 1;
		GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY = 20;

		// Using v in the range [2, 15]
		GeneratorConfiguration.MIN_CARDINALITY = 2;
		GeneratorConfiguration.MAX_CARDINALITY = 15;

		// Using ratio 0.01
		GeneratorConfiguration.RATIO = 0.01;

		// Generate
		generator.generateHIGHLY_CONSTRAINED();
	}

	@Test
	public void testCNF() throws IOException {
		GeneratorConfiguration.N_BENCHMARKS = 30;

		// Using k in the range [6, 30]
		GeneratorConfiguration.N_PARAMS_MAX = 30;
		GeneratorConfiguration.N_PARAMS_MIN = 6;

		// Using c in the range [1, 100]
		GeneratorConfiguration.N_CONSTRAINTS_MIN = 1;
		GeneratorConfiguration.N_CONSTRAINTS_MAX = 100;

		// Using d in the range [1, 20]
		GeneratorConfiguration.MIN_CONSTRAINTS_COMPLEXITY = 1;
		GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY = 20;

		// Using v in the range [2, 15]
		GeneratorConfiguration.MIN_CARDINALITY = 2;
		GeneratorConfiguration.MAX_CARDINALITY = 15;

		// Generate
		generator.generateCNF();
	}

	@Test
	public void testUNIFORMALL() throws IOException {
		GeneratorConfiguration.N_BENCHMARKS = 15;

		// Using k in the range [6, 30]
		GeneratorConfiguration.N_PARAMS_MAX = 30;
		GeneratorConfiguration.N_PARAMS_MIN = 6;

		// Using v in the range [2, 15]
		GeneratorConfiguration.MIN_CARDINALITY = 2;
		GeneratorConfiguration.MAX_CARDINALITY = 15;

		// Generate
		generator.generateUNIFORM_ALL();
	}

	@Test
	public void testUNIFORMBOOLEAN() throws IOException {
		GeneratorConfiguration.N_BENCHMARKS = 15;

		// Using k in the range [6, 30]
		GeneratorConfiguration.N_PARAMS_MAX = 30;
		GeneratorConfiguration.N_PARAMS_MIN = 6;

		// Generate
		generator.generateUNIFORM_BOOLEAN();
	}

	@Test
	public void testMCA() throws IOException {
		GeneratorConfiguration.N_BENCHMARKS = 30;

		// Using k in the range [6, 30]
		GeneratorConfiguration.N_PARAMS_MAX = 30;
		GeneratorConfiguration.N_PARAMS_MIN = 6;

		// Using v in the range [2, 15]
		GeneratorConfiguration.MIN_CARDINALITY = 2;
		GeneratorConfiguration.MAX_CARDINALITY = 15;

		// Generate
		generator.generateMCA();
	}

}
