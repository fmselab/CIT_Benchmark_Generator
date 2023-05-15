package main;

import java.io.IOException;
import java.util.concurrent.Callable;

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

	/**
	 * 
	 * 
	 * private JTextField txtMaxConstraintsComplexity; private JTextField
	 * txtMinConstraintComplexity; private JTextField txtRatio;
	 */
	@Parameters(index = "0", description = "The category for the benchmark to be generated (UNIFORM_BOOLEAN, UNIFORM_ALL, MCA, BOOLC, MCAC, NUMC, HIGHLY_CONSTRAINED, CNF).")
	String trackStr;
	Track track;

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

	@Option(names = "-r", description = "Maximum accepted tuple validity ratio. By default it is 0.01.")
	private double r = 0.01;

	@Option(names = "-similar", description = "Given a CTWedge model, it generates a model similar to that.")
	private String similarModel = null;

	public static void main(String[] args) throws IOException {
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
	 */
	private void generate() throws IOException, InterruptedException {
		// First set the configurations
		setConfigurations();

		// Then, based on the chosen track, generate benchmarks
		track = Track.valueOf(trackStr);

		switch (track) {
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
	 * @throws InterruptedException 
	 */
	public void setConfigurations() throws InterruptedException {
		GeneratorConfiguration.N_BENCHMARKS = nTests;
		if (similarModel == null) {
			GeneratorConfiguration.N_PARAMS_MAX = kmax;
			GeneratorConfiguration.N_PARAMS_MIN = kmin;
			GeneratorConfiguration.MAX_CARDINALITY = vmax;
			GeneratorConfiguration.MIN_CARDINALITY = vmin;
			GeneratorConfiguration.LOWER_BOUND_INT = vIntLow;
			GeneratorConfiguration.UPPER_BOUND_INT = vIntUp;
			GeneratorConfiguration.RATIO = r;
			GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY = dmax;
			GeneratorConfiguration.MIN_CONSTRAINTS_COMPLEXITY = dmin;
			GeneratorConfiguration.N_CONSTRAINTS_MAX = cmax;
			GeneratorConfiguration.N_CONSTRAINTS_MIN = cmin;
		} else {
			// Extract the configuration from that of model given by the user
			ModelConfigurationExtractor extractor = new ModelConfigurationExtractor(similarModel);
			GeneratorConfiguration.N_PARAMS_MAX = extractor.getNumParams();
			GeneratorConfiguration.N_PARAMS_MIN = extractor.getNumParams();
			GeneratorConfiguration.MAX_CARDINALITY = extractor.getMaximumCardinality();
			GeneratorConfiguration.MIN_CARDINALITY = extractor.getMinimumCardinality();
			GeneratorConfiguration.LOWER_BOUND_INT = extractor.getLowerBoundForInts();
			GeneratorConfiguration.UPPER_BOUND_INT = extractor.getUpperBoundForInts();
			GeneratorConfiguration.RATIO = extractor.getTupleValidityRatio();
			GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY = extractor.getMaxConstraintComplexity();
			GeneratorConfiguration.MIN_CONSTRAINTS_COMPLEXITY = extractor.getMinConstraintComplexity();
			GeneratorConfiguration.N_CONSTRAINTS_MAX = extractor.getNumConstraints();
			GeneratorConfiguration.N_CONSTRAINTS_MIN = extractor.getNumConstraints();
		}
	}

	/**
	 * Generates HIGHLY_CONSTRAINED instances
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void generateHIGHLY_CONSTRAINED() throws IOException, InterruptedException {
		// The generator that considers constraints
		Generator g = new WithConstraintGenerator();

		for (int i = 0; i < GeneratorConfiguration.N_BENCHMARKS; i++) {
			Model m1;
			// Keep generating the same model until a solvable one is found
			do {
				m1 = g.generate(Category.ALSO_ENUMS);
				m1.setName(Track.HIGHLY_CONSTRAINED + "_" + i);
			} while (!m1.isSolvable() || m1.getTupleValidityRatio() > GeneratorConfiguration.RATIO);

			// Export the model
			exportModel(m1);
		}
	}

	/**
	 * Generates BOOLC instances
	 * 
	 * @throws IOException
	 */
	public void generateBOOLC() throws IOException {
		// The generator that considers constraints
		Generator g = new WithConstraintGenerator();

		for (int i = 0; i < GeneratorConfiguration.N_BENCHMARKS; i++) {
			Model m1;
			// Keep generating the same model until a solvable one is found
			do {
				m1 = g.generate(Category.ONLY_BOOLEAN);
				m1.setName(Track.BOOLC + "_" + i);
			} while (!m1.isSolvable());

			// Export the model
			exportModel(m1);
		}
	}

	/**
	 * Generates MCAC instances
	 * 
	 * @throws IOException
	 */
	public void generateMCAC() throws IOException {
		// The generator that considers constraints
		Generator g = new WithConstraintGenerator();

		for (int i = 0; i < GeneratorConfiguration.N_BENCHMARKS; i++) {
			Model m1;
			// Keep generating the same model until a solvable one is found
			do {
				m1 = g.generate(Category.ALSO_ENUMS);
				m1.setName(Track.MCAC + "_" + i);
			} while (!m1.isSolvable());

			// Export the model
			exportModel(m1);
		}
	}

	/**
	 * Generates CNF instances
	 * 
	 * @throws IOException
	 */
	public void generateCNF() throws IOException {
		// The generator that considers constraints
		Generator g = new WithConstraintGeneratorCNF();

		for (int i = 0; i < GeneratorConfiguration.N_BENCHMARKS; i++) {
			Model m1;
			// Keep generating the same model until a solvable one is found
			do {
				m1 = g.generate(Category.ALSO_ENUMS);
				m1.setName(Track.CNF + "_" + i);
			} while (!m1.isSolvable());

			// Export the model
			exportModel(m1);
		}
	}

	/**
	 * Generates NUMC instances
	 * 
	 * @throws IOException
	 */
	public void generateNUMC() throws IOException {
		// The generator that considers constraints
		Generator g = new WithConstraintGenerator();

		for (int i = 0; i < GeneratorConfiguration.N_BENCHMARKS; i++) {
			Model m1;
			// Keep generating the same model until a solvable one is found
			do {
				m1 = g.generate(Category.CONSTRAINTS_WITH_RELATIONAL);
				m1.setName(Track.NUMC + "_" + i);
			} while (!m1.isSolvable());

			// Export the model
			exportModel(m1);
		}
	}

	/**
	 * Generates UNIFORM_BOOLEAN instances
	 * 
	 * @throws IOException
	 */
	public void generateUNIFORM_BOOLEAN() throws IOException {
		// The generator without constraints
		Generator g = new WithoutConstraintGenerator();

		for (int i = 0; i < GeneratorConfiguration.N_BENCHMARKS; i++) {
			Model m1;
			m1 = g.generate(Category.ONLY_BOOLEAN);
			m1.setName(Track.UNIFORM_BOOLEAN + "_" + i);

			// Export the model
			exportModel(m1);
		}
	}

	/**
	 * Generates UNIFORM_ALL instances
	 * 
	 * @throws IOException
	 */
	public void generateUNIFORM_ALL() throws IOException {
		// The generator without constraints
		Generator g = new WithoutConstraintGeneratorSameCardinality();

		for (int i = 0; i < GeneratorConfiguration.N_BENCHMARKS; i++) {
			Model m1;
			m1 = g.generate(Category.ALSO_ENUMS);
			m1.setName(Track.UNIFORM_ALL + "_" + i);

			// Export the model
			exportModel(m1);
		}
	}

	/**
	 * Generates MCA instances
	 * 
	 * @throws IOException
	 */
	public void generateMCA() throws IOException {
		// The generator without constraints
		Generator g = new WithoutConstraintGenerator();

		for (int i = 0; i < GeneratorConfiguration.N_BENCHMARKS; i++) {
			Model m1;
			m1 = g.generate(Category.ALSO_ENUMS);
			m1.setName(Track.MCA + "_" + i);

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
		if (acts)
			m1.exportACTS(destinationFolder);
		if (ctwedge)
			m1.exportCTWedge(destinationFolder);
		if (pict)
			m1.exportPICT(destinationFolder);
	}
}
