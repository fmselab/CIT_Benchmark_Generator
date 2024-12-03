package util.genetics.mutations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import ctwedge.ctWedge.Constraint;
import generators.GeneratorWithConstraintsInterface;
import generators.Track;
import generators.WithConstraintGenerator;
import generators.WithConstraintGeneratorCNF;
import generators.WithConstraintGeneratorFT;
import models.Model;

public class ConstraintAdderMutation implements EvolutionaryOperator<Model> {

	/**
	 * The probability for applying the mutation
	 */
	private float probability;

	/**
	 * Builds a new ConstraintAdderMutation object
	 * 
	 * @param p the probability for applying the mutation
	 */
	public ConstraintAdderMutation(float p) {
		this.probability = p;
	}

	/**
	 * Apply the mutation to the selected candidates
	 * 
	 * @param selectedCandidates the selected candidates
	 * @param rng                the random
	 * @return the mutated population
	 */
	@Override
	public List<Model> apply(List<Model> selectedCandidates, Random rng) {

		List<Model> mutatedPopulation = new ArrayList<Model>(selectedCandidates.size());
		for (Model m : selectedCandidates) {
			mutatedPopulation.add(mutateModel(m, rng));
		}
		return mutatedPopulation;

	}

	/*
	 * Mutate the model by adding a constraint
	 * 
	 * @param m the model to mutate
	 * @param rng the random number generator
	 * @return the mutated model
	 */
	public Model mutateModel(Model m, Random rng) {
		Track track = m.getGeneratorConfiguration().TRACK;
		// Unconstrained tracks do not support this mutation
		if (track == Track.MCA || track == Track.UNIFORM_ALL || track == Track.UNIFORM_BOOLEAN) {
			return m;
		}

		// Check the probability
		if (rng.nextFloat(0, 1) > probability)
			return m;

		// Constrained tracks
		Model mTemp = (Model) m.clone();
		GeneratorWithConstraintsInterface gen;

		if (m.getGeneratorConfiguration().CNF)
			gen = new WithConstraintGeneratorCNF();
		else if (m.getGeneratorConfiguration().FORBIDDEN_TUPLES)
			gen = new WithConstraintGeneratorFT();
		else
			gen = new WithConstraintGenerator();

		if (m.getGeneratorConfiguration().N_CONSTRAINTS_MAX > m.getConstraints().size()) {
			System.out.println("****** Adding a constraint");
			int complexity = rng.nextInt(m.getGeneratorConfiguration().MIN_CONSTRAINTS_COMPLEXITY,
					m.getGeneratorConfiguration().MAX_CONSTRAINTS_COMPLEXITY + 1);
			Constraint c = gen.generateConstraintFromComplexity(mTemp, complexity);
			mTemp.addConstraint(c);
		}

		return mTemp;

	}

}
