package util.genetics.mutations;

import java.util.Random;

import ctwedge.ctWedge.ImpliesExpression;
import ctwedge.ctWedge.ImpliesOperator;
import ctwedge.ctWedge.impl.CtWedgeFactoryImpl;
import generators.Track;
import models.Model;

public class ConstraintDblImpliesToImpliesMutation extends ConstraintOperatorSubstitutionMutation {

	private static final long serialVersionUID = 1L;

	/**
	 * Builds a new ConstraintImpliesToDblImpliesMutation object
	 * 
	 * @param p the probability for applying the mutation
	 */
	public ConstraintDblImpliesToImpliesMutation(float p) {
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
		ImpliesExpression dbl = factory.createImpliesExpression();
		ImpliesExpression single = factory.createImpliesExpression();
		dbl.setOp(ImpliesOperator.IFF);
		single.setOp(ImpliesOperator.IMPL);
		System.out.println("****** Changing DPLIMPL in IMPL");
		Model mTemp = changeOperator(m, rng, dbl, single);

		return mTemp;
	}

}
