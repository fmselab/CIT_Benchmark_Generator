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
public class CTCompetition2022Generation {
	BenchmarkGeneratorCLI generator = new BenchmarkGeneratorCLI();

	@Test
	public void testBOOLC() throws IOException, InvalidConfigurationException, SolverException, InterruptedException {
		GeneratorConfiguration.N_BENCHMARKS = 50;

		// Using k in the range [2, 20]
		GeneratorConfiguration.N_PARAMS_MAX = 20;
		GeneratorConfiguration.N_PARAMS_MIN = 2;

		// Using c in the range [1, 100]
		GeneratorConfiguration.N_CONSTRAINTS_MIN = 1;
		GeneratorConfiguration.N_CONSTRAINTS_MAX = 100;

		// Using d in the range [1, 20]
		GeneratorConfiguration.MIN_CONSTRAINTS_COMPLEXITY = 1;
		GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY = 20;

		// Using v in the range [2, 20]
		GeneratorConfiguration.MIN_CARDINALITY = 2;
		GeneratorConfiguration.MAX_CARDINALITY = 20;

		// Generate
		generator.generateBOOLC();
	}

	@Test
	public void testMCAC() throws IOException, InvalidConfigurationException, SolverException, InterruptedException {
		GeneratorConfiguration.N_BENCHMARKS = 50;

		// Using k in the range [2, 20]
		GeneratorConfiguration.N_PARAMS_MAX = 20;
		GeneratorConfiguration.N_PARAMS_MIN = 2;

		// Using c in the range [1, 100]
		GeneratorConfiguration.N_CONSTRAINTS_MIN = 1;
		GeneratorConfiguration.N_CONSTRAINTS_MAX = 100;

		// Using d in the range [1, 20]
		GeneratorConfiguration.MIN_CONSTRAINTS_COMPLEXITY = 1;
		GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY = 20;

		// Using v in the range [2, 20]
		GeneratorConfiguration.MIN_CARDINALITY = 2;
		GeneratorConfiguration.MAX_CARDINALITY = 20;

		// Generate
		generator.generateMCAC();
	}

	@Test
	public void testNUMC() throws IOException, InvalidConfigurationException, SolverException, InterruptedException {
		GeneratorConfiguration.N_BENCHMARKS = 50;

		// Using k in the range [2, 20]
		GeneratorConfiguration.N_PARAMS_MAX = 20;
		GeneratorConfiguration.N_PARAMS_MIN = 2;

		// Using c in the range [1, 100]
		GeneratorConfiguration.N_CONSTRAINTS_MIN = 1;
		GeneratorConfiguration.N_CONSTRAINTS_MAX = 100;

		// Using d in the range [1, 20]
		GeneratorConfiguration.MIN_CONSTRAINTS_COMPLEXITY = 1;
		GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY = 20;

		// Using v in the range [2, 20]
		GeneratorConfiguration.MIN_CARDINALITY = 2;
		GeneratorConfiguration.MAX_CARDINALITY = 20;

		// Generate
		generator.generateNUMC();
	}

	@Test
	public void testUNIFORMALL() throws IOException, InvalidConfigurationException, SolverException, InterruptedException {
		GeneratorConfiguration.N_BENCHMARKS = 50;

		// Using k in the range [2, 20]
		GeneratorConfiguration.N_PARAMS_MAX = 20;
		GeneratorConfiguration.N_PARAMS_MIN = 2;

		// Using v in the range [2, 20]
		GeneratorConfiguration.MIN_CARDINALITY = 2;
		GeneratorConfiguration.MAX_CARDINALITY = 20;

		// Generate
		generator.generateUNIFORM_ALL();
	}

	@Test
	public void testUNIFORMBOOLEAN() throws IOException, InvalidConfigurationException, SolverException, InterruptedException {
		GeneratorConfiguration.N_BENCHMARKS = 50;

		// Using k in the range [2, 20]
		GeneratorConfiguration.N_PARAMS_MAX = 20;
		GeneratorConfiguration.N_PARAMS_MIN = 2;

		// Generate
		generator.generateUNIFORM_BOOLEAN();
	}

	@Test
	public void testMCA() throws IOException, InvalidConfigurationException, SolverException, InterruptedException {
		GeneratorConfiguration.N_BENCHMARKS = 50;

		// Using k in the range [2, 20]
		GeneratorConfiguration.N_PARAMS_MAX = 20;
		GeneratorConfiguration.N_PARAMS_MIN = 2;

		// Using v in the range [2, 20]
		GeneratorConfiguration.MIN_CARDINALITY = 2;
		GeneratorConfiguration.MAX_CARDINALITY = 20;

		// Generate
		generator.generateMCA();
	}

}
