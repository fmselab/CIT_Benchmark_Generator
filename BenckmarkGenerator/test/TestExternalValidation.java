import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.api.SolverException;

import util.ModelConfigurationExtractor;

public class TestExternalValidation {

	static String BENCHMARKS_PATH = "C:\\Users\\Andrea_PC\\Desktop\\Benchmarks\\CTCompetition2022Repo";
	static String OUTPUTFILE = "C:\\Users\\Andrea_PC\\Desktop\\Benchmarks\\"
			+ new SimpleDateFormat("yyyyMMddhhmmss'.csv'").format(new Date());

	@Test
	public void test() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(OUTPUTFILE)));
		bw.append(
				"Path,ModelName,Type,NumParams,LowerInt,UpperInt,MinCardinality,MaxCardinality,NConstraints,MinComplexity,MaxComplexity,RatioTuple,RatioTest,\n");
		Files.walk(Paths.get((BENCHMARKS_PATH))).map(Path::toFile).filter(x -> x.getName().endsWith(".ctw"))
				.forEach(x -> {
					try {
						analyzeFile(x, bw);
					} catch (IOException | InterruptedException | InvalidConfigurationException | SolverException e) {
						e.printStackTrace();
					}
				});
		bw.close();
	}

	private void analyzeFile(File x, BufferedWriter bw) throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		ModelConfigurationExtractor extractor = new ModelConfigurationExtractor(x.getAbsolutePath());
		System.out.println("Analyzing " + extractor.getModelName());
		bw.append(BENCHMARKS_PATH + "," + extractor.getModelName() + "," + extractor.getModelType().toString() + ","
				+ extractor.getNumParams() + "," + extractor.getLowerBoundForInts() + ","
				+ extractor.getUpperBoundForInts() + "," + extractor.getMinimumCardinality() + ","
				+ extractor.getMaximumCardinality() + "," + extractor.getNumConstraints() + ","
				+ extractor.getMinConstraintComplexity() + "," + extractor.getMaxConstraintComplexity()
				+ "," + extractor.getTupleValidityRatio() + "," + extractor.getTestValidityRatio() + ",\n");
		bw.flush();
	}

}
