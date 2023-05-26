package generators;

/**
 * Configuration parameters for generators
 * 
 * @author andrea
 *
 */
public class GeneratorConfiguration {

	public static int N_BENCHMARKS = 1;
	public static int N_PARAMS_MAX = 500;
	public static int N_PARAMS_MIN = 1;
	public static int LOWER_BOUND_INT = -100;
	public static int UPPER_BOUND_INT = 100;
	public static int MIN_CARDINALITY = 2;
	public static int MAX_CARDINALITY = 500;
	public static int N_CONSTRAINTS_MAX = 100;
	public static int N_CONSTRAINTS_MIN = 1;
	public static int MAX_CONSTRAINTS_COMPLEXITY = 10;
	public static int MIN_CONSTRAINTS_COMPLEXITY = 1;
	public static boolean USE_CONSTRAINTS_BETWEEN_PARAMETERS = false;
	public static double RATIO = 0.01;
	public static double RATIO_TEST = 0.01;
	public static double TIMEOUT = 6.0;
	public static Track TRACK = Track.BOOLC;
	public static int N = 1000;
	public static double EPSILON = 0.1;
	public static boolean ACTS = false;
	public static boolean CTWEDGE = false;
	public static boolean PICT = false;
	public static boolean CHECK_TUPLE_RATIO = false;
	public static boolean CHECK_TEST_RATIO = false;
	public static boolean ALWAYS_EXPORT = true;
	public static int N_ATTEMPTS = 10;
	public static boolean FORBIDDEN_TUPLES = false;
	
}
