package util;

import java.util.ArrayList;
import java.util.stream.Stream;

import generators.GeneratorConfiguration;
import models.BooleanParameter;
import models.EnumerativeParameter;
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
	
	/**
	 * Adds to the model m a new enumerative parameter, either by giving a random name
	 * or one chosen from the dictionary
	 * 
	 * @param m     the model
	 * @param card	the cardinality
	 * @param names the list of already used names
	 * @param i     the index
	 */
	public static void addEnumerativeParameter(Model m, int card, ArrayList<String> names, int i) {
		EnumerativeParameter p = null;		
		
		if (GeneratorConfiguration.DICTIONARY == null) {
			p = new EnumerativeParameter("Par" + i);
			for (int j = 0; j < card; j++)
				p.addValue("PAR" + i + "_" + j);
		}
		else {
			Stream<Dictionary> dictStream = GeneratorConfiguration.DICTIONARY.stream()
					.filter(x -> x.getType().equalsIgnoreCase("Enum") && !names.contains(x.getName()) && x.getValues().size() == card);
			
			// If no available parameter is found, this is the alternative
			Dictionary alternative = new Dictionary("Par" + i);
			for (int j = 0; j < card; j++)
				alternative.addValue("PAR" + i + "_" + j);
			
			Dictionary dict = dictStream.findAny().orElse(alternative);
			names.add(dict.getName());
			p = new EnumerativeParameter(dict.getName());
			for (int j = 0; j < card; j++)
				p.addValue(dict.values.get(j));
		}
		m.addParameter(p);
	}
	
}
