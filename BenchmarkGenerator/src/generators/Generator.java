package generators;

import models.Model;

/**
 * Generator interface.
 * 
 * Each generator, given a category, must implement a method to generate a test
 * model
 * 
 * @author andrea
 *
 */
public interface Generator {

	public Model generate(Category type, GeneratorConfiguration config);

}
