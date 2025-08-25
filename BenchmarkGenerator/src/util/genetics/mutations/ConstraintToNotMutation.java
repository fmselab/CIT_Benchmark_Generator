package util.genetics.mutations;

import java.util.Random;

import org.eclipse.xtext.EcoreUtil2;
import org.uma.jmetal.operator.mutation.MutationOperator;

import ctwedge.ctWedge.CtWedgeFactory;
import ctwedge.ctWedge.Expression;
import ctwedge.ctWedge.NotExpression;
import ctwedge.ctWedge.impl.CtWedgeFactoryImpl;
import generators.Track;
import models.Model;
import util.genetics.solution.ModelSolution;

/**
 * Mutation that changes a constraint to its negation
 */
public class ConstraintToNotMutation implements MutationOperator<ModelSolution> {

	private static final long serialVersionUID = 1L;

	/**
	 * The probability for applying the mutation
	 */
	private float probability;

	/**
	 * Builds a new ConstraintRemoverMutation object
	 * 
	 * @param p the probability for applying the mutation
	 */
	public ConstraintToNotMutation(float p) {
		this.probability = p;
	}

	/**
	 * Executes the mutation
	 * 
	 * @param solution the solution to be mutated
	 * @return the mutation solution
	 */
	@Override
	public ModelSolution execute(ModelSolution solution) {
		Model m = solution.getModel();
		Model mutated = mutateModel(m);
		ModelSolution mutatedSolution = new ModelSolution(mutated, solution.variables().size(),
				solution.objectives().length);
		return mutatedSolution;
	}

	/**
	 * Gets the mutation probability
	 * 
	 * @return the probability
	 */
	@Override
	public double mutationProbability() {
		return probability;
	}

	/**
	 * Mutate the model by negating a constraint
	 * 
	 * @param m the model to mutate	 * 
	 * @return the mutated model
	 */
	public Model mutateModel(Model m) {
		Track track = m.getGeneratorConfiguration().TRACK;
		Random rng = new Random();
		
		// Unconstrained tracks do not support this mutation
		if (track == Track.MCA || track == Track.UNIFORM_ALL || track == Track.UNIFORM_BOOLEAN) {
			return m;
		}

		// Check the probability
		if (rng.nextFloat(0, 1) > probability)
			return m;

		Model mTemp = (Model) m.clone();

		// In order to negate a constraint, there must be at least one constraint
		if (m.getConstraints().size() > 0) {

			int nConstraint = m.getConstraints().size() > 1 ? rng.nextInt(0, m.getConstraints().size() - 1) : 0;
			System.out.println(
					"****** Negating the " + nConstraint + "-th constraint over " + (m.getConstraints().size() - 1));

			CtWedgeFactory factory = new CtWedgeFactoryImpl();
			NotExpression negatedConstraint = factory.createNotExpression();
			negatedConstraint
					.setPredicate((Expression) EcoreUtil2.cloneIfContained(mTemp.getConstraints().get(nConstraint)));
			mTemp.changeConstraint(mTemp.getConstraints().get(nConstraint), negatedConstraint);

		}

		return mTemp;
	}

}
