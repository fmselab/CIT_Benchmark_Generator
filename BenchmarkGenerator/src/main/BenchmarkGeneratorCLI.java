package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.api.SolverException;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.AbstractEvolutionEngine;
import org.uncommons.watchmaker.framework.CachingFitnessEvaluator;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.ElapsedTime;
import org.uncommons.watchmaker.framework.termination.Stagnation;
import org.uncommons.watchmaker.framework.termination.TargetFitness;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import ctwedge.util.NotConvertableModel;
import generators.Category;
import generators.Generator;
import generators.GeneratorConfiguration;
import generators.Track;
import generators.WithConstraintGenerator;
import generators.WithConstraintGeneratorCNF;
import generators.WithConstraintGeneratorFT;
import generators.WithoutConstraintGenerator;
import generators.WithoutConstraintGeneratorSameCardinality;
import models.Model;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import util.Dictionary;
import util.ModelConfigurationExtractor;
import util.genetics.ModelFactory;
import util.genetics.SBModelRatioGenerator;
import util.genetics.mutations.ConstraintAdderMutation;
import util.genetics.mutations.ConstraintAndToOrMutation;
import util.genetics.mutations.ConstraintDblImpliesToImpliesMutation;
import util.genetics.mutations.ConstraintImpliesToDblImpliesMutation;
import util.genetics.mutations.ConstraintNotRemoverMutation;
import util.genetics.mutations.ConstraintOrToAndMutation;
import util.genetics.mutations.ConstraintRemoverMutation;
import util.genetics.mutations.ConstraintSubstitutionMutation;
import util.genetics.mutations.ConstraintToNotMutation;
import util.genetics.mutations.ParameterAdderMutation;
import util.genetics.mutations.ParameterExtenderMutation;
import util.genetics.problems.ModelProblem;
import util.genetics.problems.ModelRatioProblem;
import util.genetics.problems.ModelSolvabilityProblem;
import util.genetics.problems.ModelTestRatioProblem;
import util.genetics.problems.ModelTupleRatioProblem;

public class BenchmarkGeneratorCLI implements Callable<Integer> {

	private static final Logger LOGGER = LogManager.getRootLogger();

	@Parameters(index = "0", description = "The category for the benchmark to be generated (UNIFORM_BOOLEAN, UNIFORM_ALL, MCA, BOOLC, MCAC, NUMC).")
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

	@Option(names = "-p", description = "Probability for ratio computation. By default it is 0.75")
	private double P = 0.75;

	@Option(names = "-epsilon", description = "The accepted error when computing the test validity ratio, if MDDs cannot be used. By default it is 0.1")
	private double epsilon = 0.1;

	@Option(names = "-ft", description = "Only constraints expressed as forbidden tuples? By default it is disabled")
	private boolean ft = false;

	@Option(names = "-cnf", description = "Only constraints expressed as CNF? By default it is disabled")
	private boolean cnf = false;

	@Option(names = "-dict", description = "The JSON file containing the dictionary")
	private String dict = null;

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
		GeneratorConfiguration config = setConfigurations();

		// Then, based on the chosen track, generate benchmarks
		generateIPMs(config);
	}

	/**
	 * Based on the chosen track, the benchmarks are generated
	 * 
	 * @param config the generator configuration
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws InvalidConfigurationException
	 * @throws SolverException
	 */
	public void generateIPMs(GeneratorConfiguration config)
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		// Clear the previously generated models
		modelsList.clear();

		switch (config.TRACK) {
		case BOOLC:
			generateBOOLC(config);
			break;
		case MCA:
			generateMCA(config);
			break;
		case MCAC:
			generateMCAC(config);
			break;
		case NUMC:
			generateNUMC(config);
			break;
		case UNIFORM_ALL:
			generateUNIFORM_ALL(config);
			break;
		case UNIFORM_BOOLEAN:
			generateUNIFORM_BOOLEAN(config);
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
	 * @throws IOException
	 * 
	 * @return the generator configuration
	 */
	public GeneratorConfiguration setConfigurations()
			throws InterruptedException, InvalidConfigurationException, SolverException, IOException {
		GeneratorConfiguration config = new GeneratorConfiguration();
		config.N_BENCHMARKS = nTests;
		if (similarModel == null) {
			LOGGER.debug("Configurations set by the user");
			config.N_PARAMS_MAX = kmax;
			config.N_PARAMS_MIN = kmin;
			config.MAX_CARDINALITY = vmax;
			config.MIN_CARDINALITY = vmin;
			config.LOWER_BOUND_INT = vIntLow;
			config.UPPER_BOUND_INT = vIntUp;
			config.RATIO = r;
			config.RATIO_TEST = rTest;
			config.MAX_CONSTRAINTS_COMPLEXITY = dmax;
			config.MIN_CONSTRAINTS_COMPLEXITY = dmin;
			config.N_CONSTRAINTS_MAX = cmax;
			config.N_CONSTRAINTS_MIN = cmin;
			config.P = P;
			config.EPSILON = epsilon;
			config.TRACK = Track.valueOf(trackStr);
			config.CHECK_TEST_RATIO = chkTestRatio;
			config.CHECK_TUPLE_RATIO = chkTupleRatio;
			config.FORBIDDEN_TUPLES = ft;
			config.CNF = cnf;
		} else {
			// Extract the configuration from that of model given by the user
			LOGGER.debug("Configurations read from the baseline model");
			config = setConfigurationsFromFile(similarModel);
		}

		// Then, set the export format
		config.ACTS = acts;
		config.PICT = pict;
		config.CTWEDGE = ctwedge;
		// Now set the dictionary, if needed
		if (dict != null) {
			config = setDictionary(dict, config);
		}

		return config;
	}

	/**
	 * Sets the configuration based on the model received as input
	 * 
	 * @param modelPath the model path
	 * @throws InterruptedException
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 * @throws IOException
	 * 
	 * @return the generator configuration
	 */
	public static GeneratorConfiguration setConfigurationsFromFile(String modelPath)
			throws InterruptedException, InvalidConfigurationException, SolverException, IOException {
		ModelConfigurationExtractor extractor = new ModelConfigurationExtractor(modelPath);
		GeneratorConfiguration config = new GeneratorConfiguration();

		config.N_PARAMS_MAX = extractor.getNumParams();
		config.N_PARAMS_MIN = extractor.getNumParams();
		config.MAX_CARDINALITY = extractor.getMaximumCardinality();
		config.MIN_CARDINALITY = extractor.getMinimumCardinality();
		config.LOWER_BOUND_INT = extractor.getLowerBoundForInts();
		config.UPPER_BOUND_INT = extractor.getUpperBoundForInts();
		try {
			config.RATIO = extractor.getTupleValidityRatio();
		} catch (NotConvertableModel c) {
			config.RATIO = 1;
		}
		config.MAX_CONSTRAINTS_COMPLEXITY = extractor.getMaxConstraintComplexity();
		config.MIN_CONSTRAINTS_COMPLEXITY = extractor.getMinConstraintComplexity();
		config.N_CONSTRAINTS_MAX = extractor.getNumConstraints();
		config.N_CONSTRAINTS_MIN = extractor.getNumConstraints();
		config.TRACK = extractor.getModelType();
		config.FORBIDDEN_TUPLES = extractor.hasForbiddenTuples();
		config.CNF = extractor.isCNF();

		return config;
	}

	/**
	 * Generates BOOLC instances
	 * 
	 * @param config the generator configuration
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 */
	public void generateBOOLC(GeneratorConfiguration config)
			throws IOException, InvalidConfigurationException, SolverException, InterruptedException {
		// The generator that considers constraints
		Generator g;
		if (!config.FORBIDDEN_TUPLES && !config.CNF)
			g = new WithConstraintGenerator();
		else if (config.CNF)
			g = new WithConstraintGeneratorCNF();
		else
			g = new WithConstraintGeneratorFT();

		for (int i = 0; i < config.N_BENCHMARKS; i++) {
			Model m1;
			// Keep generating the same model until a solvable one is found
			if (config.USE_SEARCH)
				m1 = generateWithGeneratorAndSearch(g, Category.ONLY_BOOLEAN, config);
			else
				m1 = generateWithGenerator(g, Category.ONLY_BOOLEAN, config, config.CHECK_SOLVABLE);
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
	 * @param config          the generator configuration
	 * @return the model
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private Model generateWithGenerator(Generator generator, Category category, GeneratorConfiguration config)
			throws InvalidConfigurationException, SolverException, InterruptedException, IOException {
		return generateWithGenerator(generator, category, config, true);
	}

	/**
	 * Generates a model with a given generator and category, and verifies its
	 * solvability
	 * 
	 * @param generator       the generator
	 * @param category        the category
	 * @param checkTupleRatio check the tuple validity ratio?
	 * @param checkTestRatio  check the test validity ratio?
	 * @param config          the generator configuration
	 * @param checkSolvable   whether to chech or not the solvabillity
	 * @return the model
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private Model generateWithGenerator(Generator generator, Category category, GeneratorConfiguration config,
			Boolean checkSolvable)
			throws InvalidConfigurationException, SolverException, InterruptedException, IOException {
		boolean isSolvable = false;
		Model m = null;
		int i = 0;
		do {

			m = generator.generate(category, config);
			isSolvable = m.isSolvable();

			if (isSolvable) {
				LOGGER.debug("The generated model is solvable");
				// Check the tuple validity ratio
				if (config.CHECK_TUPLE_RATIO) {
					LOGGER.debug("Checking TUPLE VALIDITY RATIO");
					try {
						double ratio = m.getTupleValidityRatio();
						LOGGER.debug("TUPLE VALIDITY RATIO " + Double.toString(ratio));
						if (Math.abs(ratio - config.RATIO) > config.EPSILON)
							isSolvable = false;
					} catch (InterruptedException e) {
					}
				}

				// Check the test validity ratio
				if (config.CHECK_TEST_RATIO) {
					LOGGER.debug("Checking TEST VALIDITY RATIO");
					double ratio = m.getTestValidityRatio();
					LOGGER.debug("TEST VALIDITY RATIO " + Double.toString(ratio));
					// If the ratio is exact, it means that it has been computed with the MDD, thus
					// we can use it to decide whether the model is correct or not. Otherwise, we
					// can only work with probabilities
					if (m.isRatioExact() && Math.abs(ratio - config.RATIO_TEST) > config.EPSILON)
						isSolvable = false;
					else {
						// The ratio is not exact. But we can check the interval based on EPSILON
						if (!(ratio >= (1 - config.EPSILON) * config.RATIO_TEST
								&& ratio <= (1 + config.EPSILON) * config.RATIO_TEST))
							isSolvable = false;
					}
				}
			}
			i++;
			if (!isSolvable && checkSolvable) {
				LOGGER.debug("Generated a non solvable / compliant model. Trying another one");
				m = null;
			}
		} while ((checkSolvable && !isSolvable) && i < config.N_ATTEMPTS);
		return m;
	}

	/**
	 * Generates a model with a given generator and category, and verifies its
	 * solvability
	 * 
	 * @param generator       the generator
	 * @param category        the category
	 * @param checkTupleRatio check the tuple validity ratio?
	 * @param checkTestRatio  check the test validity ratio?
	 * @param config          the generator configuration
	 * @return the model
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private Model generateWithGeneratorAndSearch(Generator generator, Category category, GeneratorConfiguration config)
			throws InvalidConfigurationException, SolverException, InterruptedException, IOException {
		// Choose the right problem
		ModelProblem problem;
		if (config.CHECK_TEST_RATIO && !config.CHECK_TUPLE_RATIO)
            problem = new ModelTestRatioProblem(config.RATIO_TEST, config);
        else if (config.CHECK_TUPLE_RATIO && !config.CHECK_TEST_RATIO)
            problem = new ModelTupleRatioProblem(config.RATIO, config);
        else if (config.CHECK_TUPLE_RATIO && config.CHECK_TEST_RATIO)
        	problem = new ModelRatioProblem(config.RATIO, config.RATIO_TEST, config);
        else
            problem = new ModelSolvabilityProblem(config);
		
		// Evolve the model
		// TODO: To add a maximum time for the evolution (60 seconds for each model)
		Model m = SBModelRatioGenerator.evolveModel(config, problem);
		
		
		if (m == null || !m.isSolvable())
			return null;

		return m;
	}

	/**
	 * Generates MCAC instances
	 * 
	 * @param config the generator configuration
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 */
	public void generateMCAC(GeneratorConfiguration config)
			throws IOException, InvalidConfigurationException, SolverException, InterruptedException {
		// The generator that considers constraints
		Generator g;
		if (!config.FORBIDDEN_TUPLES && !config.CNF)
			g = new WithConstraintGenerator();
		else if (config.CNF)
			g = new WithConstraintGeneratorCNF();
		else
			g = new WithConstraintGeneratorFT();

		for (int i = 0; i < config.N_BENCHMARKS; i++) {
			Model m1;
			// Keep generating the same model until a solvable one is found
			if (config.USE_SEARCH)
				m1 = generateWithGeneratorAndSearch(g, Category.ALSO_ENUMS, config);
			else
				m1 = generateWithGenerator(g, Category.ALSO_ENUMS, config, config.CHECK_SOLVABLE);
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
	 * Generates NUMC instances
	 * 
	 * @param config the generator configuration
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 */
	public void generateNUMC(GeneratorConfiguration config)
			throws IOException, InvalidConfigurationException, SolverException, InterruptedException {
		// The generator that considers constraints
		Generator g;
		if (!config.FORBIDDEN_TUPLES && !config.CNF)
			g = new WithConstraintGenerator();
		else if (config.CNF)
			g = new WithConstraintGeneratorCNF();
		else
			g = new WithConstraintGeneratorFT();

		for (int i = 0; i < config.N_BENCHMARKS; i++) {
			Model m1;
			// Keep generating the same model until a solvable one is found
			if (config.USE_SEARCH)
				m1 = generateWithGeneratorAndSearch(g, Category.CONSTRAINTS_WITH_RELATIONAL, config);
			else
				m1 = generateWithGenerator(g, Category.CONSTRAINTS_WITH_RELATIONAL, config);
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
	 * @param config the generator configuration
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 */
	public void generateUNIFORM_BOOLEAN(GeneratorConfiguration config)
			throws IOException, InvalidConfigurationException, SolverException, InterruptedException {
		// The generator without constraints
		Generator g = new WithoutConstraintGenerator();

		for (int i = 0; i < config.N_BENCHMARKS; i++) {
			Model m1;
			m1 = generateWithGenerator(g, Category.ONLY_BOOLEAN, config);
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
	 * @param config the generator configuration
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 */
	public void generateUNIFORM_ALL(GeneratorConfiguration config)
			throws IOException, InvalidConfigurationException, SolverException, InterruptedException {
		// The generator without constraints
		Generator g = new WithoutConstraintGeneratorSameCardinality();

		for (int i = 0; i < config.N_BENCHMARKS; i++) {
			Model m1;
			m1 = generateWithGenerator(g, Category.ALSO_ENUMS, config);
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
	 * @param config the generator configuration
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 */
	public void generateMCA(GeneratorConfiguration config)
			throws IOException, InvalidConfigurationException, SolverException, InterruptedException {
		// The generator without constraints
		Generator g = new WithoutConstraintGenerator();

		for (int i = 0; i < config.N_BENCHMARKS; i++) {
			Model m1;
			m1 = generateWithGenerator(g, Category.ALSO_ENUMS, config);
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
		if (m1.getGeneratorConfiguration().ALWAYS_EXPORT) {
			if (m1.getGeneratorConfiguration().ACTS)
				m1.exportACTS(destinationFolder);
			if (m1.getGeneratorConfiguration().CTWEDGE)
				m1.exportCTWedge(destinationFolder);
			if (m1.getGeneratorConfiguration().PICT)
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

	/**
	 * This method sets the dictionary for test generation
	 * 
	 * @param selectedFile the file containing the dictionary
	 * @param config       the generator configuration
	 * @return the generator configuration
	 * @throws FileNotFoundException
	 */
	public static GeneratorConfiguration setDictionary(String selectedFile, GeneratorConfiguration config)
			throws FileNotFoundException {
		BufferedReader br = new BufferedReader(new FileReader(selectedFile));
		Type listOfMyClassObject = new TypeToken<ArrayList<Dictionary>>() {
		}.getType();
		ArrayList<Dictionary> dict = new Gson().fromJson(br, listOfMyClassObject);
		config.DICTIONARY = dict;
		return config;
	}

	/**
	 * Clears the model list
	 */
	public void clearModelsList() {
		this.modelsList.clear();
	}
}
