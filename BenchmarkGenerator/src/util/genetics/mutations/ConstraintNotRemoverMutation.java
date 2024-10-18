package util.genetics.mutations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import ctwedge.ctWedge.Constraint;
import ctwedge.ctWedge.NotExpression;
import generators.Track;
import models.Model;

public class ConstraintNotRemoverMutation implements EvolutionaryOperator<Model> {

	/**
	 * The probability for applying the mutation
	 */
	private float probability;

	/**
	 * Builds a new ConstraintRemoverMutation object
	 * 
	 * @param p the probability for applying the mutation
	 */
	public ConstraintNotRemoverMutation(float p) {
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

		Model mTemp = m;
		final Set<Constraint> constraintsWithOperators = new HashSet<>();
		m.getConstraints().stream().forEach(x -> x.eAllContents().forEachRemaining(z -> {
			if (z instanceof NotExpression)
				constraintsWithOperators.add(x);
		}));

		if (constraintsWithOperators.size() > 0) {
			// Choose randomly one of the constraints including the operator
			int constraintIndexToUpdate = rng.nextInt(0, constraintsWithOperators.size());
			int constraintIndexInList = mTemp.getConstraints()
					.indexOf(constraintsWithOperators.toArray()[constraintIndexToUpdate]);
			

			ConstraintNotRemoverVisitor visitor = new ConstraintNotRemoverVisitor();
			mTemp.changeConstraint(mTemp.getConstraints().get(constraintIndexInList),
					visitor.doSwitch(mTemp.getConstraints().get(constraintIndexInList)));
		}
		return mTemp;
	}

}
