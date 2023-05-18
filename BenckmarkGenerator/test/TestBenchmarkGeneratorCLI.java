import org.junit.Test;

import generators.GeneratorConfiguration;
import generators.Track;

/**
 * This class contains test cases for the journal paper describing the benchmark
 * generator
 * 
 * @author Andrea_PC
 *
 */
public class TestBenchmarkGeneratorCLI {

	/**
	 * TS1
	 * 
	 * NumBenchmarks BenchmarkType Ratio TupleValidityRatio TestValidityRatio
	 * IntegerBounds Cardinality NumParameters ConstraintsConfiguration
	 * BetweenParameters Complexity NumConstraints ConstraintForm Parameters Ranges
	 * Enumeratives Booleans Constraints Arithmetic ExportFormat ACTS CTWedge PICT 
	 * 
	 * 
	 * X HC X - X - X X X - X X G X X X X X X - - - -
	 * 
	 * 
	 */
	@Test
	public void ts1() {
		// NumBenchmarks = 10
		GeneratorConfiguration.N_BENCHMARKS = 10;

		// BenchmarkType = HIGHLY_CONSTRAINED
		GeneratorConfiguration.TRACK = Track.HIGHLY_CONSTRAINED;

		// TestValidityRatio
		GeneratorConfiguration.N = 1000;
		GeneratorConfiguration.EPSILON = 0.1;
		GeneratorConfiguration.RATIO_TEST = 0.01;

	}

}
