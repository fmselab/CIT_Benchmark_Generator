package generators;

import models.Model;
import models.constraints.Constraint;

public interface GeneratorWithConstraintsInterface extends Generator {

	public Constraint generateConstraintFromComplexity(Model m, int complexity);

}
