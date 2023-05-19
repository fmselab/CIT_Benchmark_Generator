package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.api.SolverException;

import ctwedge.util.NotConvertableModel;
import generators.Category;
import generators.Generator;
import generators.GeneratorConfiguration;
import generators.Track;
import generators.WithConstraintGenerator;
import generators.WithConstraintGeneratorCNF;
import generators.WithoutConstraintGenerator;
import generators.WithoutConstraintGeneratorSameCardinality;
import models.Model;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import util.ModelConfigurationExtractor;

public class BenchmarkGeneratorCLI implements Callable<Integer> {

	private static final Logger LOGGER = LogManager.getRootLogger();

	@Parameters(index = "0", description = "The category for the benchmark to be generated (UNIFORM_BOOLEAN, UNIFORM_ALL, MCA, BOOLC, MCAC, NUMC, HIGHLY_CONSTRAINED, CNF).")
	String trackStr;

	@Parameters(index = "1", description = "The number of benchmarks to generate.")
	int nTests = 1;

	@Option(names = "-acts", description = "Export in ACTS format. By default it is disabled.")
	private boolean acts = false;

	@Option(names = "-ctw", description = "Export in CTWedge format. By default it is enabled.")
	private boolean ctwedge = true;

	@Option(names = "-pict", description = "Export in PICT format. By default it is enabled.")
	private boolean pict = false;

	@Option(names = "-d", description = "Destination folder. By default it is ./benchmarks/.")
	private String destinationFolder = "./benchmarks/";

	@Option(names = "-kmin", description = "Minimum number of parameters. By default it is 1.")
	private int kmin = 1;

	@Option(names = "-kmax", description = "Maximum number of parameters. By default it is 500.")
	private int kmax = 500;

	@Option(names = "-vmin", description = "Minimum cardinality. By default it is 2.")
	private int vmin = 2;

	@Option(names = "-vmax", description = "Maximum cardinality. By default it is 500.")
	private int vmax = 500;

	@Option(names = "-vIntLow", description = "Lower bound for integer ranges. By default it is -100.")
	private int vIntLow = -100;

	@Option(names = "-vIntUp", description = "Upper bound for integer ranges. By default it is 100.")
	private int vIntUp = 100;

	@Option(names = "-cmin", description = "Minimum number of constraints. By default it is 2.")
	private int cmin = 2;

	@Option(names = "-cmax", description = "Maximum number of constraints. By default it is 100.")
	private int cmax = 100;

	@Option(names = "-dmin", description = "Minimum complexity (i.e., number of logical operators) for each constraint. By default it is 2.")
	private int dmin = 2;

	@Option(names = "-dmax", description = "Maximum complexity (i.e., number of logical operators) for each constraint. By default it is 10.")
	private int dmax = 10;

	@Option(names = "-chkTestRatio", description = "Check test ratio.")
	private boolean chkTestRatio = false;

	@Option(names = "-chkTupleRatio", description = "Check tuple ratio.")
	private boolean chkTupleRatio = false;

	@Option(names = "-r", description = "Maximum accepted tuple validity ratio. By default it is 0.01.")
	private double r = 0.01;

	@Option(names = "-rTest", description = "Maximum accepted test validity ratio. By default it is 0.01.")
	private double rTest = 0.01;

	@Option(names = "-similar", description = "Given a CTWedge model, it generates a model similar to that.")
	private String similarModel = null;

	@Option(names = "-T", description = "Number of tests to be generated when computing the test validity ratio, if MDDs cannot be used. By default it is 1000")
	private int T = 1000;

	@Option(names = "-epsilon", description = "The accepted error when computing the test validity ratio, if MDDs cannot be used. By default it is 0.1")
	private double epsilon = 0.1;

	/**
	 * The list in which all the generated models are stored
	 */
	ArrayList<Model> modelsList = new ArrayList<>();

	public static void main(String[] args) throws IOException {
		LOGGER.setLevel(Level.DEBUG);
		BenchmarkGeneratorCLI cliGenerator = new BenchmarkGeneratorCLI();
		int exitCode = new CommandLine(cliGenerator).execute(args);
		System.exit(exitCode);
	}

	/**
	 * Generator
	 */
	@Override
	public Integer call() throws Exception {
		generate();
		return null;
	}

	/**
	 * Generate the test benchmarks based on the parameters received by the user
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 */
	private void generate() throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		// First set the configurations
		setConfigurations();

		// Then, based on the chosen track, generate benchmarks
		generateIPMs();
	}

	/**
	 * Based on the chosen track, the benchmarks are generated
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws InvalidConfigurationException
	 * @throws SolverException
	 */
	public void generateIPMs()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		// Clear the previously generated models
		modelsList.clear();

		switch (GeneratorConfiguration.TRACK) {
		case BOOLC:
			generateBOOLC();
			break;
		case CNF:
			generateCNF();
			break;
		case HIGHLY_CONSTRAINED:
			generateHIGHLY_CONSTRAINED();
			break;
		case MCA:
			generateMCA();
			break;
		case MCAC:
			generateMCAC();
			break;
		case NUMC:
			generateNUMC();
			break;
		case UNIFORM_ALL:
			generateUNIFORM_ALL();
			break;
		case UNIFORM_BOOLEAN:
			generateUNIFORM_BOOLEAN();
			break;
		default:
			break;
		}
	}

	/**
	 * Sets the generator configuration based on the parameters received as input,
	 * or based on the model specified by the user
	 * 
	 * @throws InterruptedException
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 */
	public void setConfigurations() throws InterruptedException, InvalidConfigurationException, SolverException {
		GeneratorConfiguration.N_BENCHMARKS = nTests;
		if (similarModel == null) {
			LOGGER.debug("Configurations set by the user");
			GeneratorConfiguration.N_PARAMS_MAX = kmax;
			GeneratorConfiguration.N_PARAMS_MIN = kmin;
			GeneratorConfiguration.MAX_CARDINALITY = vmax;
			GeneratorConfiguration.MIN_CARDINALITY = vmin;
			GeneratorConfiguration.LOWER_BOUND_INT = vIntLow;
			GeneratorConfiguration.UPPER_BOUND_INT = vIntUp;
			GeneratorConfiguration.RATIO = r;
			GeneratorConfiguration.RATIO_TEST = rTest;
			GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY = dmax;
			GeneratorConfiguration.MIN_CONSTRAINTS_COMPLEXITY = dmin;
			GeneratorConfiguration.N_CONSTRAINTS_MAX = cmax;
			GeneratorConfiguration.N_CONSTRAINTS_MIN = cmin;
			GeneratorConfiguration.N = T;
			GeneratorConfiguration.EPSILON = epsilon;
			GeneratorConfiguration.TRACK = Track.valueOf(trackStr);
			GeneratorConfiguration.CHECK_TEST_RATIO = chkTestRatio;
			GeneratorConfiguration.CHECK_TUPLE_RATIO = chkTupleRatio;
		} else {
			// Extract the configuration from that of model given by the user
			LOGGER.debug("Configurations read from the baseline model");
			setConfigurationsFromFile(similarModel);
		}

		// Then, set the export format
		GeneratorConfiguration.ACTS = acts;
		GeneratorConfiguration.PICT = pict;
		GeneratorConfiguration.CTWEDGE = ctwedge;
	}

	/**
	 * Sets the configuration based on the model received as input
	 * 
	 * @param modelPath the model path
	 * @throws InterruptedException
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 */
	public static void setConfigurationsFromFile(String modelPath)
			throws InterruptedException, InvalidConfigurationException, SolverException {
		ModelConfigurationExtractor extractor = new ModelConfigurationExtractor(modelPath);
		GeneratorConfiguration.N_PARAMS_MAX = extractor.getNumParams();
		GeneratorConfiguration.N_PARAMS_MIN = extractor.getNumParams();
		GeneratorConfiguration.MAX_CARDINALITY = extractor.getMaximumCardinality();
		GeneratorConfiguration.MIN_CARDINALITY = extractor.getMinimumCardinality();
		GeneratorConfiguration.LOWER_BOUND_INT = extractor.getLowerBoundForInts();
		GeneratorConfiguration.UPPER_BOUND_INT = extractor.getUpperBoundForInts();
		try {
			GeneratorConfiguration.RATIO = extractor.getTupleValidityRatio();
		} catch (NotConvertableModel c) {
			GeneratorConfiguration.RATIO = 1;
		}
		GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY = extractor.getMaxConstraintComplexity();
		GeneratorConfiguration.MIN_CONSTRAINTS_COMPLEXITY = extractor.getMinConstraintComplexity();
		GeneratorConfiguration.N_CONSTRAINTS_MAX = extractor.getNumConstraints();
		GeneratorConfiguration.N_CONSTRAINTS_MIN = extractor.getNumConstraints();
		GeneratorConfiguration.TRACK = extractor.getModelType();
	}

	/**
	 * Generates HIGHLY_CONSTRAINED instances
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 */
	public void generateHIGHLY_CONSTRAINED()
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		// The generator that considers constraints
		Generator g = new WithConstraintGenerator();

		for (int i = 0; i < GeneratorConfiguration.N_BENCHMARKS; i++) {
			Model m1;
			// Keep generating the same model until a solvable one is found
			m1 = generateWithGenerator(g, Category.CONSTRAINTS_WITH_RELATIONAL);
			if (m1 != null) {
				m1.setName(Track.HIGHLY_CONSTRAINED + "_" + i);
				LOGGER.debug("Added a new model: " + m1.getName());

				modelsList.add(m1);

				// Export the model
				exportModel(m1);
			}
		}
	}

	/**
	 * Generates BOOLC instances
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 */
	public void generateBOOLC()
			throws IOException, InvalidConfigurationException, SolverException, InterruptedException {
		// The generator that considers constraints
		Generator g = new WithConstraintGenerator();

		for (int i = 0; i < GeneratorConfiguration.N_BENCHMARKS; i++) {
			Model m1;
			// Keep generating the same model until a solvable one is found
			m1 = generateWithGenerator(g, Category.ONLY_BOOLEAN);
			if (m1 != null) {
				m1.setName(Track.BOOLC + "_" + i);
				LOGGER.debug("Added a new model: " + m1.getName());

				modelsList.add(m1);

				// Export the model
				exportModel(m1);
			}
		}
	}

	/**
	 * Generates a model with a given generator and category, and verifies its
	 * solvability
	 * 
	 * @param generator       the generator
	 * @param category        the category
	 * @param checkTupleRatio check the tuple validity ratio?
	 * @param checkTestRatio  check the test validity ratio?
	 * @return the model
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private Model generateWithGenerator(Generator generator, Category category)
			throws InvalidConfigurationException, SolverException, InterruptedException, IOException {
		boolean isSolvable = false;
		Model m = null;
		int i = 0;
		do {
			m = generator.generate(category);
			isSolvable = m.isSolvable();

			if (isSolvable) {
				LOGGER.debug("The generated model is solvable");
				// Check the tuple validity ratio
				if (GeneratorConfiguration.CHECK_TUPLE_RATIO) {
					LOGGER.debug("Checking TUPLE VALIDITY RATIO");
					try {
						double ratio = m.getTupleValidityRatio();
						LOGGER.debug("TUPLE VALIDITY RATIO " + Double.toString(ratio));
						if (ratio > GeneratorConfiguration.RATIO)
							isSolvable = false;
					} catch (InterruptedException e) {
					}
				}

				// Check the test validity ratio
				if (GeneratorConfiguration.CHECK_TEST_RATIO) {
					LOGGER.debug("Checking TEST VALIDITY RATIO");
					double ratio = m.getTestValidityRatio();
					LOGGER.debug("TEST VALIDITY RATIO " + Double.toString(ratio));
					// If the ratio is exact, it means that it has been computed with the MDD, thus
					// we can use it to decide whether the model is correct or not. Otherwise, we
					// can only work with probabilities
					if (m.isRatioExact() && ratio > GeneratorConfiguration.RATIO_TEST)
						isSolvable = false;
				}
			}
			i++;
			if (!isSolvable) {
				LOGGER.debug("Generated a non solvable / compliant model. Trying another one");
				m = null;
			}
		} while (!isSolvable && i < GeneratorConfiguration.N_ATTEMPTS);
		return m;
	}

	/**
	 * Generates MCAC instances
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 */
	public void generateMCAC()
			throws IOException, InvalidConfigurationException, SolverException, InterruptedException {
		// The generator that considers constraints
		Generator g = new WithConstraintGenerator();

		for (int i = 0; i < GeneratorConfiguration.N_BENCHMARKS; i++) {
			Model m1;
			// Keep generating the same model until a solvable one is found
			m1 = generateWithGenerator(g, Category.ALSO_ENUMS);
			if (m1 != null) {
				m1.setName(Track.MCAC + "_" + i);
				LOGGER.debug("Added a new model: " + m1.getName());

				modelsList.add(m1);

				// Export the model
				exportModel(m1);
			}
		}
	}

	/**
	 * Generates CNF instances
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 */
	public void generateCNF() throws IOException, InvalidConfigurationException, SolverException, InterruptedException {
		// The generator that considers constraints
		Generator g = new WithConstraintGeneratorCNF();

		for (int i = 0; i < GeneratorConfiguration.N_BENCHMARKS; i++) {
			Model m1;
			// Keep generating the same model until a solvable one is found
			m1 = generateWithGenerator(g, Category.ALSO_ENUMS);
			if (m1 != null) {
				m1.setName(Track.CNF + "_" + i);
				LOGGER.debug("Added a new model: " + m1.getName());

				modelsList.add(m1);

				// Export the model
				exportModel(m1);
			}
		}
	}

	/**
	 * Generates NUMC instances
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 */
	public void generateNUMC()
			throws IOException, InvalidConfigurationException, SolverException, InterruptedException {
		// The generator that considers constraints
		Generator g = new WithConstraintGenerator();

		for (int i = 0; i < GeneratorConfiguration.N_BENCHMARKS; i++) {
			Model m1;
			// Keep generating the same model until a solvable one is found
			m1 = generateWithGenerator(g, Category.CONSTRAINTS_WITH_RELATIONAL);
			if (m1 != null) {
				m1.setName(Track.NUMC + "_" + i);
				LOGGER.debug("Added a new model: " + m1.getName());

				modelsList.add(m1);

				// Export the model
				exportModel(m1);
			}
		}
	}

	/**
	 * Generates UNIFORM_BOOLEAN instances
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 */
	public void generateUNIFORM_BOOLEAN()
			throws IOException, InvalidConfigurationException, SolverException, InterruptedException {
		// The generator without constraints
		Generator g = new WithoutConstraintGenerator();

		for (int i = 0; i < GeneratorConfiguration.N_BENCHMARKS; i++) {
			Model m1;
			m1 = generateWithGenerator(g, Category.ONLY_BOOLEAN);
			m1.setName(Track.UNIFORM_BOOLEAN + "_" + i);
			LOGGER.debug("Added a new model: " + m1.getName());

			modelsList.add(m1);

			// Export the model
			exportModel(m1);
		}
	}

	/**
	 * Generates UNIFORM_ALL instances
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 */
	public void generateUNIFORM_ALL()
			throws IOException, InvalidConfigurationException, SolverException, InterruptedException {
		// The generator without constraints
		Generator g = new WithoutConstraintGeneratorSameCardinality();

		for (int i = 0; i < GeneratorConfiguration.N_BENCHMARKS; i++) {
			Model m1;
			m1 = generateWithGenerator(g, Category.ALSO_ENUMS);
			m1.setName(Track.UNIFORM_ALL + "_" + i);
			LOGGER.debug("Added a new model: " + m1.getName());

			modelsList.add(m1);

			// Export the model
			exportModel(m1);
		}
	}

	/**
	 * Generates MCA instances
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 */
	public void generateMCA() throws IOException, InvalidConfigurationException, SolverException, InterruptedException {
		// The generator without constraints
		Generator g = new WithoutConstraintGenerator();

		for (int i = 0; i < GeneratorConfiguration.N_BENCHMARKS; i++) {
			Model m1;
			m1 = generateWithGenerator(g, Category.ALSO_ENUMS);
			m1.setName(Track.MCA + "_" + i);
			LOGGER.debug("Added a new model: " + m1.getName());

			modelsList.add(m1);

			// Export the model
			exportModel(m1);
		}
	}

	/**
	 * Export a model given as parameter
	 * 
	 * @param m1 the model to be exported
	 * @throws IOException
	 */
	public void exportModel(Model m1) throws IOException {
		if (GeneratorConfiguration.ALWAYS_EXPORT) {
			if (GeneratorConfiguration.ACTS)
				m1.exportACTS(destinationFolder);
			if (GeneratorConfiguration.CTWEDGE)
				m1.exportCTWedge(destinationFolder);
			if (GeneratorConfiguration.PICT)
				m1.exportPICT(destinationFolder);
		}
	}

	/**
	 * Access the list of models
	 * 
	 * @return the list of modelsthe list of models
	 */
	public ArrayList<Model> getModelsList() {
		return modelsList;
	}
}
