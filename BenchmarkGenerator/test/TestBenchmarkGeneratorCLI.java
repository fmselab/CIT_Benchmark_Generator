import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.api.SolverException;

import ctwedge.ctWedge.Bool;
import ctwedge.ctWedge.Enumerative;
import ctwedge.ctWedge.Parameter;
import ctwedge.ctWedge.Range;
import ctwedge.util.ext.Utility;
import ctwedge.util.smt.SMTConstraintTranslator;
import generators.GeneratorConfiguration;
import generators.Track;
import main.BenchmarkGeneratorCLI;
import models.Model;
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
	static double P = 0.75;
	static double EPSILON = 0.1;
	static double RATIO_TUPLE = 0.3;
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
	GeneratorConfiguration config = new GeneratorConfiguration();
	
	
	@Before
	public void setup() {
		// Limit the number of attempts for testing
		config.N_ATTEMPTS = 2;
		// Activate the LOGGER and deactivate the ones that are not needed
		Logger LOGGER = LogManager.getRootLogger();
		LOGGER.setLevel(Level.DEBUG);
		LogManager.getLogger(kali.util.ConstraintTranslator.class).setLevel(Level.OFF);
		LogManager.getLogger(SMTConstraintTranslator.class).setLevel(Level.OFF);
	}

	/**
	 * TS1
	 * 
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
		config.N_BENCHMARKS = N_BENCHMARKS;

		// BenchmarkType = NUMC
		config.TRACK = Track.NUMC;

		// IntegerBounds
		config.LOWER_BOUND_INT = LOWER_INT;
		config.UPPER_BOUND_INT = UPPER_INT;

		// Do not check ratio
		config.CHECK_TUPLE_RATIO = false;
		config.CHECK_TEST_RATIO = false;

		// Cardinality
		config.MIN_CARDINALITY = MIN_CARDINALITY;
		config.MAX_CARDINALITY = MAX_CARDINALITY;

		// Number of parameters
		config.N_PARAMS_MIN = MIN_PARAMS;
		config.N_PARAMS_MAX = MAX_PARAMS;

		// Complexity
		config.MIN_CONSTRAINTS_COMPLEXITY = MIN_COMPLEXITY;
		config.MAX_CONSTRAINTS_COMPLEXITY = MAX_COMPLEXITY;

		// Number of constraints
		config.N_CONSTRAINTS_MIN = MIN_CONSTRAINTS;
		config.N_CONSTRAINTS_MAX = MAX_CONSTRAINTS;

		// Export all formats required
		config.ACTS = false;
		config.CTWEDGE = false;
		config.PICT = false;

		// ----------- GENERATION -----------
		generator.generateIPMs(config);
		ArrayList<Model> modelsList = generator.getModelsList();

		// ----------- CHECK THE OUTCOME BASED ON THE SET CONFIGURATION -----------

		// First, check the number of models
		assertTrue(modelsList.size() <= N_BENCHMARKS);

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
				assertTrue(p instanceof Bool || p instanceof Enumerative
						|| p instanceof Range);
			}

			// Check that the model have been exported
			assertFalse(new File("./benchmarks/" + m.getName() + ".ctw").exists());
			assertFalse(new File("./benchmarks/" + m.getName() + ".txt").exists());
			assertFalse(new File("./benchmarks/" + m.getName() + "_pict.txt").exists());

			// Check the complexity
			assertTrue(extractor.getMaxConstraintComplexity() <= MAX_COMPLEXITY);
			assertTrue(extractor.getMinConstraintComplexity() >= MIN_COMPLEXITY);

			// Check the track. Considering that the models are randomly generated, it may
			// happen that a particular type of parameter (i.e., Integers) are not present.
			// In that case, the track is considered MCAC even if it is NUMC, or BOOLC if
			// also enumeratives are missing
			assertTrue(extractor.getModelType() == Track.NUMC || extractor.getModelType() == Track.MCAC
					|| extractor.getModelType() == Track.BOOLC);

		}
	}

	/**
	 * TS2
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
		config.N_BENCHMARKS = N_BENCHMARKS;

		// BenchmarkType = MCAC
		config.TRACK = Track.NUMC;

		// Check both ratios
		config.P = P;
		config.EPSILON = EPSILON;
		config.RATIO_TEST = RATIO_TEST;
		config.RATIO = RATIO_TUPLE;
		config.CHECK_TUPLE_RATIO = true;
		config.CHECK_TEST_RATIO = true;

		// IntegerBounds
		config.LOWER_BOUND_INT = LOWER_INT;
		config.UPPER_BOUND_INT = UPPER_INT;

		// Cardinality
		config.MIN_CARDINALITY = MIN_CARDINALITY;
		config.MAX_CARDINALITY = MAX_CARDINALITY;

		// Number of parameters
		config.N_PARAMS_MIN = MIN_PARAMS;
		config.N_PARAMS_MAX = MAX_PARAMS;

		// Complexity
		config.MIN_CONSTRAINTS_COMPLEXITY = MIN_COMPLEXITY;
		config.MAX_CONSTRAINTS_COMPLEXITY = MAX_COMPLEXITY;

		// Number of constraints
		config.N_CONSTRAINTS_MIN = MIN_CONSTRAINTS;
		config.N_CONSTRAINTS_MAX = MAX_CONSTRAINTS;

		// Use CNF
		config.CNF = true;

		// Export the required formats
		config.ACTS = true;
		config.CTWEDGE = true;
		config.PICT = true;

		// ----------- GENERATION -----------
		generator.generateIPMs(config);
		ArrayList<Model> modelsList = generator.getModelsList();

		// ----------- CHECK THE OUTCOME BASED ON THE SET CONFIGURATION -----------

		// First, check the number of models
		assertTrue(modelsList.size() <= N_BENCHMARKS);

		for (Model m : modelsList) {
			ModelConfigurationExtractor extractor = new ModelConfigurationExtractor(Utility.loadModel(m.toString()));

			// Check the cardinality
			assertTrue(extractor.getMaximumCardinality() <= MAX_CARDINALITY);
			assertTrue(extractor.getMinimumCardinality() >= MIN_CARDINALITY);

			// Check the lower and upper bound for integers
			assertTrue(extractor.getLowerBoundForInts() >= LOWER_INT);
			assertTrue(extractor.getUpperBoundForInts() <= UPPER_INT);

			// Check the number of params
			assertTrue(extractor.getNumParams() <= MAX_PARAMS);
			assertTrue(extractor.getNumParams() >= MIN_PARAMS);

			// Check the ratio
			assertTrue(extractor.getTupleValidityRatio() <= RATIO_TUPLE);

			// The test ratio has been computed. Evaluate it
			if (m.isRatioExact()) {
				assertTrue(m.getTestValidityRatio() < config.RATIO_TEST);
			} else {
				double ratio = m.getTestValidityRatio();
				// The ratio is not exact. But we can check the interval based on EPSILON
				assertTrue(ratio >= (1 - config.EPSILON) * config.RATIO_TEST
						&& ratio <= (1 + config.EPSILON) * config.RATIO_TEST);
			}

			// Check the number of constraints
			assertTrue(extractor.getNumConstraints() <= MAX_CONSTRAINTS);
			assertTrue(extractor.getNumConstraints() >= MIN_CONSTRAINTS);

			// Check that the parameters are all Boolean or Enumeratives or Integers
			for (Parameter p : m.getParameters()) {
				assertTrue(p instanceof Bool || p instanceof Enumerative
						|| p instanceof Range);
			}

			// Check that the model have been exported
			assertTrue(new File("./benchmarks/" + m.getName() + ".ctw").exists());
			assertTrue(new File("./benchmarks/" + m.getName() + ".txt").exists());
			assertTrue(new File("./benchmarks/" + m.getName() + "_pict.txt").exists());

			// Then, delete the files
			new File("./benchmarks/" + m.getName() + ".ctw").delete();
			new File("./benchmarks/" + m.getName() + ".txt").delete();
			new File("./benchmarks/" + m.getName() + "_pict.txt").delete();

			// Check CNF
			assertTrue(extractor.isCNF());

			// Check the complexity
			assertTrue(extractor.getMaxConstraintComplexity() <= MAX_COMPLEXITY);
			assertTrue(extractor.getMinConstraintComplexity() >= MIN_COMPLEXITY);

			// Check the track. Considering that the models are randomly generated, it may
			// happen that a particular type of parameter (i.e., Integers) are not present.
			// In that case, the track is considered MCAC even if it is NUMC, or BOOLC if
			// also enumeratives are missing
			assertTrue(extractor.getModelType() == Track.NUMC || extractor.getModelType() == Track.MCAC
					|| extractor.getModelType() == Track.BOOLC);
		}
	}

	/**
	 * TS3
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
		config.N_BENCHMARKS = N_BENCHMARKS;

		// BenchmarkType = MCAC
		config.TRACK = Track.NUMC;

		// Do not check ratios
		config.CHECK_TUPLE_RATIO = false;
		config.CHECK_TEST_RATIO = false;

		// IntegerBounds
		config.LOWER_BOUND_INT = LOWER_INT;
		config.UPPER_BOUND_INT = UPPER_INT;

		// Cardinality
		config.MIN_CARDINALITY = MIN_CARDINALITY;
		config.MAX_CARDINALITY = MAX_CARDINALITY;

		// Number of parameters
		config.N_PARAMS_MIN = MIN_PARAMS;
		config.N_PARAMS_MAX = MAX_PARAMS;

		// Complexity
		config.MIN_CONSTRAINTS_COMPLEXITY = MIN_COMPLEXITY;
		config.MAX_CONSTRAINTS_COMPLEXITY = MAX_COMPLEXITY;

		// Number of constraints
		config.N_CONSTRAINTS_MIN = MIN_CONSTRAINTS;
		config.N_CONSTRAINTS_MAX = MAX_CONSTRAINTS;

		// Use forbidden tuples
		config.FORBIDDEN_TUPLES = true;

		// Export the required formats
		config.ACTS = true;
		config.CTWEDGE = false;
		config.PICT = true;

		// ----------- GENERATION -----------
		generator.generateIPMs(config);
		ArrayList<Model> modelsList = generator.getModelsList();

		// ----------- CHECK THE OUTCOME BASED ON THE SET CONFIGURATION -----------

		// First, check the number of models
		assertTrue(modelsList.size() <= N_BENCHMARKS);

		for (Model m : modelsList) {
			ModelConfigurationExtractor extractor = new ModelConfigurationExtractor(Utility.loadModel(m.toString()));

			// Check the cardinality
			assertTrue(extractor.getMaximumCardinality() <= MAX_CARDINALITY);
			assertTrue(extractor.getMinimumCardinality() >= MIN_CARDINALITY);

			// Check the lower and upper bound for integers
			assertTrue(extractor.getLowerBoundForInts() >= LOWER_INT);
			assertTrue(extractor.getUpperBoundForInts() <= UPPER_INT);

			// Check the number of params
			assertTrue(extractor.getNumParams() <= MAX_PARAMS);
			assertTrue(extractor.getNumParams() >= MIN_PARAMS);

			// Check it is in FT
			assertTrue(extractor.hasForbiddenTuples());

			// Check the number of constraints
			assertTrue(extractor.getNumConstraints() <= MAX_CONSTRAINTS);
			assertTrue(extractor.getNumConstraints() >= MIN_CONSTRAINTS);

			// Check that the parameters are all Boolean or Enumeratives or Integers
			for (Parameter p : m.getParameters()) {
				assertTrue(p instanceof Bool || p instanceof Enumerative
						|| p instanceof Range);
			}

			// Check that the model have been exported
			assertFalse(new File("./benchmarks/" + m.getName() + ".ctw").exists());
			assertTrue(new File("./benchmarks/" + m.getName() + ".txt").exists());
			assertTrue(new File("./benchmarks/" + m.getName() + "_pict.txt").exists());

			// Then, delete the files
			new File("./benchmarks/" + m.getName() + ".txt").delete();
			new File("./benchmarks/" + m.getName() + "_pict.txt").delete();

			// Check the complexity
			assertTrue(extractor.getMaxConstraintComplexity() <= MAX_COMPLEXITY);
			assertTrue(extractor.getMinConstraintComplexity() >= MIN_COMPLEXITY);

			// Check the track. Considering that the models are randomly generated, it may
			// happen that a particular type of parameter (i.e., Integers) are not present.
			// In that case, the track is considered MCAC even if it is NUMC, or BOOLC if
			// also enumeratives are missing
			assertTrue(extractor.getModelType() == Track.NUMC || extractor.getModelType() == Track.MCAC
					|| extractor.getModelType() == Track.BOOLC);
		}
	}

	/**
	 * TS4
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
		// NumBenchmarks = 10
		config.N_BENCHMARKS = N_BENCHMARKS;

		// BenchmarkType = MCAC
		config.TRACK = Track.MCAC;

		// Consider only tuple ratio
		config.RATIO = RATIO_TUPLE;
		config.CHECK_TUPLE_RATIO = true;
		config.CHECK_TEST_RATIO = false;

		// Cardinality
		config.MIN_CARDINALITY = MIN_CARDINALITY;
		config.MAX_CARDINALITY = MAX_CARDINALITY;

		// Number of parameters
		config.N_PARAMS_MIN = MIN_PARAMS;
		config.N_PARAMS_MAX = MAX_PARAMS;

		// Complexity
		config.MIN_CONSTRAINTS_COMPLEXITY = MIN_COMPLEXITY;
		config.MAX_CONSTRAINTS_COMPLEXITY = MAX_COMPLEXITY;

		// Number of constraints
		config.N_CONSTRAINTS_MIN = MIN_CONSTRAINTS;
		config.N_CONSTRAINTS_MAX = MAX_CONSTRAINTS;

		// Export the desired formats
		config.ACTS = false;
		config.CTWEDGE = false;
		config.PICT = false;

		// ----------- GENERATION -----------
		generator.generateIPMs(config);
		ArrayList<Model> modelsList = generator.getModelsList();

		// ----------- CHECK THE OUTCOME BASED ON THE SET CONFIGURATION -----------

		// First, check the number of models
		assertTrue(modelsList.size() <= N_BENCHMARKS);

		for (Model m : modelsList) {
			ModelConfigurationExtractor extractor = new ModelConfigurationExtractor(Utility.loadModel(m.toString()));

			// Check the ratio
			assertTrue(extractor.getTupleValidityRatio() <= RATIO_TUPLE);

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
				assertTrue(p instanceof Bool || p instanceof Enumerative);
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
			// In that case, the track is considered BOOLC even if it is NUMC
			assertTrue(extractor.getModelType() == Track.MCAC || extractor.getModelType() == Track.BOOLC);
		}
	}

	/**
	 * TS5
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
		config.N_BENCHMARKS = N_BENCHMARKS;

		// BenchmarkType = MCAC
		config.TRACK = Track.MCAC;

		// Do not consider ratio
		config.CHECK_TUPLE_RATIO = false;
		config.CHECK_TEST_RATIO = false;

		// Cardinality
		config.MIN_CARDINALITY = MIN_CARDINALITY;
		config.MAX_CARDINALITY = MAX_CARDINALITY;

		// Number of parameters
		config.N_PARAMS_MIN = MIN_PARAMS;
		config.N_PARAMS_MAX = MAX_PARAMS;

		// Complexity
		config.MIN_CONSTRAINTS_COMPLEXITY = MIN_COMPLEXITY;
		config.MAX_CONSTRAINTS_COMPLEXITY = MAX_COMPLEXITY;

		// Number of constraints
		config.N_CONSTRAINTS_MIN = MIN_CONSTRAINTS;
		config.N_CONSTRAINTS_MAX = MAX_CONSTRAINTS;

		// Use CNF
		config.CNF = true;

		// Export the desired formats
		config.ACTS = true;
		config.CTWEDGE = true;
		config.PICT = false;

		// ----------- GENERATION -----------
		generator.generateIPMs(config);
		ArrayList<Model> modelsList = generator.getModelsList();

		// ----------- CHECK THE OUTCOME BASED ON THE SET CONFIGURATION -----------

		// First, check the number of models
		assertTrue(modelsList.size() <= N_BENCHMARKS);

		for (Model m : modelsList) {
			ModelConfigurationExtractor extractor = new ModelConfigurationExtractor(Utility.loadModel(m.toString()));

			// Check the cardinality
			assertTrue(extractor.getMaximumCardinality() <= MAX_CARDINALITY);
			assertTrue(extractor.getMinimumCardinality() >= MIN_CARDINALITY);

			// Check the number of params
			assertTrue(extractor.getNumParams() <= MAX_PARAMS);
			assertTrue(extractor.getNumParams() >= MIN_PARAMS);

			// Check CNF
			assertTrue(extractor.isCNF());

			// Check the number of constraints
			assertTrue(extractor.getNumConstraints() <= MAX_CONSTRAINTS);
			assertTrue(extractor.getNumConstraints() >= MIN_CONSTRAINTS);

			// Check that the parameters are all Boolean or Enumeratives
			for (Parameter p : m.getParameters()) {
				assertTrue(p instanceof Bool || p instanceof Enumerative);
			}

			// Check that the model have not been exported
			assertTrue(new File("./benchmarks/" + m.getName() + ".ctw").exists());
			assertTrue(new File("./benchmarks/" + m.getName() + ".txt").exists());
			assertFalse(new File("./benchmarks/" + m.getName() + "_pict.txt").exists());

			// Delete the models
			new File("./benchmarks/" + m.getName() + ".ctw").delete();
			new File("./benchmarks/" + m.getName() + ".txt").delete();

			// Check the complexity
			assertTrue(extractor.getMaxConstraintComplexity() <= MAX_COMPLEXITY);
			assertTrue(extractor.getMinConstraintComplexity() >= MIN_COMPLEXITY);

			// Check the track. Considering that the models are randomly generated, it may
			// happen that a particular type of parameter (i.e., Enumeratives) are not
			// present.
			// In that case, the track is considered BOOLC even if it is NUMC
			assertTrue(extractor.getModelType() == Track.MCAC || extractor.getModelType() == Track.BOOLC);
		}
	}

	/**
	 * TS6
	 * 
	 * 
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 * @throws InterruptedException
	 * @throws IOException
	 * 
	 */
	@Test
	public void ts6() throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		// NumBenchmarks = 10
		config.N_BENCHMARKS = N_BENCHMARKS;

		// BenchmarkType = MCAC
		config.TRACK = Track.MCAC;

		// Check both ratios
		config.P = P;
		config.EPSILON = EPSILON;
		config.RATIO_TEST = RATIO_TEST;
		config.RATIO = RATIO_TUPLE;
		config.CHECK_TUPLE_RATIO = true;
		config.CHECK_TEST_RATIO = true;

		// Cardinality
		config.MIN_CARDINALITY = MIN_CARDINALITY;
		config.MAX_CARDINALITY = MAX_CARDINALITY;

		// Number of parameters
		config.N_PARAMS_MIN = MIN_PARAMS;
		config.N_PARAMS_MAX = MAX_PARAMS;

		// Complexity
		config.MIN_CONSTRAINTS_COMPLEXITY = MIN_COMPLEXITY;
		config.MAX_CONSTRAINTS_COMPLEXITY = MAX_COMPLEXITY;

		// Use constraints as forbidden tuples
		config.FORBIDDEN_TUPLES = true;

		// Number of constraints
		config.N_CONSTRAINTS_MIN = MIN_CONSTRAINTS;
		config.N_CONSTRAINTS_MAX = MAX_CONSTRAINTS;

		// Export the desired formats
		config.ACTS = false;
		config.CTWEDGE = false;
		config.PICT = false;

		// ----------- GENERATION -----------
		generator.generateIPMs(config);
		ArrayList<Model> modelsList = generator.getModelsList();

		// ----------- CHECK THE OUTCOME BASED ON THE SET CONFIGURATION -----------

		// First, check the number of models
		assertTrue(modelsList.size() <= N_BENCHMARKS);

		for (Model m : modelsList) {
			ModelConfigurationExtractor extractor = new ModelConfigurationExtractor(Utility.loadModel(m.toString()));

			// Check the cardinality
			assertTrue(extractor.getMaximumCardinality() <= MAX_CARDINALITY);
			assertTrue(extractor.getMinimumCardinality() >= MIN_CARDINALITY);

			// Check the number of params
			assertTrue(extractor.getNumParams() <= MAX_PARAMS);
			assertTrue(extractor.getNumParams() >= MIN_PARAMS);

			// Check forbidden tuples
			assertTrue(extractor.hasForbiddenTuples());

			// Check the number of constraints
			assertTrue(extractor.getNumConstraints() <= MAX_CONSTRAINTS);
			assertTrue(extractor.getNumConstraints() >= MIN_CONSTRAINTS);

			// Check that the parameters are all Boolean or Enumeratives
			for (Parameter p : m.getParameters()) {
				assertTrue(p instanceof Bool || p instanceof Enumerative);
			}

			// Check the ratio
			assertTrue(extractor.getTupleValidityRatio() <= RATIO_TUPLE);

			// The test ratio has been computed. Evaluate it
			if (m.isRatioExact()) {
				assertTrue(m.getTestValidityRatio() < config.RATIO_TEST);
			} else {
				double ratio = m.getTestValidityRatio();
				// The ratio is not exact. But we can check the interval based on EPSILON
				assertTrue(ratio >= (1 - config.EPSILON) * config.RATIO_TEST
						&& ratio <= (1 + config.EPSILON) * config.RATIO_TEST);
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
			assertTrue(extractor.getModelType() == Track.MCAC || extractor.getModelType() == Track.BOOLC);
		}
	}

	/**
	 * TS7
	 * 
	 * 
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 * @throws InterruptedException
	 * @throws IOException
	 * 
	 */
	@Test
	public void ts7() throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		// NumBenchmarks = 10
		config.N_BENCHMARKS = N_BENCHMARKS;

		// BenchmarkType = BOOLC
		config.TRACK = Track.BOOLC;

		// Do not check ratios
		config.CHECK_TUPLE_RATIO = false;
		config.CHECK_TEST_RATIO = false;

		// Number of parameters
		config.N_PARAMS_MIN = MIN_PARAMS;
		config.N_PARAMS_MAX = MAX_PARAMS;

		// Complexity
		config.MIN_CONSTRAINTS_COMPLEXITY = MIN_COMPLEXITY;
		config.MAX_CONSTRAINTS_COMPLEXITY = MAX_COMPLEXITY;

		// Number of constraints
		config.N_CONSTRAINTS_MIN = MIN_CONSTRAINTS;
		config.N_CONSTRAINTS_MAX = MAX_CONSTRAINTS;

		// Export all models
		config.ACTS = false;
		config.CTWEDGE = true;
		config.PICT = true;

		// ----------- GENERATION -----------
		generator.generateIPMs(config);
		ArrayList<Model> modelsList = generator.getModelsList();

		// ----------- CHECK THE OUTCOME BASED ON THE SET CONFIGURATION -----------

		// First, check the number of models
		assertTrue(modelsList.size() <= N_BENCHMARKS);

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
				assertTrue(p instanceof Bool);
			}

			// Check that the model have not been exported
			assertTrue(new File("./benchmarks/" + m.getName() + ".ctw").exists());
			assertFalse(new File("./benchmarks/" + m.getName() + ".txt").exists());
			assertTrue(new File("./benchmarks/" + m.getName() + "_pict.txt").exists());

			// Delete the models
			new File("./benchmarks/" + m.getName() + ".ctw").delete();
			new File("./benchmarks/" + m.getName() + "_pict.txt").delete();

			// Check the complexity
			assertTrue(extractor.getMaxConstraintComplexity() <= MAX_COMPLEXITY);
			assertTrue(extractor.getMinConstraintComplexity() >= MIN_COMPLEXITY);

			// Check the track
			assertTrue(extractor.getModelType() == Track.BOOLC);
		}
	}

	/**
	 * TS8
	 * 
	 * 
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 * @throws InterruptedException
	 * @throws IOException
	 * 
	 */
	@Test
	public void ts8() throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		// NumBenchmarks = 10
		config.N_BENCHMARKS = N_BENCHMARKS;

		// BenchmarkType = BOOLC
		config.TRACK = Track.BOOLC;

		// Check both ratios
		config.P = P;
		config.EPSILON = EPSILON;
		config.RATIO_TEST = RATIO_TEST;
		config.RATIO = RATIO_TUPLE;
		config.CHECK_TUPLE_RATIO = true;
		config.CHECK_TEST_RATIO = true;

		// Number of parameters
		config.N_PARAMS_MIN = MIN_PARAMS;
		config.N_PARAMS_MAX = MAX_PARAMS;

		// Complexity
		config.MIN_CONSTRAINTS_COMPLEXITY = MIN_COMPLEXITY;
		config.MAX_CONSTRAINTS_COMPLEXITY = MAX_COMPLEXITY;

		// Number of constraints
		config.N_CONSTRAINTS_MIN = MIN_CONSTRAINTS;
		config.N_CONSTRAINTS_MAX = MAX_CONSTRAINTS;

		// No export
		config.ACTS = false;
		config.CTWEDGE = false;
		config.PICT = false;

		// Set CNF
		config.CNF = true;

		// ----------- GENERATION -----------
		generator.generateIPMs(config);
		ArrayList<Model> modelsList = generator.getModelsList();

		// ----------- CHECK THE OUTCOME BASED ON THE SET CONFIGURATION -----------

		// First, check the number of models
		assertTrue(modelsList.size() <= N_BENCHMARKS);

		for (Model m : modelsList) {
			ModelConfigurationExtractor extractor = new ModelConfigurationExtractor(Utility.loadModel(m.toString()));

			// Check the number of params
			assertTrue(extractor.getNumParams() <= MAX_PARAMS);
			assertTrue(extractor.getNumParams() >= MIN_PARAMS);

			// Check CNF
			assertTrue(extractor.isCNF());

			// Check the number of constraints
			assertTrue(extractor.getNumConstraints() <= MAX_CONSTRAINTS);
			assertTrue(extractor.getNumConstraints() >= MIN_CONSTRAINTS);

			// Check that the parameters are all Booleans
			for (Parameter p : m.getParameters()) {
				assertTrue(p instanceof Bool);
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

			// Check the ratio
			assertTrue(extractor.getTupleValidityRatio() <= RATIO_TUPLE);

			// The test ratio has been computed. Evaluate it
			if (m.isRatioExact()) {
				assertTrue(m.getTestValidityRatio() < config.RATIO_TEST);
			} else {
				double ratio = m.getTestValidityRatio();
				// The ratio is not exact. But we can check the interval based on EPSILON
				assertTrue(ratio >= (1 - config.EPSILON) * config.RATIO_TEST
						&& ratio <= (1 + config.EPSILON) * config.RATIO_TEST);
			}
		}
	}

	/**
	 * TS9
	 * 
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 * @throws InterruptedException
	 * @throws IOException
	 * 
	 */
	@Test
	public void ts9() throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		// NumBenchmarks = 10
		config.N_BENCHMARKS = N_BENCHMARKS;

		// BenchmarkType = BOOLC
		config.TRACK = Track.BOOLC;

		// TestValidityRatio
		config.P = P;
		config.EPSILON = EPSILON;
		config.RATIO_TEST = RATIO_TEST;
		config.CHECK_TUPLE_RATIO = false;
		config.CHECK_TEST_RATIO = true;

		// Number of parameters
		config.N_PARAMS_MIN = MIN_PARAMS;
		config.N_PARAMS_MAX = MAX_PARAMS;

		// Complexity
		config.MIN_CONSTRAINTS_COMPLEXITY = MIN_COMPLEXITY;
		config.MAX_CONSTRAINTS_COMPLEXITY = MAX_COMPLEXITY;

		// Number of constraints
		config.N_CONSTRAINTS_MIN = MIN_CONSTRAINTS;
		config.N_CONSTRAINTS_MAX = MAX_CONSTRAINTS;

		// Do not export any format
		config.ACTS = false;
		config.CTWEDGE = false;
		config.PICT = false;

		// Use forbidden tuples
		config.CNF = false;
		config.FORBIDDEN_TUPLES = true;

		// ----------- GENERATION -----------
		generator.generateIPMs(config);
		ArrayList<Model> modelsList = generator.getModelsList();

		// ----------- CHECK THE OUTCOME BASED ON THE SET CONFIGURATION -----------

		// First, check the number of models
		assertTrue(modelsList.size() <= N_BENCHMARKS);
		for (Model m : modelsList) {
			ModelConfigurationExtractor extractor = new ModelConfigurationExtractor(Utility.loadModel(m.toString()));

			// Check the number of params
			assertTrue(extractor.getNumParams() <= MAX_PARAMS);
			assertTrue(extractor.getNumParams() >= MIN_PARAMS);

			// Check ForbiddenTuples
			assertTrue(extractor.hasForbiddenTuples());

			// Check the number of constraints
			assertTrue(extractor.getNumConstraints() <= MAX_CONSTRAINTS);
			assertTrue(extractor.getNumConstraints() >= MIN_CONSTRAINTS);

			// Check that the parameters are all Boolean or Enumeratives or Integers
			for (Parameter p : m.getParameters()) {
				assertTrue(p instanceof Bool);
			}

			// Check that the model have been exported
			assertFalse(new File("./benchmarks/" + m.getName() + ".ctw").exists());
			assertFalse(new File("./benchmarks/" + m.getName() + ".txt").exists());
			assertFalse(new File("./benchmarks/" + m.getName() + "_pict.txt").exists());

			// Check the complexity
			assertTrue(extractor.getMaxConstraintComplexity() <= MAX_COMPLEXITY);
			assertTrue(extractor.getMinConstraintComplexity() >= MIN_COMPLEXITY);

			// The models must be of BOOLC type
			assertTrue(extractor.getModelType() == Track.BOOLC);

			// The ratio has been computed. Evaluate it
			if (m.isRatioExact()) {
				assertTrue(m.getTestValidityRatio() < config.RATIO_TEST);
			} else {
				double ratio = m.getTestValidityRatio();
				// The ratio is not exact. But we can check the interval based on EPSILON
				assertTrue(ratio >= (1 - config.EPSILON) * config.RATIO_TEST
						&& ratio <= (1 + config.EPSILON) * config.RATIO_TEST);
			}
		}
	}

	/**
	 * TS10
	 * 
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 * @throws InterruptedException
	 * @throws IOException
	 * 
	 */
	@Test
	public void ts10() throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		// NumBenchmarks = 10
		config.N_BENCHMARKS = N_BENCHMARKS;

		// BenchmarkType = MCA
		config.TRACK = Track.MCA;

		// Do not check ratio
		config.CHECK_TUPLE_RATIO = false;
		config.CHECK_TEST_RATIO = false;

		// Cardinality
		config.MIN_CARDINALITY = MIN_CARDINALITY;
		config.MAX_CARDINALITY = MAX_CARDINALITY;

		// Number of parameters
		config.N_PARAMS_MIN = MIN_PARAMS;
		config.N_PARAMS_MAX = MAX_PARAMS;

		// Export in the desired formats
		config.ACTS = true;
		config.CTWEDGE = true;
		config.PICT = true;

		// ----------- GENERATION -----------
		generator.generateIPMs(config);
		ArrayList<Model> modelsList = generator.getModelsList();

		// ----------- CHECK THE OUTCOME BASED ON THE SET CONFIGURATION -----------

		// First, check the number of models
		assertTrue(modelsList.size() == N_BENCHMARKS);

		for (Model m : modelsList) {
			ModelConfigurationExtractor extractor = new ModelConfigurationExtractor(Utility.loadModel(m.toString()));

			// Check the number of params
			assertTrue(extractor.getNumParams() <= MAX_PARAMS);
			assertTrue(extractor.getNumParams() >= MIN_PARAMS);

			// Check the cardinality
			assertTrue(extractor.getMaximumCardinality() <= MAX_CARDINALITY);
			assertTrue(extractor.getMinimumCardinality() >= MIN_CARDINALITY);

			// Check the number of constraints
			assertTrue(extractor.getNumConstraints() == 0);

			// Check that the parameters are all Booleans or enumeratives
			for (Parameter p : m.getParameters()) {
				assertTrue(p instanceof Bool || p instanceof Enumerative);
			}

			// Check that the model have been exported accordingly to the request
			assertTrue(new File("./benchmarks/" + m.getName() + ".ctw").exists());
			assertTrue(new File("./benchmarks/" + m.getName() + ".txt").exists());
			assertTrue(new File("./benchmarks/" + m.getName() + "_pict.txt").exists());

			// Remove the files
			new File("./benchmarks/" + m.getName() + ".ctw").delete();
			new File("./benchmarks/" + m.getName() + ".txt").delete();
			new File("./benchmarks/" + m.getName() + "_pict.txt").delete();

			// Check the track
			assertTrue(extractor.getModelType() == Track.MCA || extractor.getModelType() == Track.UNIFORM_ALL
					|| extractor.getModelType() == Track.UNIFORM_BOOLEAN);
		}
	}

	/**
	 * TS11
	 * 
	 * 
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 * @throws InterruptedException
	 * @throws IOException
	 * 
	 */
	@Test
	public void ts11() throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		// NumBenchmarks = 10
		config.N_BENCHMARKS = N_BENCHMARKS;

		// BenchmarkType = UNIFORM_ALL
		config.TRACK = Track.UNIFORM_ALL;

		// Do not check ratio
		config.CHECK_TUPLE_RATIO = false;
		config.CHECK_TEST_RATIO = false;

		// Cardinality
		config.MIN_CARDINALITY = MIN_CARDINALITY;
		config.MAX_CARDINALITY = MAX_CARDINALITY;

		// Number of parameters
		config.N_PARAMS_MIN = MIN_PARAMS;
		config.N_PARAMS_MAX = MAX_PARAMS;

		// Export in the desired formats
		config.ACTS = false;
		config.CTWEDGE = false;
		config.PICT = false;

		// ----------- GENERATION -----------
		generator.generateIPMs(config);
		ArrayList<Model> modelsList = generator.getModelsList();

		// ----------- CHECK THE OUTCOME BASED ON THE SET CONFIGURATION -----------

		// First, check the number of models
		assertTrue(modelsList.size() == N_BENCHMARKS);

		for (Model m : modelsList) {
			ModelConfigurationExtractor extractor = new ModelConfigurationExtractor(Utility.loadModel(m.toString()));

			// Check the number of params
			assertTrue(extractor.getNumParams() <= MAX_PARAMS);
			assertTrue(extractor.getNumParams() >= MIN_PARAMS);

			// Check the cardinality
			assertTrue(extractor.getMaximumCardinality() <= MAX_CARDINALITY);
			assertTrue(extractor.getMinimumCardinality() >= MIN_CARDINALITY);

			// Check the number of constraints
			assertTrue(extractor.getNumConstraints() == 0);

			// Check that the parameters are all Booleans or enumeratives
			for (Parameter p : m.getParameters()) {
				assertTrue(p instanceof Bool || p instanceof Enumerative);
			}

			// Check that the model have been exported accordingly to the request
			assertFalse(new File("./benchmarks/" + m.getName() + ".ctw").exists());
			assertFalse(new File("./benchmarks/" + m.getName() + ".txt").exists());
			assertFalse(new File("./benchmarks/" + m.getName() + "_pict.txt").exists());

			// Check the track
			assertTrue(
					extractor.getModelType() == Track.UNIFORM_ALL || extractor.getModelType() == Track.UNIFORM_BOOLEAN);
		}
	}

	/**
	 * TS12
	 * 
	 * 
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 * @throws InterruptedException
	 * @throws IOException
	 * 
	 */
	@Test
	public void ts12() throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		// NumBenchmarks = 10
		config.N_BENCHMARKS = N_BENCHMARKS;

		// BenchmarkType = UNIFORM_BOOLEAN
		config.TRACK = Track.UNIFORM_BOOLEAN;

		// Do not check ratio
		config.CHECK_TUPLE_RATIO = false;
		config.CHECK_TEST_RATIO = false;

		// Number of parameters
		config.N_PARAMS_MIN = MIN_PARAMS;
		config.N_PARAMS_MAX = MAX_PARAMS;

		// Export in the desired formats
		config.ACTS = true;
		config.CTWEDGE = true;
		config.PICT = true;

		// ----------- GENERATION -----------
		generator.generateIPMs(config);
		ArrayList<Model> modelsList = generator.getModelsList();

		// ----------- CHECK THE OUTCOME BASED ON THE SET CONFIGURATION -----------

		// First, check the number of models
		assertTrue(modelsList.size() == N_BENCHMARKS);

		for (Model m : modelsList) {
			ModelConfigurationExtractor extractor = new ModelConfigurationExtractor(Utility.loadModel(m.toString()));

			// Check the number of params
			assertTrue(extractor.getNumParams() <= MAX_PARAMS);
			assertTrue(extractor.getNumParams() >= MIN_PARAMS);

			// Check the cardinality
			assertTrue(extractor.getMaximumCardinality() <= MAX_CARDINALITY);
			assertTrue(extractor.getMinimumCardinality() >= MIN_CARDINALITY);

			// Check the number of constraints
			assertTrue(extractor.getNumConstraints() == 0);

			// Check that the parameters are all Booleans
			for (Parameter p : m.getParameters()) {
				assertTrue(p instanceof Bool);
			}

			// Check that the model have been exported accordingly to the request
			assertTrue(new File("./benchmarks/" + m.getName() + ".ctw").exists());
			assertTrue(new File("./benchmarks/" + m.getName() + ".txt").exists());
			assertTrue(new File("./benchmarks/" + m.getName() + "_pict.txt").exists());

			// Then, delete the files
			new File("./benchmarks/" + m.getName() + ".ctw").delete();
			new File("./benchmarks/" + m.getName() + ".txt").delete();
			new File("./benchmarks/" + m.getName() + "_pict.txt").delete();

			// Check the track
			assertTrue(extractor.getModelType() == Track.UNIFORM_BOOLEAN);
		}
	}

	/**
	 * TS13
	 * 
	 * 
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 * @throws InterruptedException
	 * @throws IOException
	 * 
	 */
	@Test
	public void ts13() throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		// NumBenchmarks = 10
		config.N_BENCHMARKS = N_BENCHMARKS;

		// BenchmarkType = BOOLC
		config.TRACK = Track.BOOLC;

		// Check both ratios
		config.P = P;
		config.EPSILON = EPSILON;
		config.RATIO_TEST = RATIO_TEST;
		config.RATIO = RATIO_TUPLE;
		config.CHECK_TUPLE_RATIO = true;
		config.CHECK_TEST_RATIO = true;

		// Number of parameters
		config.N_PARAMS_MIN = MIN_PARAMS;
		config.N_PARAMS_MAX = MAX_PARAMS;

		// Complexity
		config.MIN_CONSTRAINTS_COMPLEXITY = MIN_COMPLEXITY;
		config.MAX_CONSTRAINTS_COMPLEXITY = MAX_COMPLEXITY;

		// Number of constraints
		config.N_CONSTRAINTS_MIN = MIN_CONSTRAINTS;
		config.N_CONSTRAINTS_MAX = MAX_CONSTRAINTS;

		// Export all models
		config.ACTS = true;
		config.CTWEDGE = false;
		config.PICT = true;

		// ----------- GENERATION -----------
		generator.generateIPMs(config);
		ArrayList<Model> modelsList = generator.getModelsList();

		// ----------- CHECK THE OUTCOME BASED ON THE SET CONFIGURATION -----------

		// First, check the number of models
		assertTrue(modelsList.size() <= N_BENCHMARKS);

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
				assertTrue(p instanceof Bool);
			}

			// Check the ratio
			assertTrue(extractor.getTupleValidityRatio() <= RATIO_TUPLE);

			// The test ratio has been computed. Evaluate it
			if (m.isRatioExact()) {
				assertTrue(m.getTestValidityRatio() < config.RATIO_TEST);
			} else {
				double ratio = m.getTestValidityRatio();
				// The ratio is not exact. But we can check the interval based on EPSILON
				assertTrue(ratio >= (1 - config.EPSILON) * config.RATIO_TEST
						&& ratio <= (1 + config.EPSILON) * config.RATIO_TEST);
			}

			// Check that the model have not been exported
			assertFalse(new File("./benchmarks/" + m.getName() + ".ctw").exists());
			assertTrue(new File("./benchmarks/" + m.getName() + ".txt").exists());
			assertTrue(new File("./benchmarks/" + m.getName() + "_pict.txt").exists());

			// Delete the models
			new File("./benchmarks/" + m.getName() + ".txt").delete();
			new File("./benchmarks/" + m.getName() + "_pict.txt").delete();

			// Check the complexity
			assertTrue(extractor.getMaxConstraintComplexity() <= MAX_COMPLEXITY);
			assertTrue(extractor.getMinConstraintComplexity() >= MIN_COMPLEXITY);

			// Check the track
			assertTrue(extractor.getModelType() == Track.BOOLC);
		}
	}

	/**
	 * TS14
	 * 
	 * 
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 * @throws InterruptedException
	 * @throws IOException
	 * 
	 */
	@Test
	public void ts14() throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		// NumBenchmarks = 10
		config.N_BENCHMARKS = N_BENCHMARKS;

		// BenchmarkType = UNIFORM_ALL
		config.TRACK = Track.UNIFORM_ALL;

		// Do not check ratio
		config.CHECK_TUPLE_RATIO = false;
		config.CHECK_TEST_RATIO = false;

		// Cardinality
		config.MIN_CARDINALITY = MIN_CARDINALITY;
		config.MAX_CARDINALITY = MAX_CARDINALITY;

		// Number of parameters
		config.N_PARAMS_MIN = MIN_PARAMS;
		config.N_PARAMS_MAX = MAX_PARAMS;

		// Export in the desired formats
		config.ACTS = true;
		config.CTWEDGE = true;
		config.PICT = true;

		// ----------- GENERATION -----------
		generator.generateIPMs(config);
		ArrayList<Model> modelsList = generator.getModelsList();

		// ----------- CHECK THE OUTCOME BASED ON THE SET CONFIGURATION -----------

		// First, check the number of models
		assertTrue(modelsList.size() == N_BENCHMARKS);

		for (Model m : modelsList) {
			ModelConfigurationExtractor extractor = new ModelConfigurationExtractor(Utility.loadModel(m.toString()));

			// Check the number of params
			assertTrue(extractor.getNumParams() <= MAX_PARAMS);
			assertTrue(extractor.getNumParams() >= MIN_PARAMS);

			// Check the cardinality
			assertTrue(extractor.getMaximumCardinality() <= MAX_CARDINALITY);
			assertTrue(extractor.getMinimumCardinality() >= MIN_CARDINALITY);

			// Check the number of constraints
			assertTrue(extractor.getNumConstraints() == 0);

			// Check that the parameters are all Booleans or enumeratives
			for (Parameter p : m.getParameters()) {
				assertTrue(p instanceof Bool || p instanceof Enumerative);
			}

			// Check that the model have been exported accordingly to the request
			assertTrue(new File("./benchmarks/" + m.getName() + ".ctw").exists());
			assertTrue(new File("./benchmarks/" + m.getName() + ".txt").exists());
			assertTrue(new File("./benchmarks/" + m.getName() + "_pict.txt").exists());

			// Delete the created files
			new File("./benchmarks/" + m.getName() + ".ctw").delete();
			new File("./benchmarks/" + m.getName() + ".txt").delete();
			new File("./benchmarks/" + m.getName() + "_pict.txt").delete();

			// Check the track
			assertTrue(
					extractor.getModelType() == Track.UNIFORM_ALL || extractor.getModelType() == Track.UNIFORM_BOOLEAN);
		}
	}

	/**
	 * TS15
	 * 
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 * @throws InterruptedException
	 * @throws IOException
	 * 
	 */
	@Test
	public void ts15() throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		// NumBenchmarks = 10
		config.N_BENCHMARKS = N_BENCHMARKS;

		// BenchmarkType = MCA
		config.TRACK = Track.MCA;

		// Do not check ratio
		config.CHECK_TUPLE_RATIO = false;
		config.CHECK_TEST_RATIO = false;

		// Cardinality
		config.MIN_CARDINALITY = MIN_CARDINALITY;
		config.MAX_CARDINALITY = MAX_CARDINALITY;

		// Number of parameters
		config.N_PARAMS_MIN = MIN_PARAMS;
		config.N_PARAMS_MAX = MAX_PARAMS;

		// Export in the desired formats
		config.ACTS = false;
		config.CTWEDGE = false;
		config.PICT = false;

		// ----------- GENERATION -----------
		generator.generateIPMs(config);
		ArrayList<Model> modelsList = generator.getModelsList();

		// ----------- CHECK THE OUTCOME BASED ON THE SET CONFIGURATION -----------

		// First, check the number of models
		assertTrue(modelsList.size() == N_BENCHMARKS);

		for (Model m : modelsList) {
			ModelConfigurationExtractor extractor = new ModelConfigurationExtractor(Utility.loadModel(m.toString()));

			// Check the number of params
			assertTrue(extractor.getNumParams() <= MAX_PARAMS);
			assertTrue(extractor.getNumParams() >= MIN_PARAMS);

			// Check the cardinality
			assertTrue(extractor.getMaximumCardinality() <= MAX_CARDINALITY);
			assertTrue(extractor.getMinimumCardinality() >= MIN_CARDINALITY);

			// Check the number of constraints
			assertTrue(extractor.getNumConstraints() == 0);

			// Check that the parameters are all Booleans or enumeratives
			for (Parameter p : m.getParameters()) {
				assertTrue(p instanceof Bool || p instanceof Enumerative);
			}

			// Check that the model have been exported accordingly to the request
			assertFalse(new File("./benchmarks/" + m.getName() + ".ctw").exists());
			assertFalse(new File("./benchmarks/" + m.getName() + ".txt").exists());
			assertFalse(new File("./benchmarks/" + m.getName() + "_pict.txt").exists());

			// Check the track
			assertTrue(extractor.getModelType() == Track.MCA || extractor.getModelType() == Track.UNIFORM_ALL
					|| extractor.getModelType() == Track.UNIFORM_BOOLEAN);
		}
	}

	/**
	 * TS16
	 * 
	 * 
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 * @throws InterruptedException
	 * @throws IOException
	 * 
	 */
	@Test
	public void ts16() throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		// NumBenchmarks = 10
		config.N_BENCHMARKS = N_BENCHMARKS;

		// BenchmarkType = UNIFORM_BOOLEAN
		config.TRACK = Track.UNIFORM_BOOLEAN;

		// Do not check ratio
		config.CHECK_TUPLE_RATIO = false;
		config.CHECK_TEST_RATIO = false;

		// Number of parameters
		config.N_PARAMS_MIN = MIN_PARAMS;
		config.N_PARAMS_MAX = MAX_PARAMS;

		// Export in the desired formats
		config.ACTS = false;
		config.CTWEDGE = false;
		config.PICT = false;

		// ----------- GENERATION -----------
		generator.generateIPMs(config);
		ArrayList<Model> modelsList = generator.getModelsList();

		// ----------- CHECK THE OUTCOME BASED ON THE SET CONFIGURATION -----------

		// First, check the number of models
		assertTrue(modelsList.size() == N_BENCHMARKS);

		for (Model m : modelsList) {
			ModelConfigurationExtractor extractor = new ModelConfigurationExtractor(Utility.loadModel(m.toString()));

			// Check the number of params
			assertTrue(extractor.getNumParams() <= MAX_PARAMS);
			assertTrue(extractor.getNumParams() >= MIN_PARAMS);

			// Check the cardinality
			assertTrue(extractor.getMaximumCardinality() <= MAX_CARDINALITY);
			assertTrue(extractor.getMinimumCardinality() >= MIN_CARDINALITY);

			// Check the number of constraints
			assertTrue(extractor.getNumConstraints() == 0);

			// Check that the parameters are all Booleans
			for (Parameter p : m.getParameters()) {
				assertTrue(p instanceof Bool);
			}

			// Check that the model have been exported accordingly to the request
			assertFalse(new File("./benchmarks/" + m.getName() + ".ctw").exists());
			assertFalse(new File("./benchmarks/" + m.getName() + ".txt").exists());
			assertFalse(new File("./benchmarks/" + m.getName() + "_pict.txt").exists());

			// Check the track
			assertTrue(extractor.getModelType() == Track.UNIFORM_BOOLEAN);
		}
	}

	/**
	 * TS17
	 * 
	 * 
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 * @throws InterruptedException
	 * @throws IOException
	 * 
	 */
	@Test
	public void ts17() throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		// NumBenchmarks = 10
		config.N_BENCHMARKS = N_BENCHMARKS;

		// BenchmarkType = MCAC
		config.TRACK = Track.MCAC;

		// TestValidityRatio
		config.P = P;
		config.EPSILON = EPSILON;
		config.RATIO_TEST = RATIO_TEST;
		config.CHECK_TUPLE_RATIO = false;
		config.CHECK_TEST_RATIO = true;

		// Cardinality
		config.MIN_CARDINALITY = MIN_CARDINALITY;
		config.MAX_CARDINALITY = MAX_CARDINALITY;

		// Number of parameters
		config.N_PARAMS_MIN = MIN_PARAMS;
		config.N_PARAMS_MAX = MAX_PARAMS;

		// Complexity
		config.MIN_CONSTRAINTS_COMPLEXITY = MIN_COMPLEXITY;
		config.MAX_CONSTRAINTS_COMPLEXITY = MAX_COMPLEXITY;

		// Number of constraints
		config.N_CONSTRAINTS_MIN = MIN_CONSTRAINTS;
		config.N_CONSTRAINTS_MAX = MAX_CONSTRAINTS;

		// Use forbidden tuples
		config.CNF = false;
		config.FORBIDDEN_TUPLES = true;

		// Export the desired formats
		config.ACTS = false;
		config.CTWEDGE = true;
		config.PICT = true;

		// ----------- GENERATION -----------
		generator.generateIPMs(config);
		ArrayList<Model> modelsList = generator.getModelsList();

		// ----------- CHECK THE OUTCOME BASED ON THE SET CONFIGURATION -----------

		// First, check the number of models
		assertTrue(modelsList.size() <= N_BENCHMARKS);

		for (Model m : modelsList) {
			ModelConfigurationExtractor extractor = new ModelConfigurationExtractor(Utility.loadModel(m.toString()));

			// Check the cardinality
			assertTrue(extractor.getMaximumCardinality() <= MAX_CARDINALITY);
			assertTrue(extractor.getMinimumCardinality() >= MIN_CARDINALITY);

			// Check the number of params
			assertTrue(extractor.getNumParams() <= MAX_PARAMS);
			assertTrue(extractor.getNumParams() >= MIN_PARAMS);

			// Check forbidden tuples
			assertTrue(extractor.hasForbiddenTuples());

			// The ratio has been computed. Evaluate it
			if (m.isRatioExact()) {
				assertTrue(m.getTestValidityRatio() < config.RATIO_TEST);
			} else {
				double ratio = m.getTestValidityRatio();
				// The ratio is not exact. But we can check the interval based on EPSILON
				assertTrue(ratio >= (1 - config.EPSILON) * config.RATIO_TEST
						&& ratio <= (1 + config.EPSILON) * config.RATIO_TEST);
			}

			// Check the number of constraints
			assertTrue(extractor.getNumConstraints() <= MAX_CONSTRAINTS);
			assertTrue(extractor.getNumConstraints() >= MIN_CONSTRAINTS);

			// Check that the parameters are all Boolean or Enumeratives
			for (Parameter p : m.getParameters()) {
				assertTrue(p instanceof Bool || p instanceof Enumerative);
			}

			// Check that the model have not been exported
			assertFalse(new File("./benchmarks/" + m.getName() + ".ctw").exists());
			assertTrue(new File("./benchmarks/" + m.getName() + ".txt").exists());
			assertTrue(new File("./benchmarks/" + m.getName() + "_pict.txt").exists());

			// Delete the files
			new File("./benchmarks/" + m.getName() + ".txt").delete();
			new File("./benchmarks/" + m.getName() + "_pict.txt").delete();

			// Check the complexity
			assertTrue(extractor.getMaxConstraintComplexity() <= MAX_COMPLEXITY);
			assertTrue(extractor.getMinConstraintComplexity() >= MIN_COMPLEXITY);

			// Check the track. Considering that the models are randomly generated, it may
			// happen that a particular type of parameter (i.e., Enumeratives) are not
			// present.
			assertTrue(extractor.getModelType() == Track.MCAC || extractor.getModelType() == Track.BOOLC);
		}
	}
}
