package generators;

/**
 * Randomly generates integer numbers. It is used for randomly choosing which
 * parameter or value has to be selected while randomly generating test
 * benchmarks
 * 
 * @author andrea
 *
 */
public class Randomizer {

	/**
	 * Generate the random number
	 * 
	 * @param min the lower bound
	 * @param max the upper bound
	 * @return the random number
	 */
	public static int generate(int min, int max) {
		return min + (int) (Math.random() * ((max - min) + 1));
	}
}
