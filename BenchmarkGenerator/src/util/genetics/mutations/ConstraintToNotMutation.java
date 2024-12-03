package util.genetics.mutations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.xtext.EcoreUtil2;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import ctwedge.ctWedge.CtWedgeFactory;
import ctwedge.ctWedge.Expression;
import ctwedge.ctWedge.NotExpression;
import ctwedge.ctWedge.impl.CtWedgeFactoryImpl;
import generators.Track;
import models.Model;

/**
 * Mutation that changes a constraint to its negation
 */
public class ConstraintToNotMutation implements EvolutionaryOperator<Model> {

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
	 * Apply the mutation to the selected candidates
	 * 
	 * @param selectedCandidates the selected candidates
	 * @param rng                the random number generator
	 * @return the mutated population
	 */
	@Override
	public List<Model> apply(List<Model> selectedCandidates, Random rng) {

		List<Model> mutatedPopulation = new ArrayList<Model>(selectedCandidates.size());
		for (Model m : selectedCandidates) {
			mutatedPopulation.add(mutateModel(m, rng));
		}
		return mutatedPopulation;

	}

	/**
	 * Mutate the model by changing a constraint to its negation
	 * 
	 * @param m   the model to mutate
	 * @param rng the random number generator
	 * @return the mutated model
	 */
	public Model mutateModel(Model m, Random rng) {
		Track track = m.getGeneratorConfiguration().TRACK;
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
