package util.genetics.mutations;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.uma.jmetal.operator.mutation.MutationOperator;

import ctwedge.ctWedge.Constraint;
import ctwedge.ctWedge.NotExpression;
import generators.Track;
import models.Model;
import util.genetics.mutations.visitors.ConstraintNotRemoverVisitor;
import util.genetics.solution.ModelSolution;

public class ConstraintNotRemoverMutation implements MutationOperator<ModelSolution> {

	private static final long serialVersionUID = 1L;

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
	 * Mutate the model by removing a NOT from a constraint
	 * 
	 * @param m the model to mutate	 * 
	 * @return the mutated model
	 */
	public Model mutateModel(Model m) {
		Track track = m.getGeneratorConfiguration().TRACK;
		Random rng = new Random();
		// Unconstrained tracks do not support this mutation
		if (track == Track.MCA || track == Track.UNIFORM_ALL || track == Track.UNIFORM_BOOLEAN) {
			return m;
		}

		// Check the probability
		if (rng.nextFloat(0, 1) > probability)
			return m;

		Model mTemp = (Model) m.clone();
		final Set<Constraint> constraintsWithOperators = new HashSet<>();
		m.getConstraints().stream().forEach(x -> x.eAllContents().forEachRemaining(z -> {
			if (z instanceof NotExpression)
				constraintsWithOperators.add(x);
		}));

		if (constraintsWithOperators.size() > 0) {
			// Choose randomly one of the constraints including the operator
			int constraintIndexToUpdate = rng.nextInt(0, constraintsWithOperators.size());
			int constraintIndexInList = m.getConstraints()
					.indexOf(constraintsWithOperators.toArray()[constraintIndexToUpdate]);
			
			System.out.println("****** Removing a NOT");

			ConstraintNotRemoverVisitor visitor = new ConstraintNotRemoverVisitor();
			mTemp.changeConstraint(mTemp.getConstraints().get(constraintIndexInList),
					visitor.doSwitch(mTemp.getConstraints().get(constraintIndexInList)));
		}
		return mTemp;
	}
}
