package util.genetics.comparator;

import java.util.Comparator;

import util.genetics.solution.ModelSolution;

public class ModelDominanceComparator implements Comparator<ModelSolution> {
	
	@Override
	public int compare(ModelSolution s1, ModelSolution s2) {
		boolean s1Better = false;
		boolean s2Better = false;
		
		double distance1 = 0;
		double distance2 = 0;

		for (int i = 0; i < s1.objectives().length; i++) {
			double obj1 = s1.objectives()[i];
			double obj2 = s2.objectives()[i];
			
			distance1 += obj1 * obj1;
			distance2 += obj2 * obj2;
		}
		
		distance1 = Math.sqrt(distance1);
		distance2 = Math.sqrt(distance2);
		
		if (distance1 < distance2) {
			s1Better = true;
		} else if (distance2 < distance1) {
			s2Better = true;
		}

		if (s1Better && !s2Better) {
			return -1; // s1 dominates s2
		} else if (s2Better && !s1Better) {
			return 1; // s2 dominates s1
		} else {
			return 0; // non-dominated
		}
	}
	
}
