package generators;

import java.sql.Timestamp;

import models.BooleanParameter;
import models.EnumerativeParameter;
import models.IntegerParameter;
import models.Model;

public class WithoutConstraintGeneratorSameCardinality implements Generator {

	public static int N_PARAMS_MAX = 500;
	public static int N_PARAMS_MIN = 1;
	public static int DIM_ENUMS_MAX = 50;
	public static int LOWER_BOUND_INT = -100;
	public static int UPPER_BOUND_INT = 100;
	public static int MAX_CARDINALITY = 500;
	
	@Override
	public Model generate(Category type) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		// The model with a unique name
		Model m = new Model();
		m.setName("model" + timestamp.getTime());
		
		// Number of parameters
		int n = Randomizer.generate(N_PARAMS_MIN, N_PARAMS_MAX);
		int from = 0; 
		
		// Cardinality
		int cardinality = Randomizer.generate(1, MAX_CARDINALITY); 
				
		// Generate the benchmark
		switch (type) {
			case ONLY_BOOLEAN:
				// With only booleans
				for (int i=0; i<n; i++) 
					m.addParameter(new BooleanParameter("Par" + i));
				
				break;
				
			case ALSO_ENUMS:
				// With also enumerative, but all with the same number of values
				for (int i=0; i<n; i++) {
					// If the cardinality is 2, we can use also booleans
					if (cardinality == 2 && Randomizer.generate(0, 1) == 0) {
							m.addParameter(new BooleanParameter("Par" + i));
					} else {
						// Define a new enumerative parameter
						EnumerativeParameter p = new EnumerativeParameter("Par" + i);
						
						for(int j=0; j<cardinality; j++)
							p.addValue("PAR" + i + "_" + j);
						
						m.addParameter(p);
					}
				}
				break;
	
			case ALSO_INTEGERS:
				// With also integer ranges
				boolean computeParams = true;
				for (int i=0; i<n; i++) {
					// If the cardinality is 2, we can use also booleans
					if (cardinality == 2 && Randomizer.generate(0, 1) == 0) {
							m.addParameter(new BooleanParameter("Par" + i));
					} else {
						// Define a new integer parameter
						from = Randomizer.generate(LOWER_BOUND_INT, UPPER_BOUND_INT);
						
						// Define a new integer parameter
						m.addParameter(new IntegerParameter("Par" + i, from, from + cardinality - 1));
						computeParams = true;
					}
				}			
				
				break;
				
			case CONSTRAINTS_WITH_RELATIONAL:
				// With also enumerative and integers
				for (int i=0; i<n; i++) {
					// Probability 33% of extracting a boolean and 33% for enumerative and 33% for integers
					int parType = Randomizer.generate(0, 2);
					
					if (cardinality == 2 && Randomizer.generate(0, 2) == 0) {
						m.addParameter(new BooleanParameter("Par" + i));
					} else {
						parType = Randomizer.generate(0, 1);
						
						switch(parType) {
							case 0:
								// Define a new enumerative parameter
								EnumerativeParameter p = new EnumerativeParameter("Par" + i);
								
								for(int j=0; j<cardinality; j++)
									p.addValue("PAR" + i + "_" + j);
								
								m.addParameter(p);
								break;
							case 1:
								// Define a new integer parameter
								from = Randomizer.generate(LOWER_BOUND_INT, UPPER_BOUND_INT);
								
								// Define a new integer parameter
								m.addParameter(new IntegerParameter("Par" + i, from, from + cardinality - 1));
								computeParams = true;
								break;
						}						
					}					
				}
				break;
		}
		
		return m;
	}

}
