import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.api.SolverException;

import ctwedge.modelanalyzer.AllInCNF;
import ctwedge.util.ext.Utility;
import generators.GeneratorConfiguration;
import generators.Track;
import main.BenchmarkGeneratorCLI;
import models.BooleanParameter;
import models.EnumerativeParameter;
import models.IntegerParameter;
import models.Model;
import models.Parameter;
import util.ModelConfigurationExtractor;

/**
 * This class contains test cases for the journal paper describing the benchmark
 * generator
 * 
 * @author Andrea Bombarda
 *
 */
public class TestBenchmarkGeneratorCLI {

	/**
	 * Constants for test execution, as presented in the paper
	 */
	static int N_BENCHMARKS = 10;
	static int N = 1000;
	static double EPSILON = 0.1;
	static double RATIO_TUPLE = 0.1;
	static double RATIO_TEST = 0.1;
	static int LOWER_INT = -50;
	static int UPPER_INT = 50;
	static int MIN_CARDINALITY = 2;
	static int MAX_CARDINALITY = 30;
	static int MIN_PARAMS = 2;
	static int MAX_PARAMS = 30;
	static int MIN_COMPLEXITY = 1;
	static int MAX_COMPLEXITY = 15;
	static int MIN_CONSTRAINTS = 1;
	static int MAX_CONSTRAINTS = 20;

	/**
	 * The generator to be tested
	 */
	BenchmarkGeneratorCLI generator = new BenchmarkGeneratorCLI();

	/**
	 * TS1
	 * 
	 * NumBenchmarks BenchmarkType Ratio TupleValidityRatio TestValidityRatio
	 * IntegerBounds Cardinality NumParameters ConstraintsConfiguration
	 * BetweenParameters Complexity NumConstraints ConstraintForm Parameters Ranges
	 * Enumeratives Booleans Constraints Arithmetic ExportFormat ACTS CTWedge PICT
	 * 
	 * 
	 * X HC X - X X X X X - X X G X X X X X X - - - -
	 * 
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 * @throws InterruptedException
	 * @throws IOException
	 * 
	 */
	@Test
	public void ts1() throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		// NumBenchmarks = 10
		GeneratorConfiguration.N_BENCHMARKS = N_BENCHMARKS;

		// BenchmarkType = HIGHLY_CONSTRAINED
		GeneratorConfiguration.TRACK = Track.HIGHLY_CONSTRAINED;

		// TestValidityRatio
		GeneratorConfiguration.N = N;
		GeneratorConfiguration.EPSILON = EPSILON;
		GeneratorConfiguration.RATIO_TEST = RATIO_TEST;
		GeneratorConfiguration.CHECK_TUPLE_RATIO = false;
		GeneratorConfiguration.CHECK_TEST_RATIO = true;

		// IntegerBounds
		GeneratorConfiguration.LOWER_BOUND_INT = LOWER_INT;
		GeneratorConfiguration.UPPER_BOUND_INT = UPPER_INT;

		// Cardinality
		GeneratorConfiguration.MIN_CARDINALITY = MIN_CARDINALITY;
		GeneratorConfiguration.MAX_CARDINALITY = MAX_CARDINALITY;

		// Number of parameters
		GeneratorConfiguration.N_PARAMS_MIN = MIN_PARAMS;
		GeneratorConfiguration.N_PARAMS_MAX = MAX_PARAMS;

		// Complexity
		GeneratorConfiguration.MIN_CONSTRAINTS_COMPLEXITY = MIN_COMPLEXITY;
		GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY = MAX_COMPLEXITY;

		// Number of constraints
		GeneratorConfiguration.N_CONSTRAINTS_MIN = MIN_CONSTRAINTS;
		GeneratorConfiguration.N_CONSTRAINTS_MAX = MAX_CONSTRAINTS;

		// Do not export any format
		GeneratorConfiguration.ACTS = false;
		GeneratorConfiguration.CTWEDGE = false;
		GeneratorConfiguration.PICT = false;

		// ----------- GENERATION -----------
		generator.generateIPMs();

		// TODO: Check the outcome
	}

	/**
	 * TS2
	 * 
	 * NumBenchmarks BenchmarkType Ratio TupleValidityRatio TestValidityRatio
	 * IntegerBounds Cardinality NumParameters ConstraintsConfiguration
	 * BetweenParameters Complexity NumConstraints ConstraintForm Parameters Ranges
	 * Enumeratives Booleans Constraints Arithmetic ExportFormat ACTS CTWedge PICT
	 * 
	 * 
	 * X CF - - - - X X X X X X C X - X X X - X X X X
	 * 
	 * 
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 * @throws InterruptedException
	 * @throws IOException
	 * 
	 */
	@Test
	public void ts2() throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		// NumBenchmarks = 10
		GeneratorConfiguration.N_BENCHMARKS = N_BENCHMARKS;

		// BenchmarkType = CNF
		GeneratorConfiguration.TRACK = Track.CNF;

		// Cardinality
		GeneratorConfiguration.MIN_CARDINALITY = MIN_CARDINALITY;
		GeneratorConfiguration.MAX_CARDINALITY = MAX_CARDINALITY;

		// Do not check ratio
		GeneratorConfiguration.CHECK_TUPLE_RATIO = false;
		GeneratorConfiguration.CHECK_TEST_RATIO = false;

		// Number of parameters
		GeneratorConfiguration.N_PARAMS_MIN = MIN_PARAMS;
		GeneratorConfiguration.N_PARAMS_MAX = MAX_PARAMS;

		// Complexity
		GeneratorConfiguration.MIN_CONSTRAINTS_COMPLEXITY = MIN_COMPLEXITY;
		GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY = MAX_COMPLEXITY;

		// Number of constraints
		GeneratorConfiguration.N_CONSTRAINTS_MIN = MIN_CONSTRAINTS;
		GeneratorConfiguration.N_CONSTRAINTS_MAX = MAX_CONSTRAINTS;

		// Export all formats
		GeneratorConfiguration.ACTS = true;
		GeneratorConfiguration.CTWEDGE = true;
		GeneratorConfiguration.PICT = true;

		// ----------- GENERATION -----------
		generator.generateIPMs();
		ArrayList<Model> modelsList = generator.getModelsList();

		// ----------- CHECK THE OUTCOME BASED ON THE SET CONFIGURATION -----------

		// First, check the number of models
		assertTrue(modelsList.size() == N_BENCHMARKS);

		for (Model m : modelsList) {
			ModelConfigurationExtractor extractor = new ModelConfigurationExtractor(Utility.loadModel(m.toString()));

			// Check the cardinality
			assertTrue(extractor.getMaximumCardinality() <= MAX_CARDINALITY);
			assertTrue(extractor.getMinimumCardinality() >= MIN_CARDINALITY);

			// Check the number of params
			assertTrue(extractor.getNumParams() <= MAX_PARAMS);
			assertTrue(extractor.getNumParams() >= MIN_PARAMS);

			// Check the number of constraints
			assertTrue(extractor.getNumConstraints() <= MAX_CONSTRAINTS);
			assertTrue(extractor.getNumConstraints() >= MIN_CONSTRAINTS);

			// Check that the parameters are all Boolean or Enumeratives
			for (Parameter p : m.getParameters()) {
				assertTrue(p instanceof BooleanParameter || p instanceof EnumerativeParameter);
			}

			// Check that the model have been exported
			assertTrue(new File("./benchmarks/" + m.getName() + ".ctw").exists());
			assertTrue(new File("./benchmarks/" + m.getName() + ".txt").exists());
			assertTrue(new File("./benchmarks/" + m.getName() + "_pict.txt").exists());

			// Then, delete the files
			new File("./benchmarks/" + m.getName() + ".ctw").delete();
			new File("./benchmarks/" + m.getName() + ".txt").delete();
			new File("./benchmarks/" + m.getName() + "_pict.txt").delete();

			// Check the complexity
			assertTrue(extractor.getMaxConstraintComplexity() <= MAX_COMPLEXITY);
			assertTrue(extractor.getMinConstraintComplexity() >= MIN_COMPLEXITY);

			// Check the track
			assertTrue(extractor.getModelType() == Track.CNF);

		}
	}

	/**
	 * TS3
	 * 
	 * NumBenchmarks BenchmarkType Ratio TupleValidityRatio TestValidityRatio
	 * IntegerBounds Cardinality NumParameters ConstraintsConfiguration
	 * BetweenParameters Complexity NumConstraints ConstraintForm Parameters Ranges
	 * Enumeratives Booleans Constraints Arithmetic ExportFormat ACTS CTWedge PICT
	 * 
	 * 
	 * X NC - - - X X X X - X X G X X X X X X X X X -
	 * 
	 * 
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 * @throws InterruptedException
	 * @throws IOException
	 * 
	 */
	@Test
	public void ts3() throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		// NumBenchmarks = 10
		GeneratorConfiguration.N_BENCHMARKS = N_BENCHMARKS;

		// BenchmarkType = NUMC
		GeneratorConfiguration.TRACK = Track.NUMC;

		// IntegerBounds
		GeneratorConfiguration.LOWER_BOUND_INT = LOWER_INT;
		GeneratorConfiguration.UPPER_BOUND_INT = UPPER_INT;

		// Do not check ratio
		GeneratorConfiguration.CHECK_TUPLE_RATIO = false;
		GeneratorConfiguration.CHECK_TEST_RATIO = false;

		// Cardinality
		GeneratorConfiguration.MIN_CARDINALITY = MIN_CARDINALITY;
		GeneratorConfiguration.MAX_CARDINALITY = MAX_CARDINALITY;

		// Number of parameters
		GeneratorConfiguration.N_PARAMS_MIN = MIN_PARAMS;
		GeneratorConfiguration.N_PARAMS_MAX = MAX_PARAMS;

		// Complexity
		GeneratorConfiguration.MIN_CONSTRAINTS_COMPLEXITY = MIN_COMPLEXITY;
		GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY = MAX_COMPLEXITY;

		// Number of constraints
		GeneratorConfiguration.N_CONSTRAINTS_MIN = MIN_CONSTRAINTS;
		GeneratorConfiguration.N_CONSTRAINTS_MAX = MAX_CONSTRAINTS;

		// Export all formats
		GeneratorConfiguration.ACTS = true;
		GeneratorConfiguration.CTWEDGE = true;
		GeneratorConfiguration.PICT = false;

		// ----------- GENERATION -----------
		generator.generateIPMs();
		ArrayList<Model> modelsList = generator.getModelsList();

		// ----------- CHECK THE OUTCOME BASED ON THE SET CONFIGURATION -----------

		// First, check the number of models
		assertTrue(modelsList.size() == N_BENCHMARKS);

		for (Model m : modelsList) {
			ModelConfigurationExtractor extractor = new ModelConfigurationExtractor(Utility.loadModel(m.toString()));

			// Check the cardinality
			assertTrue(extractor.getMaximumCardinality() <= MAX_CARDINALITY);
			assertTrue(extractor.getMinimumCardinality() >= MIN_CARDINALITY);

			// Check the number of params
			assertTrue(extractor.getNumParams() <= MAX_PARAMS);
			assertTrue(extractor.getNumParams() >= MIN_PARAMS);

			// Check the lower and upper bound for integers
			assertTrue(extractor.getLowerBoundForInts() >= LOWER_INT);
			assertTrue(extractor.getUpperBoundForInts() <= UPPER_INT);

			// Check the number of constraints
			assertTrue(extractor.getNumConstraints() <= MAX_CONSTRAINTS);
			assertTrue(extractor.getNumConstraints() >= MIN_CONSTRAINTS);

			// Check that the parameters are all Boolean or Enumeratives or Integers
			for (Parameter p : m.getParameters()) {
				assertTrue(p instanceof BooleanParameter || p instanceof EnumerativeParameter
						|| p instanceof IntegerParameter);
			}

			// Check that the model have been exported
			assertTrue(new File("./benchmarks/" + m.getName() + ".ctw").exists());
			assertTrue(new File("./benchmarks/" + m.getName() + ".txt").exists());
			assertFalse(new File("./benchmarks/" + m.getName() + "_pict.txt").exists());

			// Then, delete the files
			new File("./benchmarks/" + m.getName() + ".ctw").delete();
			new File("./benchmarks/" + m.getName() + ".txt").delete();

			// Check the complexity
			assertTrue(extractor.getMaxConstraintComplexity() <= MAX_COMPLEXITY);
			assertTrue(extractor.getMinConstraintComplexity() >= MIN_COMPLEXITY);

			// Check the track. Considering that the models are randomly generated, it may
			// happen that a particular type of parameter (i.e., Integers) are not present.
			// In that case, the track is considered MCAC even if it is NUMC, or BOOLC if
			// also enumeratives are missing
			assertTrue(extractor.getModelType() == Track.NUMC || extractor.getModelType() == Track.MCAC
					|| extractor.getModelType() == Track.BOOLC || extractor.getModelType() == Track.CNF);

		}
	}

	/**
	 * TS4
	 * 
	 * NumBenchmarks BenchmarkType Ratio TupleValidityRatio TestValidityRatio
	 * IntegerBounds Cardinality NumParameters ConstraintsConfiguration
	 * BetweenParameters Complexity NumConstraints ConstraintForm Parameters Ranges
	 * Enumeratives Booleans Constraints Arithmetic ExportFormat ACTS CTWedge PICT
	 * 
	 * 
	 * X MC X X - - X X X X X X G X - X X X - - - - -
	 * 
	 * 
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 * @throws InterruptedException
	 * @throws IOException
	 * 
	 */
	@Test
	public void ts4() throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		// TODO: Long execution time
		// NumBenchmarks = 10
		GeneratorConfiguration.N_BENCHMARKS = N_BENCHMARKS;

		// BenchmarkType = MCAC
		GeneratorConfiguration.TRACK = Track.MCAC;

		// Tuple validity ratio
		GeneratorConfiguration.RATIO = RATIO_TUPLE;
		GeneratorConfiguration.CHECK_TUPLE_RATIO = true;
		GeneratorConfiguration.CHECK_TEST_RATIO = false;

		// Cardinality
		GeneratorConfiguration.MIN_CARDINALITY = MIN_CARDINALITY;
		GeneratorConfiguration.MAX_CARDINALITY = MAX_CARDINALITY;

		// Number of parameters
		GeneratorConfiguration.N_PARAMS_MIN = MIN_PARAMS;
		GeneratorConfiguration.N_PARAMS_MAX = MAX_PARAMS;

		// Complexity
		GeneratorConfiguration.MIN_CONSTRAINTS_COMPLEXITY = MIN_COMPLEXITY;
		GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY = MAX_COMPLEXITY;

		// Number of constraints
		GeneratorConfiguration.N_CONSTRAINTS_MIN = MIN_CONSTRAINTS;
		GeneratorConfiguration.N_CONSTRAINTS_MAX = MAX_CONSTRAINTS;

		// No export
		GeneratorConfiguration.ACTS = false;
		GeneratorConfiguration.CTWEDGE = false;
		GeneratorConfiguration.PICT = false;

		// ----------- GENERATION -----------
		generator.generateIPMs();
		ArrayList<Model> modelsList = generator.getModelsList();

		// ----------- CHECK THE OUTCOME BASED ON THE SET CONFIGURATION -----------

		// First, check the number of models
		assertTrue(modelsList.size() == N_BENCHMARKS);

		for (Model m : modelsList) {
			ModelConfigurationExtractor extractor = new ModelConfigurationExtractor(Utility.loadModel(m.toString()));

			// Check the cardinality
			assertTrue(extractor.getMaximumCardinality() <= MAX_CARDINALITY);
			assertTrue(extractor.getMinimumCardinality() >= MIN_CARDINALITY);

			// Check the number of params
			assertTrue(extractor.getNumParams() <= MAX_PARAMS);
			assertTrue(extractor.getNumParams() >= MIN_PARAMS);

			// Check the ratio
			System.out.println(extractor.getTupleValidityRatio());
			assertTrue(extractor.getTupleValidityRatio() <= RATIO_TUPLE);

			// Check the number of constraints
			assertTrue(extractor.getNumConstraints() <= MAX_CONSTRAINTS);
			assertTrue(extractor.getNumConstraints() >= MIN_CONSTRAINTS);

			// Check that the parameters are all Boolean or Enumeratives
			for (Parameter p : m.getParameters()) {
				assertTrue(p instanceof BooleanParameter || p instanceof EnumerativeParameter);
			}

			// Check that the model have not been exported
			assertFalse(new File("./benchmarks/" + m.getName() + ".ctw").exists());
			assertFalse(new File("./benchmarks/" + m.getName() + ".txt").exists());
			assertFalse(new File("./benchmarks/" + m.getName() + "_pict.txt").exists());

			// Check the complexity
			assertTrue(extractor.getMaxConstraintComplexity() <= MAX_COMPLEXITY);
			assertTrue(extractor.getMinConstraintComplexity() >= MIN_COMPLEXITY);

			// Check the track. Considering that the models are randomly generated, it may
			// happen that a particular type of parameter (i.e., Enumeratives) are not
			// present.
			// In that case, the track is considered BOOLC even if it is NUMC, or CNF if
			// randomly all the constraints are in CNF
			assertTrue(extractor.getModelType() == Track.MCAC || extractor.getModelType() == Track.BOOLC
					|| extractor.getModelType() == Track.CNF);
		}
	}

	/**
	 * TS5
	 * 
	 * NumBenchmarks BenchmarkType Ratio TupleValidityRatio TestValidityRatio
	 * IntegerBounds Cardinality NumParameters ConstraintsConfiguration
	 * BetweenParameters Complexity NumConstraints ConstraintForm Parameters Ranges
	 * Enumeratives Booleans Constraints Arithmetic ExportFormat ACTS CTWedge PICT
	 * 
	 * 
	 * X BC - - - - - X X - X X G X - - X X - - - - -
	 * 
	 * 
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 * @throws InterruptedException
	 * @throws IOException
	 * 
	 */
	@Test
	public void ts5() throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		// NumBenchmarks = 10
		GeneratorConfiguration.N_BENCHMARKS = N_BENCHMARKS;

		// BenchmarkType = BOOLC
		GeneratorConfiguration.TRACK = Track.BOOLC;

		// Do not check ratio
		GeneratorConfiguration.CHECK_TUPLE_RATIO = false;
		GeneratorConfiguration.CHECK_TEST_RATIO = false;

		// Number of parameters
		GeneratorConfiguration.N_PARAMS_MIN = MIN_PARAMS;
		GeneratorConfiguration.N_PARAMS_MAX = MAX_PARAMS;

		// Complexity
		GeneratorConfiguration.MIN_CONSTRAINTS_COMPLEXITY = MIN_COMPLEXITY;
		GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY = MAX_COMPLEXITY;

		// Number of constraints
		GeneratorConfiguration.N_CONSTRAINTS_MIN = MIN_CONSTRAINTS;
		GeneratorConfiguration.N_CONSTRAINTS_MAX = MAX_CONSTRAINTS;

		// No export
		GeneratorConfiguration.ACTS = false;
		GeneratorConfiguration.CTWEDGE = false;
		GeneratorConfiguration.PICT = false;

		// ----------- GENERATION -----------
		generator.generateIPMs();
		ArrayList<Model> modelsList = generator.getModelsList();

		// ----------- CHECK THE OUTCOME BASED ON THE SET CONFIGURATION -----------

		// First, check the number of models
		assertTrue(modelsList.size() == N_BENCHMARKS);

		for (Model m : modelsList) {
			ModelConfigurationExtractor extractor = new ModelConfigurationExtractor(Utility.loadModel(m.toString()));
			
			// Check the number of params
			assertTrue(extractor.getNumParams() <= MAX_PARAMS);
			assertTrue(extractor.getNumParams() >= MIN_PARAMS);

			// Check the number of constraints
			assertTrue(extractor.getNumConstraints() <= MAX_CONSTRAINTS);
			assertTrue(extractor.getNumConstraints() >= MIN_CONSTRAINTS);

			// Check that the parameters are all Booleans
			for (Parameter p : m.getParameters()) {
				assertTrue(p instanceof BooleanParameter);
			}

			// Check that the model have not been exported
			assertFalse(new File("./benchmarks/" + m.getName() + ".ctw").exists());
			assertFalse(new File("./benchmarks/" + m.getName() + ".txt").exists());
			assertFalse(new File("./benchmarks/" + m.getName() + "_pict.txt").exists());

			// Check the complexity
			assertTrue(extractor.getMaxConstraintComplexity() <= MAX_COMPLEXITY);
			assertTrue(extractor.getMinConstraintComplexity() >= MIN_COMPLEXITY);

			// Check the track
			assertTrue(extractor.getModelType() == Track.BOOLC);
		}
	}
}
