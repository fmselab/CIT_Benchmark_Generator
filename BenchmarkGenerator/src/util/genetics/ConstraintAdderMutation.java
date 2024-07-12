package util.genetics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import models.BooleanParameter;
import models.Model;
import models.constraints.Constraint;

public class ConstraintAdderMutation implements EvolutionaryOperator<Model> {

	@Override
	public List<Model> apply(List<Model> selectedCandidates, Random rng) {

		List<Model> mutatedPopulation = new ArrayList<Model>(selectedCandidates.size());
		for (Model m : selectedCandidates) {
			mutatedPopulation.add(mutateModel(m, rng));
		}
		return mutatedPopulation;

	}

	private Model mutateModel(Model m, Random rng) {
		Model mTemp = m; 
		mTemp.addConstraint(new Constraint());
		return mTemp;
	}

}
