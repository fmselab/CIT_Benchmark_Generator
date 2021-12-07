import static org.junit.Assert.*;

import org.junit.Test;

import ctwedge.ctWedge.CitModel;
import ctwedge.generator.util.Utility;
import generators.Category;
import generators.Generator;
import generators.GeneratorConfiguration;
import generators.WithConstraintGenerator;
import models.Model;

public class TestGenModel {
	
	@Test
	public void testName() throws Exception {
		Generator g = new WithConstraintGenerator();
		
		// Using k in the range [2, 20]
		GeneratorConfiguration.N_PARAMS_MAX = 3;
		GeneratorConfiguration.N_PARAMS_MIN = 2;
				
		
		Model model = g.generate(Category.ONLY_BOOLEAN);
		model.setName("temp");
		// parso con ctwedge
		CitModel citmodel = Utility.loadModel(model.toString());
		//
		int size = citmodel.getParameters().size();
		assertTrue(2 <= size && size <=3);
		// constraint

	}

}
