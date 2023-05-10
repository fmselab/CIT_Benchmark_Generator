import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ctwedge.ctWedge.CitModel;
import ctwedge.util.NotConvertableModel;
import ctwedge.util.ext.Utility;

public class TestComplexity {

	public static String PATH = "E:\\GitHub\\CIT_Benchmark_Generator\\Benchmarks_FollowUp_CITCompetition_2022\\CTWedge";
	public static String OUTPUT_PATH = "E:\\GitHub\\CIT_Benchmark_Generator\\Benchmarks_FollowUp_CITCompetition_2022\\ModelComplexity.csv";
	
	@Test
	public void evaluateComplexity() throws Exception {
		List<File> fileList = new ArrayList<>();
		new TestGenModel().listFiles(new File(PATH), fileList);
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(OUTPUT_PATH)));
		for (File file : fileList) {
			try {
				System.out.println("Processing file " + file.getPath());
				CitModel ctwedgeModel = Utility.loadModelFromPath(file.getPath());
				BufferedReader reader = new BufferedReader(new FileReader(file));
				int nParams = 0;
				for (Object line : reader.lines().toArray()) {
					String l = (String)line;
					if (l.startsWith("Par") && !l.equals("Parameters:")) {
						nParams++;
					}
				}
				reader.close();
				bw.append(ctwedgeModel.getName() + ";" + nParams + ";\n");
			} catch (NotConvertableModel e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();			
			}			
		}
		bw.close();
	}
}
