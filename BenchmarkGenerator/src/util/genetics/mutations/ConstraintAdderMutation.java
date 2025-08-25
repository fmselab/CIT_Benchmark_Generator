package util.genetics.mutations;

import java.util.Random;

import org.uma.jmetal.operator.mutation.MutationOperator;

import ctwedge.ctWedge.Constraint;
import generators.GeneratorWithConstraintsInterface;
import generators.Track;
import generators.WithConstraintGenerator;
import generators.WithConstraintGeneratorCNF;
import generators.WithConstraintGeneratorFT;
import models.Model;
import util.genetics.solution.ModelSolution;

public class ConstraintAdderMutation implements MutationOperator<ModelSolution> {

	private static final long serialVersionUID = 1L;
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
	 * Executes the mutation
	 * 
	 * @param solution the solution to be mutated
	 * @return the mutation solution
	 */
	@Override
	public ModelSolution execute(ModelSolution solution) {
		Model m = solution.getModel();
		Model mutated = mutateModel(m);
		ModelSolution mutatedSolution = new ModelSolution(mutated, solution.variables().size(),
				solution.objectives().length);
		return mutatedSolution;
	}

	/**
	 * Gets the mutation probability
	 * 
	 * @return the probability
	 */
	@Override
	public double mutationProbability() {
		return probability;
	}

	/**
	 * Mutate the model by adding a constraint
	 * 
	 * @param m the model to mutate	 * 
	 * @return the mutated model
	 */
	public Model mutateModel(Model m) {
		Random rn = new Random();
		
		Track track = m.getGeneratorConfiguration().TRACK;
		// Unconstrained tracks do not support this mutation
		if (track == Track.MCA || track == Track.UNIFORM_ALL || track == Track.UNIFORM_BOOLEAN) {
			return m;
		}

		// Check the probability
		if (rn.nextFloat(0, 1) > probability)
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
			int complexity = rn.nextInt(m.getGeneratorConfiguration().MIN_CONSTRAINTS_COMPLEXITY,
					m.getGeneratorConfiguration().MAX_CONSTRAINTS_COMPLEXITY + 1);
			Constraint c = gen.generateConstraintFromComplexity(mTemp, complexity);
			mTemp.addConstraint(c);
		}

		return mTemp;
	}

}
