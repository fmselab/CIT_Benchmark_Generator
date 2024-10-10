package generators;

import java.sql.Timestamp;
import java.util.ArrayList;

import models.Model;

/**
 * Generates a new model without constraints, and with parameters of the
 * category given as parameter
 * 
 * 
 * @author andrea
 *
 */
public class WithoutConstraintGenerator implements Generator {

	/**
	 * Generate an IPM
	 * 
	 * @param type the type of models to be generated (with booleans, with
	 *             enumeratives, with integers, ...)
	 * @param config the generator configuration used for generating the model
	 * @return the generated IPM
	 */
	@Override
	public Model generate(Category type, GeneratorConfiguration config) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		// The model with a unique name
		Model m = new Model(type);
		m.setGeneratorConfiguration(config);
		m.setName("model" + timestamp.getTime());

		// The names of the used parameters
		ArrayList<String> names = new ArrayList<>();

		// Number of parameters
		int n = Randomizer.generate(m.getGeneratorConfiguration().N_PARAMS_MIN, m.getGeneratorConfiguration().N_PARAMS_MAX);
		for (int i = 0;i<n; i++) {
			m.addNewRandomParameter(names);
		}

		return m;
	}

}
