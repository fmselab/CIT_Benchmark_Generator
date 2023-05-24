import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.junit.Test;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.api.SolverException;

import util.ModelConfigurationExtractor;

public class TestExternalValidation {

	static String BENCHMARKS_PATH = "/home/bombarda/Documents/Benchmarks/Jin2020";
	static String OUTPUTFILE = "/home/bombarda/Documents/Benchmarks/"
			+ new SimpleDateFormat("yyyyMMddhhmmss'.csv'").format(new Date());
	static String[] IGNORE_FILE_FOR_RATIO = { "MLinux", "MFreeBSD", "MEShop", "MEcos", "MFiasco", "MuClinux", "MToyBox", "MBusyBox"};

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

	private void analyzeFile(File x, BufferedWriter bw)
			throws IOException, InterruptedException, InvalidConfigurationException, SolverException {
		ModelConfigurationExtractor extractor = new ModelConfigurationExtractor(x.getAbsolutePath());
		System.out.println("Analyzing " + extractor.getModelName());
		double tupleValidityRatio = -1;
		double testValidityRatio = -1;
		if (Arrays.stream(IGNORE_FILE_FOR_RATIO).filter(f -> f.equals(extractor.getModelName())).count() == 0) {
			tupleValidityRatio = extractor.getTupleValidityRatio();
			testValidityRatio = extractor.getTestValidityRatio();
		}
		bw.append(BENCHMARKS_PATH + "," + extractor.getModelName() + "," + extractor.getModelType().toString() + ","
				+ extractor.getNumParams() + "," + extractor.getLowerBoundForInts() + ","
				+ extractor.getUpperBoundForInts() + "," + extractor.getMinimumCardinality() + ","
				+ extractor.getMaximumCardinality() + "," + extractor.getNumConstraints() + ","
				+ extractor.getMinConstraintComplexity() + "," + extractor.getMaxConstraintComplexity() + ","
				+ tupleValidityRatio + "," + testValidityRatio + ",\n");
		bw.flush();
	}

}
