package util.genetics.mutations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.xtext.EcoreUtil2;
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

/**
 * Mutation that extends a parameter of the model by adding a new element
 */
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

	/**
	 * Apply the mutation to the selected candidates
	 * 
	 * @param selectedCandidates the selected candidates
	 * @param rng                the random number generator
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
	 * Mutate the model by extending a parameter
	 * 
	 * @param m   the model to mutate
	 * @param rng the random number generator
	 * @return the mutated model
	 */
	public Model mutateModel(Model m, Random rng) {
		Model mTemp = (Model) m.clone();

		// Check the probability
		if (rng.nextFloat(0, 1) > probability)
			return m;

		// Check if the model contains Enumerative or Integer parameters. Indeed,
		// Booleans cannot be extended
		if (!m.getParameters().stream().anyMatch(x -> x instanceof Enumerative)
				&& !m.getParameters().stream().anyMatch(x -> x instanceof Range))
			return m;

		Parameter param;
		if (rng.nextFloat(0, 1) < 0.5 || !m.getParameters().stream().anyMatch(x -> x instanceof Range)) {
			// Extend an enumerative
			param = mTemp.getRandomParamenterOfClass(new CtWedgeFactoryImpl().createEnumerative());
			if (param == null)
				return m;
			// Get the previously contained elements and add a new one
			List<Element> elemsList = ((EnumerativeImpl) param).getElements();
			List<Element> newElemsList = new ArrayList<Element>();
			// Add the new element only if the maximum cardinality is not overpassed
			if (elemsList.size() < m.getGeneratorConfiguration().MAX_CARDINALITY) {
				for (Element e : elemsList)
					newElemsList.add(EcoreUtil2.copy(e));
				Element e = new CtWedgeFactoryImpl().createElement();
				e.setName(param.getName().toUpperCase() + "_" + elemsList.size());
				newElemsList.add(e);
				((EnumerativeImpl) param).eSet(CtWedgePackage.ENUMERATIVE__ELEMENTS, newElemsList);
			}
		} else {
			// Extend a range
			param = mTemp.getRandomParamenterOfClass(new CtWedgeFactoryImpl().createRange());
			if (param == null)
				return m;
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

		System.out.println("****** Adding a value to the parameter " + param.getName());
		return mTemp;
	}

}
