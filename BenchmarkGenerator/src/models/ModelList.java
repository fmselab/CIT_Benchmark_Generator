package models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

/**
 * A list of models generated by the BenchmarkGenerator
 * 
 * It is iterable
 * 
 * @author andrea
 *
 */
public class ModelList implements Iterable<Model> {

	/**
	 * The list
	 */
	ArrayList<Model> models;

	/**
	 * Build an empty model list
	 */
	public ModelList() {
		models = new ArrayList<Model>();
	}

	/**
	 * Add a model m to the lsit
	 * 
	 * @param m the model
	 */
	public void addModel(Model m) {
		models.add(m);
	}

	/**
	 * Add all the models to the list
	 * 
	 * @param list the list of models
	 */
	public void addModel(ArrayList<Model> list) {
		models.addAll(list);
	}

	/**
	 * Returns a model from the list, given its name
	 * 
	 * @param name the name of the model
	 * @return the model corresponding to the name given as parameter. NULL is
	 *         returned when the model is not found
	 */
	public Model getModelByName(String name) {
		Optional<Model> model = models.stream()
				.filter(x -> (x.getName().equals(name) || x.getName().equals(name.trim()))).findFirst();
		if (model.isPresent())
			return model.get();
		return null;
	}

	/**
	 * Clear the list of models
	 */
	public void clearModels() {
		models.clear();
	}

	/**
	 * Returns the iterator over the list of models
	 * 
	 * @return the iterator
	 */
	@Override
	public Iterator<Model> iterator() {
		return models.iterator();
	}
}
