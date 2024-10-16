import java.io.IOException;

import org.junit.Test;
import org.sosy_lab.common.configuration.InvalidConfigurationException;

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
	 * @throws InvalidConfigurationException 
	 * @throws IOException 
	 */
	@Test
	public void test() throws InterruptedException, InvalidConfigurationException, IOException {
		GeneratorConfiguration config = new GeneratorConfiguration();
		config.MAX_CARDINALITY = 300;
		Generator gWC = new WithConstraintGenerator();
		Model m1 = gWC.generate(Category.ALSO_ENUMS, config);
		m1.setName("HIGHLY_CONSTRAINED_" + 0);
		if (m1.getHighestCardinality() < 127) {
			System.out.println(m1.toString());
			System.out.println(m1.getTestValidityRatio());
			assert (m1.getTestValidityRatio() >= 0);
		}
	}

}
