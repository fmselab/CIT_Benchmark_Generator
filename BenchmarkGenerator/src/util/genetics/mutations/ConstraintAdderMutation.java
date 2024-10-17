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

		// Constrained tracks
		Model mTemp = m;
		GeneratorWithConstraintsInterface gen;

		if (m.getGeneratorConfiguration().CNF)
			gen = new WithConstraintGeneratorCNF();
		else if (m.getGeneratorConfiguration().FORBIDDEN_TUPLES)
			gen = new WithConstraintGeneratorFT();
		else
			gen = new WithConstraintGenerator();

		int nConstraints = rng.nextInt(0, m.getGeneratorConfiguration().N_CONSTRAINTS_MAX - m.getConstraints().size());
		System.out.println("****** Adding " + nConstraints + " constraints");
		for (int i = 0; i < nConstraints; i++) {
			int complexity = rng.nextInt(m.getGeneratorConfiguration().MIN_CONSTRAINTS_COMPLEXITY,
					m.getGeneratorConfiguration().MAX_CONSTRAINTS_COMPLEXITY + 1);
			Constraint c = gen.generateConstraintFromComplexity(mTemp, complexity);
			mTemp.addConstraint(c);
		}

		return mTemp;
	}

}