package models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

public class ModelList implements Iterable<Model>{
	ArrayList<Model> models;
	
	public ModelList() {
		models = new ArrayList<Model>();
	}
	
	public void addModel(Model m) {
		models.add(m);
	}
	
	public Model getModelByName(String name) {
		Optional<Model> model = models.stream().filter(x -> x.getName().equals(name)).findFirst();
		if (model.isPresent())
			return model.get();
		return null;
	}
	
	public void clearModels() {
		models.clear();
	}

	@Override
	public Iterator<Model> iterator() {
		return models.iterator();
	}
}
