package util.genetics.mutations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import ctwedge.ctWedge.CtWedgeFactory;
import ctwedge.ctWedge.Expression;
import ctwedge.ctWedge.NotExpression;
import ctwedge.ctWedge.impl.CtWedgeFactoryImpl;
import generators.Track;
import models.Model;

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

	@Override
	public List<Model> apply(List<Model> selectedCandidates, Random rng) {

		List<Model> mutatedPopulation = new ArrayList<Model>(selectedCandidates.size());
		for (Model m : selectedCandidates) {
			mutatedPopulation.add(mutateModel(m, rng));
		}
		return mutatedPopulation;

	}

	private Model mutateModel(Model m, Random rng) {
		Track track = m.getGeneratorConfiguration().TRACK;
		// Unconstrained tracks do not support this mutation
		if (track == Track.MCA || track == Track.UNIFORM_ALL || track == Track.UNIFORM_BOOLEAN) {
			return m;
		}

		// Check the probability
		if (rng.nextFloat(0, 1) < probability)
			return m;

		// Constrained tracks
		Model mTemp = m;

		int nConstraint = rng.nextInt(0, m.getConstraints().size()-1);
		System.out.println("****** Negating the " + nConstraint + "-th constraint");

		CtWedgeFactory factory = new CtWedgeFactoryImpl();
		NotExpression negatedConstraint = factory.createNotExpression();
		negatedConstraint.setPredicate((Expression)mTemp.getConstraints().get(nConstraint));
		mTemp.changeConstraint(mTemp.getConstraints().get(nConstraint), negatedConstraint);

		return mTemp;
	}

}
