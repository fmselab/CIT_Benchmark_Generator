package generators;

import ctwedge.ctWedge.Constraint;
import models.Model;

public interface GeneratorWithConstraintsInterface extends Generator {

	public Constraint generateConstraintFromComplexity(Model m, int complexity);

}
