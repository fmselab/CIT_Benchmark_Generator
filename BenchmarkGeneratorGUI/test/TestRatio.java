import org.junit.Test;

import benchmark.generator.gui.BenchmarkGenerator;
import generators.Category;
import generators.Generator;
import generators.GeneratorConfiguration;
import generators.WithConstraintGenerator;
import models.Model;

public class TestRatio {

	/**
	 * Test checking the RATIO computation for HIGHLY CONSTRAINED models
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void test() throws InterruptedException {
		GeneratorConfiguration.MAX_CARDINALITY = 300;
		Generator gWC = new WithConstraintGenerator();
		Model m1 = gWC.generate(Category.ALSO_ENUMS);
		m1.setName(BenchmarkGenerator.HIGHLY_CONSTRAINED + "_" + 0);
		if (m1.getHighestCardinality() < 127) {
			System.out.println(m1.toString());
			System.out.println(m1.getTestValidityRatio());
			assert (m1.getTestValidityRatio() >= 0);
		}
	}

}
