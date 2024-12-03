package generators;

import java.util.ArrayList;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import models.Model;
import util.Dictionary;

/**
 * Configuration parameters for generators
 * 
 * @author andrea
 *
 */
public class GeneratorConfiguration {

	public Boolean CHECK_SOLVABLE = true;
	public int N_BENCHMARKS = 1;
	public int N_PARAMS_MAX = 500;
	public int N_PARAMS_MIN = 1;
	public int LOWER_BOUND_INT = -100;
	public int UPPER_BOUND_INT = 100;
	public int MIN_CARDINALITY = 2;
	public int MAX_CARDINALITY = 500;
	public int N_CONSTRAINTS_MAX = 100;
	public int N_CONSTRAINTS_MIN = 1;
	public int MAX_CONSTRAINTS_COMPLEXITY = 10;
	public int MIN_CONSTRAINTS_COMPLEXITY = 1;
	public boolean USE_CONSTRAINTS_BETWEEN_PARAMETERS = false;
	public double RATIO = 0.01;
	public double RATIO_TEST = 0.01;
	public double TIMEOUT = 6.0;
	public Track TRACK = Track.BOOLC;
	public double P = 0.10;
	public double EPSILON = 0.1;
	public boolean ACTS = false;
	public boolean CTWEDGE = false;
	public boolean PICT = false;
	public boolean CHECK_TUPLE_RATIO = false;
	public boolean CHECK_TEST_RATIO = false;
	public boolean ALWAYS_EXPORT = true;
	public int N_ATTEMPTS = 10;
	public boolean FORBIDDEN_TUPLES = false;
	public boolean CNF = false;
	public ArrayList<Dictionary> DICTIONARY = null;
	public boolean USE_SEARCH = false;
	public float PROBABILITY_PARADD = 0;
	public float PROBABILITY_PAREXT = 0;
	public float PROBABILITY_CNSTRADD = 0;
	public float PROBABILITY_CNSTRDEL = 0;
	public float PROBABILITY_CNSTRSUBST = 0;
	public float PROBABILITY_ANDTOOR = 0;
	public float PROBABILITY_ORTOAND = 0;
	public float PROBABILITY_IMPLTODBL = 0;
	public float PROBABILITY_DBLTOIMPL = 0;
	public float PROBABILITY_NOTADD = 0;
	public float PROBABILITY_NOTDEL = 0;
	public FitnessEvaluator<Model> FITNESS = null;

}
