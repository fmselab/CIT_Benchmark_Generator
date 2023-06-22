package generators;

import models.Model;
import models.Parameter;
import models.constraints.AtomicConstraint;
import models.constraints.Constraint;
import models.constraints.NotConstraint;

/**
 * Generates a new model with constraints, and with parameters of the category
 * given as parameter. The constraints are expressed as Forbidden Tuples
 * 
 * 
 * @author andrea
 *
 */
public class WithConstraintGeneratorFT extends WithoutConstraintGenerator {

	/**
	 * Generate an IPM
	 * 
	 * @param type the type of models to be generated (with booleans, with
	 *             enumeratives, with integers, ...)
	 * @return the generated IPM
	 */
	@Override
	public Model generate(Category type) {
		// Compile the model with the parameters and not the constraints
		Model m = super.generate(type);

		// Add the constraints
		int nConstraint = Randomizer.generate(GeneratorConfiguration.N_CONSTRAINTS_MIN,
				GeneratorConfiguration.N_CONSTRAINTS_MAX);
		for (int i = 0; i < nConstraint; i++) {
			Constraint c;

			// Considering MFTs, the constraints can be of two different types
			// 1. NOT (A = A1 AND B = B2 AND ...)
			// 2. A!=A1 or B!=B2 ...
			// Thus, a recursive approach is not suitable in this case

			// Extract the complexity of the constraint (i.e., the number of parameters
			// included)
			int complexity = Randomizer.generate(GeneratorConfiguration.MIN_CONSTRAINTS_COMPLEXITY,
					GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY);

			// Now, decide whether use the (1)st or (2)nd solution
			if (Randomizer.generate(0, 1) == 0) {
				// First solution
				c = new NotConstraint();
				String cnstrAsString = "";
				for (int j = 0; j < complexity; j++) {
					// Parameter of the atomic expression
					Parameter p = m.getRandomParamenter();
					// Build the new atomic constraint
					Constraint c1 = new AtomicConstraint();
					if (Randomizer.generate(0, 1) == 0 || !GeneratorConfiguration.USE_CONSTRAINTS_BETWEEN_PARAMETERS) {
						((AtomicConstraint) c1).setExpression(p.getName() + " = " + p.getRandomValue());
					} else {
						((AtomicConstraint) c1)
								.setExpression(p.getName() + " = " + m.getRandomParamenterOfClass(p).getName());
					}
					// Convert the constraint as a string
					cnstrAsString += c1.toString() + " AND ";
				}
				// Set the constraint
				((NotConstraint) c).setExpression(cnstrAsString.substring(0, cnstrAsString.length() - 4));
			} else {
				// Second solution
				c = new AtomicConstraint();
				String cnstrAsString = "";
				for (int j = 0; j < complexity; j++) {
					// Parameter of the atomic expression
					Parameter p = m.getRandomParamenter();
					// Build the new atomic constraint
					Constraint c1 = new AtomicConstraint();
					if (Randomizer.generate(0, 1) == 0 || !GeneratorConfiguration.USE_CONSTRAINTS_BETWEEN_PARAMETERS) {
						((AtomicConstraint) c1).setExpression(p.getName() + " != " + p.getRandomValue());
					} else {
						((AtomicConstraint) c1)
								.setExpression(p.getName() + " != " + m.getRandomParamenterOfClass(p).getName());
					}
					// Convert the constraint as a string
					cnstrAsString += c1.toString() + " OR ";
				}
				// Set the constraint
				((AtomicConstraint) c).setExpression(cnstrAsString.substring(0, cnstrAsString.length() - 3));
			}

			// Add the constraint
			m.addConstraint(c);
		}

		return m;
	}
}
