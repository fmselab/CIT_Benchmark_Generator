package util.genetics.mutations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import ctwedge.ctWedge.CtWedgePackage;
import ctwedge.ctWedge.Element;
import ctwedge.ctWedge.Enumerative;
import ctwedge.ctWedge.Parameter;
import ctwedge.ctWedge.Range;
import ctwedge.ctWedge.impl.CtWedgeFactoryImpl;
import ctwedge.ctWedge.impl.EnumerativeImpl;
import ctwedge.ctWedge.impl.RangeImpl;
import ctwedge.util.ParameterElementsGetterAsStrings;
import models.Model;

public class ParameterExtenderMutation implements EvolutionaryOperator<Model> {

	/**
	 * The probability for applying the mutation
	 */
	private float probability;

	/**
	 * Builds a new ParameterAdderMutation object
	 * 
	 * @param p the probability for applying the mutation
	 */
	public ParameterExtenderMutation(float p) {
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
		Model mTemp = m;

		// Check the probability
		if (rng.nextFloat(0, 1) < probability)
			return m;

		// Check if the model contains Enumerative or Integer parameters
		if (!m.getParameters().stream().anyMatch(x -> x instanceof Enumerative)
				&& !m.getParameters().stream().anyMatch(x -> x instanceof Range))
			return m;

		Parameter param;
		if (rng.nextFloat(0, 1) < 0.5) {
			param = mTemp.getRandomParamenterOfClass(new CtWedgeFactoryImpl().createEnumerative());
			// Get the previously contained elements and add a new one
			EnumerativeImpl p = (EnumerativeImpl) param;
			List<Element> elemsList = p.getElements();
			// Add the new element only if the maximum cardinality is not overpassed
			if (elemsList.size() < m.getGeneratorConfiguration().MAX_CARDINALITY) {
				Element e = new CtWedgeFactoryImpl().createElement();
				e.setName(p.getName().toUpperCase() + "_" + elemsList.size());
				elemsList.add(e);
				((EnumerativeImpl) p).eSet(CtWedgePackage.ENUMERATIVE__ELEMENTS, elemsList);
			}
		} else {
			param = mTemp.getRandomParamenterOfClass(new CtWedgeFactoryImpl().createRange());
			RangeImpl p = (RangeImpl) param;
			// Add the new element only if the maximum cardinality is not overpassed
			if (ParameterElementsGetterAsStrings.instance.doSwitch(p)
					.size() < m.getGeneratorConfiguration().MAX_CARDINALITY) {
				if (p.getBegin() > m.getGeneratorConfiguration().LOWER_BOUND_INT)
					p.setBegin(p.getBegin() - 1);
				else if (p.getEnd() < m.getGeneratorConfiguration().UPPER_BOUND_INT)
					p.setEnd(p.getEnd() + 1);
			}
		}
		if (param == null)
			return m;
		
		System.out.println("****** Adding a value to parameter " + param.getName());
		return mTemp;
	}

}
