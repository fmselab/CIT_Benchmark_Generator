package generators;

import java.sql.Timestamp;

import models.BooleanParameter;
import models.EnumerativeParameter;
import models.IntegerParameter;
import models.Model;

public class WithoutConstraintGenerator implements Generator {


	@Override
	public Model generate(Category type) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		// The model with a unique name
		Model m = new Model();
		m.setName("model" + timestamp.getTime());
		
		// Number of parameters
		int n = Randomizer.generate(GeneratorConfiguration.N_PARAMS_MIN, GeneratorConfiguration.N_PARAMS_MAX);
		int nValues = 0;
		int from = 100; 
		int to = 0;
		boolean computeParams = true;
		
		// Generate the benchmark
		switch (type) {
			case ONLY_BOOLEAN:
				// With only booleans
				for (int i=0; i<n; i++) 
					m.addParameter(new BooleanParameter("Par" + i));
				
				break;
				
			case ALSO_ENUMS:
				// With also enumerative
				for (int i=0; i<n; i++) {
					// Probability 50% of extracting a boolean and 50% for enumerative
					if (Randomizer.generate(0, 1) == 0)
						m.addParameter(new BooleanParameter("Par" + i));
					else {
						// Define a new enumerative parameter
						nValues = Randomizer.generate(1, GeneratorConfiguration.DIM_ENUMS_MAX);
						EnumerativeParameter p = new EnumerativeParameter("Par" + i);
						
						for(int j=0; j<nValues; j++)
							p.addValue("PAR" + i + "_" + j);
						
						m.addParameter(p);
					}
				}
				break;
	
			case ALSO_INTEGERS:
				// With also integer ranges
				for (int i=0; i<n; i++) {
					// Probability 50% of extracting a boolean and 50% for integer
					if (Randomizer.generate(0, 1) == 0)
						m.addParameter(new BooleanParameter("Par" + i));
					else {
						// Define a new integer parameter
						while (computeParams) {
							from = Randomizer.generate(GeneratorConfiguration.LOWER_BOUND_INT, GeneratorConfiguration.UPPER_BOUND_INT);
							to = Randomizer.generate(GeneratorConfiguration.LOWER_BOUND_INT, GeneratorConfiguration.UPPER_BOUND_INT);
							
							if (from <= to) 
								computeParams = false;
						}
						
						m.addParameter(new IntegerParameter("Par" + i, from, to));
						computeParams = true;
					}
				}			
				
				break;
				
			case CONSTRAINTS_WITH_RELATIONAL:
				// With also enumerative and integers
				for (int i=0; i<n; i++) {
					// Probability 33% of extracting a boolean and 33% for enumerative and 33% for integers
					int parType = Randomizer.generate(0, 2);
					switch(parType) {
						case 0:
							m.addParameter(new BooleanParameter("Par" + i));
							break;
						case 1:
							// Define a new enumerative parameter
							nValues = Randomizer.generate(1, GeneratorConfiguration.DIM_ENUMS_MAX);
							EnumerativeParameter p = new EnumerativeParameter("Par" + i);
							
							for(int j=0; j<nValues; j++)
								p.addValue("PAR" + i + "_" + j);
							
							m.addParameter(p);
							break;
						case 2:
							// Define a new enumerative parameter
							while (computeParams) {
								from = Randomizer.generate(GeneratorConfiguration.LOWER_BOUND_INT, GeneratorConfiguration.UPPER_BOUND_INT);
								to = Randomizer.generate(GeneratorConfiguration.LOWER_BOUND_INT, GeneratorConfiguration.UPPER_BOUND_INT);
								
								if (from <= to) 
									computeParams = false;
							}
							
							// Define a new integer parameter
							m.addParameter(new IntegerParameter("Par" + i, from, to));
							computeParams = true;
					}
				}
				break;
		}
		
		return m;
	}

}
