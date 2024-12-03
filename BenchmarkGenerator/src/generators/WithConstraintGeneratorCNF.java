package generators;

import java.util.List;
import java.util.Random;

import ctwedge.ctWedge.AtomicPredicate;
import ctwedge.ctWedge.Bool;
import ctwedge.ctWedge.Constraint;
import ctwedge.ctWedge.CtWedgeFactory;
import ctwedge.ctWedge.Enumerative;
import ctwedge.ctWedge.EqualExpression;
import ctwedge.ctWedge.Expression;
import ctwedge.ctWedge.NotExpression;
import ctwedge.ctWedge.Operators;
import ctwedge.ctWedge.Parameter;
import ctwedge.ctWedge.RelationalExpression;
import ctwedge.ctWedge.impl.AndExpressionImpl;
import ctwedge.ctWedge.impl.CtWedgeFactoryImpl;
import ctwedge.ctWedge.impl.OrExpressionImpl;
import ctwedge.util.ParameterElementsGetterAsStrings;
import models.Model;

/**
 * Generates a new model with constraints, and with parameters of the category
 * given as parameter. The constraints are in CNF
 * 
 * 
 * @author andrea
 *
 */
public class WithConstraintGeneratorCNF extends WithoutConstraintGenerator
		implements GeneratorWithConstraintsInterface {

	/**
	 * Generate an IPM
	 * 
	 * @param type   the type of models to be generated (with booleans, with
	 *               enumeratives, with integers, ...)
	 * @param config the generator configuration used for generating the model
	 * @return the generated IPM
	 */
	@Override
	public Model generate(Category type, GeneratorConfiguration config) {
		// Compile the model with the parameters and not the constraints
		Model m = super.generate(type, config);
		m.setGeneratorConfiguration(config);

		// Add the constraints
		int nConstraint = Randomizer.generate(m.getGeneratorConfiguration().N_CONSTRAINTS_MIN,
				m.getGeneratorConfiguration().N_CONSTRAINTS_MAX);
		for (int i = 0; i < nConstraint; i++) {
			Constraint c;

			// Extract the complexity of the constraint (i.e., the number of parameters
			// included)
			int complexity = Randomizer.generate(m.getGeneratorConfiguration().MIN_CONSTRAINTS_COMPLEXITY,
					m.getGeneratorConfiguration().MAX_CONSTRAINTS_COMPLEXITY);
			c = generateConstraintFromComplexity(m, complexity);

			// Add the constraint
			m.addConstraint(c);
		}

		return m;
	}

	/**
	 * Generates a constraint with a given complexity in CNF form
	 * 
	 * @param m          the model being populated
	 * @param complexity the constraint complexity
	 * @return the constraint
	 */
	public Expression generateConstraintFromComplexity(Model m, int complexity) {

		Expression c;
		int operation;
		Parameter p;
		CtWedgeFactory factory = new CtWedgeFactoryImpl();

		if (complexity <= 1) {
			// Only an atomic constraint can be created, 50% normal constraint, 50% not
			// constraint
			if (Randomizer.generate(0, 1) == 0) {
				c = factory.createAtomicPredicate();
			} else {
				c = factory.createNotExpression();
			}

			// Parameter of the atomic expression
			p = m.getRandomParamenter();

			// Extract the operation
			int upperBound = (p instanceof Bool || p instanceof Enumerative) ? 1 : 5;
			operation = Randomizer.generate(0, upperBound);
			List<String> parameterValues = ParameterElementsGetterAsStrings.instance.caseParameter(p);
			String value = parameterValues.get(new Random().nextInt(0, parameterValues.size()));

			Expression exp = factory.createEqualExpression();
			switch (operation) {
			case 0:
				// =
				((EqualExpression) exp).setOp(Operators.EQ);
				// 50% comparison between parameters, 50% between parameter and its value
				if (Randomizer.generate(0, 1) == 0
						|| !m.getGeneratorConfiguration().USE_CONSTRAINTS_BETWEEN_PARAMETERS) {
					AtomicPredicate right = factory.createAtomicPredicate();
					right.setName(value);
					((EqualExpression) exp).setRight(right);

					AtomicPredicate left = factory.createAtomicPredicate();
					left.setName(p.getName());
					((EqualExpression) exp).setLeft(left);
				} else {
					AtomicPredicate right = factory.createAtomicPredicate();
					right.setName(m.getRandomParamenterOfClass(p).getName());
					((EqualExpression) exp).setRight(right);

					AtomicPredicate left = factory.createAtomicPredicate();
					left.setName(p.getName());
					((EqualExpression) exp).setLeft(left);
				}

			case 1:
				// !=
				((EqualExpression) exp).setOp(Operators.NE);
				// 50% comparison between parameters, 50% between parameter and its value
				if (Randomizer.generate(0, 1) == 0
						|| !m.getGeneratorConfiguration().USE_CONSTRAINTS_BETWEEN_PARAMETERS) {
					AtomicPredicate right = factory.createAtomicPredicate();
					right.setName(value);
					((EqualExpression) exp).setRight(right);

					AtomicPredicate left = factory.createAtomicPredicate();
					left.setName(p.getName());
					((EqualExpression) exp).setLeft(left);
				} else {
					AtomicPredicate right = factory.createAtomicPredicate();
					right.setName(m.getRandomParamenterOfClass(p).getName());
					((EqualExpression) exp).setRight(right);

					AtomicPredicate left = factory.createAtomicPredicate();
					left.setName(p.getName());
					((EqualExpression) exp).setLeft(left);
				}
				break;

			case 2:
				// >
				exp = factory.createRelationalExpression();
				((RelationalExpression) exp).setOp(Operators.GT);
				// 50% comparison between parameters, 50% between parameter and its value
				if (Randomizer.generate(0, 1) == 0
						|| !m.getGeneratorConfiguration().USE_CONSTRAINTS_BETWEEN_PARAMETERS) {
					AtomicPredicate right = factory.createAtomicPredicate();
					right.setName(value);
					((RelationalExpression) exp).setRight(right);

					AtomicPredicate left = factory.createAtomicPredicate();
					left.setName(p.getName());
					((RelationalExpression) exp).setLeft(left);
				} else {
					AtomicPredicate right = factory.createAtomicPredicate();
					right.setName(m.getRandomParamenterOfClass(p).getName());
					((RelationalExpression) exp).setRight(right);

					AtomicPredicate left = factory.createAtomicPredicate();
					left.setName(p.getName());
					((RelationalExpression) exp).setLeft(left);
				}
				break;

			case 3:
				// >=
				exp = factory.createRelationalExpression();
				((RelationalExpression) exp).setOp(Operators.GE);
				// 50% comparison between parameters, 50% between parameter and its value
				if (Randomizer.generate(0, 1) == 0
						|| !m.getGeneratorConfiguration().USE_CONSTRAINTS_BETWEEN_PARAMETERS) {
					AtomicPredicate right = factory.createAtomicPredicate();
					right.setName(value);
					((RelationalExpression) exp).setRight(right);

					AtomicPredicate left = factory.createAtomicPredicate();
					left.setName(p.getName());
					((RelationalExpression) exp).setLeft(left);
				} else {
					AtomicPredicate right = factory.createAtomicPredicate();
					right.setName(m.getRandomParamenterOfClass(p).getName());
					((RelationalExpression) exp).setRight(right);

					AtomicPredicate left = factory.createAtomicPredicate();
					left.setName(p.getName());
					((RelationalExpression) exp).setLeft(left);
				}
				break;

			case 4:
				// <
				exp = factory.createRelationalExpression();
				((RelationalExpression) exp).setOp(Operators.LT);
				// 50% comparison between parameters, 50% between parameter and its value
				if (Randomizer.generate(0, 1) == 0
						|| !m.getGeneratorConfiguration().USE_CONSTRAINTS_BETWEEN_PARAMETERS) {
					AtomicPredicate right = factory.createAtomicPredicate();
					right.setName(value);
					((RelationalExpression) exp).setRight(right);

					AtomicPredicate left = factory.createAtomicPredicate();
					left.setName(p.getName());
					((RelationalExpression) exp).setLeft(left);
				} else {
					AtomicPredicate right = factory.createAtomicPredicate();
					right.setName(m.getRandomParamenterOfClass(p).getName());
					((RelationalExpression) exp).setRight(right);

					AtomicPredicate left = factory.createAtomicPredicate();
					left.setName(p.getName());
					((RelationalExpression) exp).setLeft(left);
				}
				break;

			case 5:
				// <=
				exp = factory.createRelationalExpression();
				((RelationalExpression) exp).setOp(Operators.LE);
				// 50% comparison between parameters, 50% between parameter and its value
				if (Randomizer.generate(0, 1) == 0
						|| !m.getGeneratorConfiguration().USE_CONSTRAINTS_BETWEEN_PARAMETERS) {
					AtomicPredicate right = factory.createAtomicPredicate();
					right.setName(value);
					((RelationalExpression) exp).setRight(right);

					AtomicPredicate left = factory.createAtomicPredicate();
					left.setName(p.getName());
					((RelationalExpression) exp).setLeft(left);
				} else {
					AtomicPredicate right = factory.createAtomicPredicate();
					right.setName(m.getRandomParamenterOfClass(p).getName());
					((RelationalExpression) exp).setRight(right);

					AtomicPredicate left = factory.createAtomicPredicate();
					left.setName(p.getName());
					((RelationalExpression) exp).setLeft(left);
				}
				break;
			}

			if (c instanceof NotExpression)
				((NotExpression) c).setPredicate(exp);
			else
				c = exp;
		} else if (complexity <= 3) {
			c = factory.createOrExpression();
			((OrExpressionImpl) c).setLeft(generateConstraintFromComplexity(m, (complexity - 1) / 2));
			((OrExpressionImpl) c).setRight(generateConstraintFromComplexity(m, (complexity - 1) / 2));
		} else {
			c = factory.createAndExpression();
			((AndExpressionImpl) c).setLeft(generateConstraintFromComplexity(m, (complexity - 1) / 2));
			((AndExpressionImpl) c).setRight(generateConstraintFromComplexity(m, (complexity - 1) / 2));
		}

		return c;
	}

}
