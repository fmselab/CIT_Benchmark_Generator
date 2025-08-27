package util.genetics;

import java.io.IOException;
import java.util.List;

import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.api.SolverException;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;

import generators.GeneratorConfiguration;
import generators.Track;
import models.Model;
import util.genetics.problems.ModelProblem;
import util.genetics.problems.ModelTestRatioProblem;
import util.genetics.solution.ModelSolution;

public final class SBModelRatioGenerator {

	@SuppressWarnings("unchecked")
	public static Model evolveModel(GeneratorConfiguration config, ModelProblem problem) {
		@SuppressWarnings("unused")
		AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(problem.getAlgorithm()).execute();
		List<ModelSolution> population = (List<ModelSolution>) problem.getAlgorithm().result();
		return population.get(0).getModel();

	}

	public static void main(String[] args) throws InterruptedException, InvalidConfigurationException, IOException, SolverException {
		GeneratorConfiguration config = new GeneratorConfiguration();
		config.CHECK_TEST_RATIO = true;
		config.CHECK_TUPLE_RATIO = true;
		config.N_PARAMS_MIN = 2;
		config.RATIO = 0.20;
		config.RATIO_TEST = 0.20;
		config.P = 0.90;
		config.N_PARAMS_MAX = 30;
		config.TRACK = Track.BOOLC;
		ModelProblem problem = new ModelTestRatioProblem(config.RATIO, config);
		Model evolvedModel = evolveModel(config, problem);
		System.out.println(evolvedModel);
		System.out.println("Test validity: " + evolvedModel.getTestValidityRatio());
		System.out.println("Tuple validity: " + evolvedModel.getTupleValidityRatio());
		System.out.println(evolvedModel.isSolvable());
	}
}