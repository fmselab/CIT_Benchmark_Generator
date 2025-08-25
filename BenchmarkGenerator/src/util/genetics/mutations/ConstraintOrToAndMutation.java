package util.genetics.mutations;

import java.util.Random;

import ctwedge.ctWedge.impl.CtWedgeFactoryImpl;
import generators.Track;
import models.Model;

public class ConstraintOrToAndMutation extends ConstraintOperatorSubstitutionMutation {

	private static final long serialVersionUID = 1L;

	/**
	 * Builds a new ConstraintImpliesToDblImpliesMutation object
	 * 
	 * @param p the probability for applying the mutation
	 */
	public ConstraintOrToAndMutation(float p) {
		super(p);
	}

	Model mutateModel(Model m) {
		Track track = m.getGeneratorConfiguration().TRACK;
		Random rng = new Random();
		
		// Unconstrained tracks do not support this mutation
		if (track == Track.MCA || track == Track.UNIFORM_ALL || track == Track.UNIFORM_BOOLEAN) {
			return m;
		}

		// Check the probability
		if (rng.nextFloat(0, 1) > probability)
			return m;

		// Constrained tracks
		CtWedgeFactoryImpl factory = new CtWedgeFactoryImpl();
		System.out.println("****** Changing OR in AND");
		Model mTemp = changeOperator(m, rng, factory.createOrExpression(), factory.createAndExpression());

		return mTemp;
	}

}
