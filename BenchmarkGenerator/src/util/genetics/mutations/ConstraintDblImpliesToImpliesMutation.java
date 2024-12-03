package util.genetics.mutations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ctwedge.ctWedge.ImpliesExpression;
import ctwedge.ctWedge.ImpliesOperator;
import ctwedge.ctWedge.impl.CtWedgeFactoryImpl;
import generators.Track;
import models.Model;

public class ConstraintDblImpliesToImpliesMutation extends ConstraintOperatorSubstitutionMutation {

	/**
	 * Builds a new ConstraintImpliesToDblImpliesMutation object
	 * 
	 * @param p the probability for applying the mutation
	 */
	public ConstraintDblImpliesToImpliesMutation(float p) {
		super(p);
	}

	@Override
	public List<Model> apply(List<Model> selectedCandidates, Random rng) {

		List<Model> mutatedPopulation = new ArrayList<Model>(selectedCandidates.size());
		for (Model m : selectedCandidates) {
			try {
				mutatedPopulation.add(mutateModel(m, rng));
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		return mutatedPopulation;

	}

	Model mutateModel(Model m, Random rng) throws CloneNotSupportedException {
		Track track = m.getGeneratorConfiguration().TRACK;
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
