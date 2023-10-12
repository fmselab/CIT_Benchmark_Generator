package util;

import java.util.ArrayList;
import java.util.stream.Stream;

import generators.GeneratorConfiguration;
import models.BooleanParameter;
import models.Model;

public class ParameterToModelAdder {
	
	/**
	 * Adds to the model m a new boolean parameter, either by giving a random name
	 * or one chosen from the dictionary
	 * 
	 * @param m     the model
	 * @param names the list of already used names
	 * @param i     the index
	 */
	public static void addBooleanParameter(Model m, ArrayList<String> names, int i) {
		if (GeneratorConfiguration.DICTIONARY == null)
			m.addParameter(new BooleanParameter("Par" + i));
		else {
			Stream<Dictionary> dictStream = GeneratorConfiguration.DICTIONARY.stream()
					.filter(x -> x.getType().equalsIgnoreCase("Boolean") && !names.contains(x.getName()));
			Dictionary dict = dictStream.findAny().orElse(new Dictionary("Par" + i));
			names.add(dict.getName());
			m.addParameter(new BooleanParameter(dict.getName()));
		}
	}
	
}
