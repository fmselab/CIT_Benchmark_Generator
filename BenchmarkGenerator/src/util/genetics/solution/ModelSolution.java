package util.genetics.solution;

import java.util.List;
import java.util.Map;

import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.Solution;

import models.Model;

public class ModelSolution extends AbstractSolution<Model> {

	private static final long serialVersionUID = 1L;
	Model model;

	public ModelSolution() {
		super(1, 1, 0);
	}

	public ModelSolution(Model m, int nVariables, int nObjectives) {
		super(nVariables, nObjectives, 0);
		this.model = m;
	}

	@Override
	public List<Model> variables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] objectives() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] constraints() {
		return null;
	}

	@Override
	public Map<Object, Object> attributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Solution<Model> copy() {
		// TODO Auto-generated method stub
		return null;
	}

	public Model getModel() {
		return model;
	}

}
