package generators;

import models.constraints.AndConstraint;
import models.constraints.AtomicConstraint;
import models.constraints.Constraint;
import models.constraints.DoubleImpliesConstraint;
import models.constraints.ImpliesConstraint;
import models.constraints.NotConstraint;
import models.constraints.OrConstraint;
import models.BooleanParameter;
import models.EnumerativeParameter;
import models.Model;
import models.Parameter;

/**
 * Generates a new model with constraints, and with parameters of the category
 * given as parameter
 * 
 * 
 * @author andrea
 *
 */
public class WithConstraintGenerator extends WithoutConstraintGenerator {

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

			// Extract the complexity of the constraint (i.e., the number of parameters
			// included)
			int complexity = Randomizer.generate(GeneratorConfiguration.MIN_CONSTRAINTS_COMPLEXITY,
					GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY);
			c = generateConstraintFromComplexity(m, complexity);

			// Add the constraint
			m.addConstraint(c);
		}

		return m;
	}

	/**
	 * Generates a constraint with a given complexity
	 * 
	 * @param m          the model being populated
	 * @param complexity the constraint complexity
	 * @return the constraint
	 */
	public Constraint generateConstraintFromComplexity(Model m, int complexity) {

		Constraint c;
		int operation;
		Parameter p;

		if (complexity <= 1) {
			// Only an atomic constraint can be created, 50% normal constraint, 50% not
			// constraint
			if (Randomizer.generate(0, 1) == 0) {
				c = new AtomicConstraint();
			} else {
				c = new NotConstraint();
			}

			// Parameter of the atomic expression
			p = m.getRandomParamenter();

			// Extract the operation
			int upperBound = (p instanceof BooleanParameter || p instanceof EnumerativeParameter) ? 1 : 5;
			operation = Randomizer.generate(0, upperBound);

			switch (operation) {
			case 0:
				// =
				// 50% comparison between parameters, 50% between parameter and its value
				if (Randomizer.generate(0, 1) == 0 || !GeneratorConfiguration.USE_CONSTRAINTS_BETWEEN_PARAMETERS) {
					((AtomicConstraint) c).setExpression(p.getName() + " = " + p.getRandomValue());
				} else {
					((AtomicConstraint) c)
							.setExpression(p.getName() + " = " + m.getRandomParamenterOfClass(p).getName());
				}
				break;

			case 1:
				// !=
				// 50% comparison between parameters, 50% between parameter and its value
				if (Randomizer.generate(0, 1) == 0 || !GeneratorConfiguration.USE_CONSTRAINTS_BETWEEN_PARAMETERS) {
					((AtomicConstraint) c).setExpression(p.getName() + " != " + p.getRandomValue());
				} else {
					((AtomicConstraint) c)
							.setExpression(p.getName() + " != " + m.getRandomParamenterOfClass(p).getName());
				}
				break;

			case 2:
				// >
				// 50% comparison between parameters, 50% between parameter and its value
				if (Randomizer.generate(0, 1) == 0 || !GeneratorConfiguration.USE_CONSTRAINTS_BETWEEN_PARAMETERS) {
					((AtomicConstraint) c).setExpression(p.getName() + " > " + p.getRandomValue());
				} else {
					((AtomicConstraint) c)
							.setExpression(p.getName() + " > " + m.getRandomParamenterOfClass(p).getName());
				}
				break;

			case 3:
				// >=
				// 50% comparison between parameters, 50% between parameter and its value
				if (Randomizer.generate(0, 1) == 0 || !GeneratorConfiguration.USE_CONSTRAINTS_BETWEEN_PARAMETERS) {
					((AtomicConstraint) c).setExpression(p.getName() + " >= " + p.getRandomValue());
				} else {
					((AtomicConstraint) c)
							.setExpression(p.getName() + " >= " + m.getRandomParamenterOfClass(p).getName());
				}
				break;

			case 4:
				// <
				// 50% comparison between parameters, 50% between parameter and its value
				if (Randomizer.generate(0, 1) == 0 || !GeneratorConfiguration.USE_CONSTRAINTS_BETWEEN_PARAMETERS) {
					((AtomicConstraint) c).setExpression(p.getName() + " < " + p.getRandomValue());
				} else {
					((AtomicConstraint) c)
							.setExpression(p.getName() + " < " + m.getRandomParamenterOfClass(p).getName());
				}
				break;

			case 5:
				// <=
				// 50% comparison between parameters, 50% between parameter and its value
				if (Randomizer.generate(0, 1) == 0 || !GeneratorConfiguration.USE_CONSTRAINTS_BETWEEN_PARAMETERS) {
					((AtomicConstraint) c).setExpression(p.getName() + " <= " + p.getRandomValue());
				} else {
					((AtomicConstraint) c)
							.setExpression(p.getName() + " <= " + m.getRandomParamenterOfClass(p).getName());
				}
				break;
			}
		} else {
			operation = Randomizer.generate(0, 3);
			c = new Constraint();

			switch (operation) {
			case 0:
				c = new AndConstraint();
				break;
			case 1:
				c = new OrConstraint();
				break;
			case 2:
				c = new ImpliesConstraint();
				break;
			case 3:
				c = new DoubleImpliesConstraint();
				break;
			}

			c.setLeft(generateConstraintFromComplexity(m, (complexity - 1) / 2));
			c.setRight(generateConstraintFromComplexity(m, (complexity - 1) / 2));
		}

		return c;
	}

}
