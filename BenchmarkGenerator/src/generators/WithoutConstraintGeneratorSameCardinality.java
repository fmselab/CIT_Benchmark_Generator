package generators;

import java.sql.Timestamp;
import java.util.ArrayList;

import models.Model;
import util.ParameterToModelAdder;

/**
 * Generates a new model without constraints, and with parameters of the
 * category given as parameter but with the same cardinality
 * 
 * 
 * @author andrea
 *
 */
public class WithoutConstraintGeneratorSameCardinality implements Generator {

	/**
	 * Generate an IPM and force all parameters to be of the same cardinality
	 * 
	 * @param type   the type of models to be generated (with booleans, with
	 *               enumeratives, with integers, ...)
	 * @param config the generator configuration
	 * @return the generated IPM
	 */
	@Override
	public Model generate(Category type, GeneratorConfiguration config) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		// The model with a unique name
		Model m = new Model(type);
		m.setName("model" + timestamp.getTime());

		// The list of already used names
		ArrayList<String> names = new ArrayList<>();

		// Number of parameters
		int n = Randomizer.generate(m.getGeneratorConfiguration().N_PARAMS_MIN,
				m.getGeneratorConfiguration().N_PARAMS_MAX);
		int from = 0;

		// Cardinality
		int cardinality = Randomizer.generate(m.getGeneratorConfiguration().MIN_CARDINALITY,
				m.getGeneratorConfiguration().MAX_CARDINALITY);

		// Generate the benchmark
		switch (type) {
		case ONLY_BOOLEAN:
			// With only booleans
			for (int i = 0; i < n; i++)
				ParameterToModelAdder.addBooleanParameter(m, names, i);

			break;

		case ALSO_ENUMS:
			// With also enumerative, but all with the same number of values
			for (int i = 0; i < n; i++) {
				// If the cardinality is 2, we can use also booleans
				if (cardinality == 2 && Randomizer.generate(0, 1) == 0) {
					ParameterToModelAdder.addBooleanParameter(m, names, i);
				} else {
					// Define a new enumerative parameter
					ParameterToModelAdder.addEnumerativeParameter(m, cardinality, names, i);
				}
			}
			break;

		case CONSTRAINTS_WITH_RELATIONAL:
			// With also enumerative and integers
			for (int i = 0; i < n; i++) {
				// Probability 33% of extracting a boolean and 33% for enumerative and 33% for
				// integers
				int parType = Randomizer.generate(0, 2);

				if (cardinality == 2 && Randomizer.generate(0, 2) == 0) {
					ParameterToModelAdder.addBooleanParameter(m, names, i);
				} else {
					parType = Randomizer.generate(0, 1);

					switch (parType) {
					case 0:
						// Define a new enumerative parameter
						ParameterToModelAdder.addEnumerativeParameter(m, cardinality, names, i);
						break;
					case 1:
						// Define a new integer parameter
						from = Randomizer.generate(m.getGeneratorConfiguration().LOWER_BOUND_INT,
								m.getGeneratorConfiguration().UPPER_BOUND_INT);

						ParameterToModelAdder.addIntegerParameter(m, cardinality, from, names, i);
						break;
					}
				}
			}
			break;
		}

		return m;
	}

}
