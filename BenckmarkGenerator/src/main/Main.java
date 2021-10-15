package main;

import ctwedge.ctWedge.CitModel;
import ctwedge.generator.acts.ACTSTranslator;
import ctwedge.generator.util.Utility;
import generators.Category;
import generators.Generator;
import generators.GeneratorConfiguration;
import generators.WithConstraintGenerator;
import models.Model;

public class Main {
	
	static int N_MODELS = 50;
	 
	public static void main(String[] args) {
		
		Generator g = new WithConstraintGenerator();
		
		GeneratorConfiguration.DIM_ENUMS_MAX = 5;
		GeneratorConfiguration.LOWER_BOUND_INT = 2;
		GeneratorConfiguration.UPPER_BOUND_INT = 5;
		GeneratorConfiguration.MAX_CARDINALITY = 5;
		GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY = 3;
		GeneratorConfiguration.N_PARAMS_MAX = 5;
		GeneratorConfiguration.N_PARAMS_MIN = 1;
		GeneratorConfiguration.N_CONSTRAINTS_MAX = 5;
		GeneratorConfiguration.N_CONSTRAINTS_MIN = 1;
		GeneratorConfiguration.MIN_SIZE = 0;
		GeneratorConfiguration.MAX_SIZE = -1;
		
		
		
		Model m1 = g.generate(Category.ONLY_BOOLEAN);
		System.out.println(m1.toString());
		System.out.println("Size: " + m1.getModelSize());
		System.out.println("Size: " + m1.getTupleValidityRatio());
		System.out.println("Size: " + m1.getTestValidityRatio());
		
		// Convert the model as ACTS
		CitModel ctwedgeModel = Utility.loadModel(m1.toString());
		ACTSTranslator translator = new ACTSTranslator();
		translator.convertModel(ctwedgeModel, true, 2, "");
	}
}
