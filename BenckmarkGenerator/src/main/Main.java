package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ctwedge.ctWedge.CitModel;
import ctwedge.generator.acts.ACTSTranslator;
import ctwedge.generator.util.Utility;
import generators.Category;
import generators.Generator;
import generators.GeneratorConfiguration;
import generators.WithConstraintGenerator;
import generators.WithoutConstraintGenerator;
import generators.WithoutConstraintGeneratorSameCardinality;
import models.Model;

public class Main {
	
	static int N_MODELS = 5;
	static String PREFIX = "UNIFORM_BOOLEAN_";
	
	public static void generateUniformBooleanNoConstraints() throws IOException {
		PREFIX = "UNIFORM_BOOLEAN_";
		
		// Define a new generator without considering the constraints
		Generator g = new WithoutConstraintGenerator();
		
		// Using k in the range [2, 20]
		GeneratorConfiguration.N_PARAMS_MAX = 20;
		GeneratorConfiguration.N_PARAMS_MIN = 2;
		
		// Generate models with only boolean params
		for (int i = 0; i<N_MODELS; i++) {
			Model m1 = g.generate(Category.ONLY_BOOLEAN);
			m1.setName(PREFIX + i);
			// Store the results in a new CTW file
			FileWriter fo = new FileWriter(new File("./examples/" + PREFIX + i + ".ctw"));
			fo.write(m1.toString());
			fo.close();
			// Convert the file in ACTS
			CitModel ctwedgeModel = Utility.loadModel(m1.toString());
			ACTSTranslator translator = new ACTSTranslator();
			translator.convertModel(ctwedgeModel, true, 2, "./examples/");
		}		
	}
	
	public static void generateUniformNoConstraints() throws IOException {
		PREFIX = "UNIFORM_ALL_";
		
		// Define a new generator without considering the constraints
		Generator g = new WithoutConstraintGeneratorSameCardinality();
		
		// Using k in the range [2, 20]
		GeneratorConfiguration.N_PARAMS_MAX = 20;
		GeneratorConfiguration.N_PARAMS_MIN = 2;
		
		// Using v in the range [2, 20]
		GeneratorConfiguration.MIN_CARDINALITY=2;
		GeneratorConfiguration.MAX_CARDINALITY=20;
		
		// Generate models with also enum parameters
		for (int i = 0; i<N_MODELS; i++) {
			Model m1 = g.generate(Category.ALSO_ENUMS);
			m1.setName(PREFIX + i);
			// Store the results in a new CTW file
			FileWriter fo = new FileWriter(new File("./examples/" + PREFIX + i + ".ctw"));
			fo.write(m1.toString());
			fo.close();
			// Convert the file in ACTS
			CitModel ctwedgeModel = Utility.loadModel(m1.toString());
			ACTSTranslator translator = new ACTSTranslator();
			translator.convertModel(ctwedgeModel, true, 2, "./examples/");
		}		
	}
	
	public static void generateMCANoConstraints() throws IOException {
		PREFIX = "MCA_";
		
		// Define a new generator without considering the constraints
		Generator g = new WithoutConstraintGenerator();
		
		// Using k in the range [2, 20]
		GeneratorConfiguration.N_PARAMS_MAX = 20;
		GeneratorConfiguration.N_PARAMS_MIN = 2;
		
		// Using v in the range [2, 20]
		GeneratorConfiguration.MIN_CARDINALITY=2;
		GeneratorConfiguration.MAX_CARDINALITY=20;
		
		// Generate models with also enum parameters
		for (int i = 0; i<N_MODELS; i++) {
			Model m1 = g.generate(Category.ALSO_ENUMS);
			m1.setName(PREFIX + i);
			// Store the results in a new CTW file
			FileWriter fo = new FileWriter(new File("./examples/" + PREFIX + i + ".ctw"));
			fo.write(m1.toString());
			fo.close();
			// Convert the file in ACTS
			CitModel ctwedgeModel = Utility.loadModel(m1.toString());
			ACTSTranslator translator = new ACTSTranslator();
			translator.convertModel(ctwedgeModel, true, 2, "./examples/");
		}		
	}
	
	public static void generateBoolConstraints() throws IOException {
		PREFIX = "BOOLC_";
		
		// Define a new generator without considering the constraints
		Generator g = new WithConstraintGenerator();
		
		// Using k in the range [2, 20]
		GeneratorConfiguration.N_PARAMS_MAX = 20;
		GeneratorConfiguration.N_PARAMS_MIN = 2;
		
		// Using c in the range [1, 100]
		GeneratorConfiguration.N_CONSTRAINTS_MIN = 1;
		GeneratorConfiguration.N_CONSTRAINTS_MAX = 100;
		
		// Using d in the range [1, 20]
		GeneratorConfiguration.MIN_CONSTRAINTS_COMPLEXITY = 1;
		GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY = 20;
		
		// Generate models with only boolean parameters
		for (int i = 0; i<N_MODELS; i++) {
			Model m1 = g.generate(Category.ONLY_BOOLEAN);
			m1.setName(PREFIX + i);
			// Store the results in a new CTW file
			FileWriter fo = new FileWriter(new File("./examples/" + PREFIX + i + ".ctw"));
			fo.write(m1.toString());
			fo.close();
			// Convert the file in ACTS
			CitModel ctwedgeModel = Utility.loadModel(m1.toString());
			ACTSTranslator translator = new ACTSTranslator();
			translator.convertModel(ctwedgeModel, true, 2, "./examples/");
		}		
	}
	 
	public static void main(String[] args) throws IOException {
//		generateUniformBooleanNoConstraints();
//		generateUniformNoConstraints();
		generateMCANoConstraints();
	}
}
