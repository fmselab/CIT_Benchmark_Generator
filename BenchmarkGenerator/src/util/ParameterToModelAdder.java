package util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import ctwedge.ctWedge.Bool;
import ctwedge.ctWedge.CtWedgePackage;
import ctwedge.ctWedge.Element;
import ctwedge.ctWedge.Enumerative;
import ctwedge.ctWedge.Range;
import ctwedge.ctWedge.impl.CtWedgeFactoryImpl;
import ctwedge.ctWedge.impl.EnumerativeImpl;
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
		if (m.getGeneratorConfiguration().DICTIONARY == null) {
			Bool p = new CtWedgeFactoryImpl().createBool();
			p.setName("Par" + i);
			m.addParameter(p);
		} else {
			Stream<Dictionary> dictStream = m.getGeneratorConfiguration().DICTIONARY.stream()
					.filter(x -> x.getType().equalsIgnoreCase("Boolean") && !names.contains(x.getName()));
			Dictionary dict = dictStream.findAny().orElse(new Dictionary("Par" + i));
			names.add(dict.getName());
			Bool p = new CtWedgeFactoryImpl().createBool();
			p.setName(dict.getName());
			m.addParameter(p);
		}
	}

	/**
	 * Adds to the model m a new enumerative parameter, either by giving a random
	 * name or one chosen from the dictionary
	 * 
	 * @param m     the model
	 * @param card  the cardinality
	 * @param names the list of already used names
	 * @param i     the index
	 */
	public static void addEnumerativeParameter(Model m, int card, ArrayList<String> names, int i) {
		Enumerative p = new CtWedgeFactoryImpl().createEnumerative();

		if (m.getGeneratorConfiguration().DICTIONARY == null) {
			p.setName("Par" + i);
			List<Element> elemsList = new ArrayList<>();
			for (int j = 0; j < card; j++) {
				Element e = new CtWedgeFactoryImpl().createElement();
				e.setName("PAR" + i + "_" + j);
				elemsList.add(e);
			}
			((EnumerativeImpl)p).eSet(CtWedgePackage.ENUMERATIVE__ELEMENTS, elemsList);
		} else {
			Stream<Dictionary> dictStream = m.getGeneratorConfiguration().DICTIONARY.stream()
					.filter(x -> x.getType().equalsIgnoreCase("Enum") && !names.contains(x.getName())
							&& x.getValues().size() == card);

			// If no available parameter is found, this is the alternative
			Dictionary alternative = new Dictionary("Par" + i);
			for (int j = 0; j < card; j++)
				alternative.addValue("PAR" + i + "_" + j);

			Dictionary dict = dictStream.findAny().orElse(alternative);
			names.add(dict.getName());
			p.setName(dict.getName());
			
			List<Element> elemsList = new ArrayList<>();
			for (int j = 0; j < card; j++) {
				Element e = new CtWedgeFactoryImpl().createElement();
				e.setName(dict.values.get(j));
				elemsList.add(e);
			}
			((EnumerativeImpl)p).eSet(CtWedgePackage.ENUMERATIVE__ELEMENTS, elemsList);
		}
		m.addParameter(p);
	}

	/**
	 * Adds to the model m a new integer parameter, either by giving a random name
	 * or one chosen from the dictionary
	 * 
	 * @param m     the model
	 * @param card  the cardinality
	 * @param from  the lower bound
	 * @param names the list of already used names
	 * @param i     the index
	 */
	public static void addIntegerParameter(Model m, int card, int from, ArrayList<String> names, int i) {
		if (m.getGeneratorConfiguration().DICTIONARY == null) {
			Range p = new CtWedgeFactoryImpl().createRange();
			p.setBegin(from);
			p.setEnd(from + card - 1);
			p.setName("Par" + i);
			m.addParameter(p);
		} else {
			Stream<Dictionary> dictStream = m.getGeneratorConfiguration().DICTIONARY.stream()
					.filter(x -> x.getType().equalsIgnoreCase("Integer") && !names.contains(x.getName())
							&& x.getLowerBound() == from && x.getUpperBound() == from + card - 1);

			// If no available parameter is found, this is the alternative
			Dictionary alternative = new Dictionary("Par" + i, from, from + card - 1);
			Dictionary dict = dictStream.findAny().orElse(alternative);
			names.add(dict.getName());
			Range p = new CtWedgeFactoryImpl().createRange();
			p.setBegin(dict.getLowerBound());
			p.setEnd(dict.getUpperBound());
			p.setName(dict.getName());
			m.addParameter(p);
		}
	}
}
