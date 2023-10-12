package generators;

import java.sql.Timestamp;
import java.util.ArrayList;

import models.BooleanParameter;
import models.EnumerativeParameter;
import models.IntegerParameter;
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
	 * @param type the type of models to be generated (with booleans, with
	 *             enumeratives, with integers, ...)
	 * @return the generated IPM
	 */
	@Override
	public Model generate(Category type) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		// The model with a unique name
		Model m = new Model();
		m.setName("model" + timestamp.getTime());
		
		// The list of already used names
		ArrayList<String> names = new ArrayList<>();

		// Number of parameters
		int n = Randomizer.generate(GeneratorConfiguration.N_PARAMS_MIN, GeneratorConfiguration.N_PARAMS_MAX);
		int from = 0;

		// Cardinality
		int cardinality = Randomizer.generate(GeneratorConfiguration.MIN_CARDINALITY,
				GeneratorConfiguration.MAX_CARDINALITY);

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
					EnumerativeParameter p = new EnumerativeParameter("Par" + i);

					for (int j = 0; j < cardinality; j++)
						p.addValue("PAR" + i + "_" + j);

					m.addParameter(p);
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
						EnumerativeParameter p = new EnumerativeParameter("Par" + i);

						for (int j = 0; j < cardinality; j++)
							p.addValue("PAR" + i + "_" + j);

						m.addParameter(p);
						break;
					case 1:
						// Define a new integer parameter
						from = Randomizer.generate(GeneratorConfiguration.LOWER_BOUND_INT,
								GeneratorConfiguration.UPPER_BOUND_INT);

						// Define a new integer parameter
						m.addParameter(new IntegerParameter("Par" + i, from, from + cardinality - 1));
						break;
					}
				}
			}
			break;
		}

		return m;
	}

}
