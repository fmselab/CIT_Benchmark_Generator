package util.genetics.mutations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import ctwedge.ctWedge.Constraint;
import models.Model;

public abstract class ConstraintOperatorSubstitutionMutation implements EvolutionaryOperator<Model> {

	/**
	 * The probability for applying the mutation
	 */
	float probability;

	/**
	 * Builds a new ConstraintOperatorSubstitutionMutation object
	 * 
	 * @param p the probability for applying the mutation
	 */
	public ConstraintOperatorSubstitutionMutation(float p) {
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

	abstract Model mutateModel(Model m, Random rng);

	Model changeOperator(Model m, Random rng, String from, String to) {
		Model mTemp = m;

		List<Constraint> constraintList = mTemp.getConstraints();
		constraintList = constraintList.stream().filter(x -> x.toString().contains(from)).collect(Collectors.toList());

		if (constraintList.size() > 0) {
			// Choose randomly one of the constraints including the operator
			int constraintIndexToUpdate = rng.nextInt(0, constraintList.size());
			// Count how many operators there are
			int countOperator = StringUtils.countMatches(constraintList.get(constraintIndexToUpdate).toString(), from);
			// Choose which one to update
			int operatorIndex = rng.nextInt(0, countOperator);

			int i = 0;
			for (Constraint c : m.getConstraints()) {
				if (c.toString().equals(constraintList.get(constraintIndexToUpdate).toString())) {

					break;
				}
				i++;
			}
		}
		return mTemp;
	}

}
