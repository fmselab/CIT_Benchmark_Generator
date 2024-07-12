package util.genetics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import generators.GeneratorConfiguration;
import models.BooleanParameter;
import models.Model;

public class ParameterAdderMutation implements EvolutionaryOperator<Model> {

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
		switch (GeneratorConfiguration.TRACK) {
		case BOOLC:
			mTemp.addParameter(
					new BooleanParameter(mTemp.getParameters().get(mTemp.getParameters().size() - 1).getName() + "_"));
			break;
		case MCA:
		case MCAC:

		case NUMC:
		case UNIFORM_ALL:
		case UNIFORM_BOOLEAN:
			mTemp.addParameter(
					new BooleanParameter(mTemp.getParameters().get(mTemp.getParameters().size() - 1).getName() + "_"));
			break;
		}
		return mTemp;

	}

}
