package generators;

import models.constraints.AndConstraint;
import models.constraints.AtomicConstraint;
import models.constraints.Constraint;
import models.constraints.DoubleImpliesConstraint;
import models.constraints.ImpliesConstraint;
import models.constraints.OrConstraint;
import models.BooleanParameter;
import models.EnumerativeParameter;
import models.Model;
import models.Parameter;

public class WithConstraintGenerator extends WithoutConstraintGenerator{
	
	@Override
	public Model generate(Category type) {
		// Compile the model with the parameters and not the constraints	
		Model m = super.generate(type);
		
		// Add the constraints
		int nConstraint = Randomizer.generate(GeneratorConfiguration.N_CONSTRAINTS_MIN, GeneratorConfiguration.N_CONSTRAINTS_MAX);
		for (int i=0; i<nConstraint; i++) {
			Constraint c;
			
			// Extract the complexity of the constraint (i.e., the number of parameters included)
			int complexity = Randomizer.generate(1, GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY);
			c = generateConstraintFromComplexity(m, complexity, type);
			
			// Add the constraint
			m.addConstraint(c);
		}
		
		return m;
	}
	
	public Constraint generateConstraintFromComplexity(Model m, int complexity, Category type) {
		
		Constraint c;
		int operation;
		Parameter p;
		
		if (complexity <= 1) {
			// Only an atomic constraint can be created
			c = new AtomicConstraint();
			
			// Parameter of the atomic expression
			p = m.getRandomParamenter();
			
			// Extract the operation
			int upperBound = (p instanceof BooleanParameter || p instanceof EnumerativeParameter) ? 1 : 5;
			operation = Randomizer.generate(0, upperBound);
					
			switch(operation) {
				case 0:
					// =
					// 50% comparison between parameters, 50% between parameter and its value
					if (Randomizer.generate(0, 1) == 0) {
						((AtomicConstraint)c).setExpression(p.getName() + " = " + p.getRandomValue());
					} else {
						((AtomicConstraint)c).setExpression(p.getName() + " = " + m.getRandomParamenterOfClass(p).getRandomValue());
					}
					break;
					
				case 1:
					// !=
					// 50% comparison between parameters, 50% between parameter and its value
					if (Randomizer.generate(0, 1) == 0) {
						((AtomicConstraint)c).setExpression(p.getName() + " != " + p.getRandomValue());
					} else {
						((AtomicConstraint)c).setExpression(p.getName() + " != " + m.getRandomParamenterOfClass(p).getRandomValue());
					}
					break;
					
				case 2:
					// >
					// 50% comparison between parameters, 50% between parameter and its value
					if (Randomizer.generate(0, 1) == 0) {
						((AtomicConstraint)c).setExpression(p.getName() + " > " + p.getRandomValue());
					} else {
						((AtomicConstraint)c).setExpression(p.getName() + " > " + m.getRandomParamenterOfClass(p).getRandomValue());
					}
					break;
					
				case 3:
					// >=
					// 50% comparison between parameters, 50% between parameter and its value
					if (Randomizer.generate(0, 1) == 0) {
						((AtomicConstraint)c).setExpression(p.getName() + " >= " + p.getRandomValue());
					} else {
						((AtomicConstraint)c).setExpression(p.getName() + " >= " + m.getRandomParamenterOfClass(p).getRandomValue());
					}
					break;
					
				case 4:
					// <
					// 50% comparison between parameters, 50% between parameter and its value
					if (Randomizer.generate(0, 1) == 0) {
						((AtomicConstraint)c).setExpression(p.getName() + " < " + p.getRandomValue());
					} else {
						((AtomicConstraint)c).setExpression(p.getName() + " < " + m.getRandomParamenterOfClass(p).getRandomValue());
					}
					break;
					
				case 5:
					// <=
					// 50% comparison between parameters, 50% between parameter and its value
					if (Randomizer.generate(0, 1) == 0) {
						((AtomicConstraint)c).setExpression(p.getName() + " <= " + p.getRandomValue());
					} else {
						((AtomicConstraint)c).setExpression(p.getName() + " <= " + m.getRandomParamenterOfClass(p).getRandomValue());
					}		
					break;
			}
		} else {
			operation = Randomizer.generate(0, 3);
			c = new Constraint();
			
			switch(operation) {
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
			
			c.setLeft(generateConstraintFromComplexity(m, complexity - 2, type));
			c.setRight(generateConstraintFromComplexity(m, complexity - 2, type));
		}
		
		return c;
	}

}
