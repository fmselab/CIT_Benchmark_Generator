import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ctwedge.ctWedge.CitModel;
import ctwedge.generator.acts.ACTSTranslator;
import ctwedge.generator.util.Utility;
import ctwedge.util.ext.NotConvertableModel;
import generators.Category;
import generators.Generator;
import generators.GeneratorConfiguration;
import generators.WithConstraintGenerator;
import models.Model;

public class TestGenModel {
	
	public void listFiles(File folder, List<File> fileList) {
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()	&& (listOfFiles[i].getName().endsWith(".citw") || listOfFiles[i].getName().endsWith(".ctw"))) {
				fileList.add(listOfFiles[i]);
			} else if (listOfFiles[i].isDirectory()) {
				listFiles(listOfFiles[i], fileList);
			}
		}
	}
	
	@Test
	public void testName() throws Exception {
		Generator g = new WithConstraintGenerator();
		
		// Using k in the range [2, 20]
		GeneratorConfiguration.N_PARAMS_MAX = 3;
		GeneratorConfiguration.N_PARAMS_MIN = 2;
				
		
		Model model = g.generate(Category.ONLY_BOOLEAN);
		model.setName("temp");
		// parso con ctwedge
		CitModel citmodel = Utility.loadModel(model.toString());
		//
		int size = citmodel.getParameters().size();
		assertTrue(2 <= size && size <=3);
		// constraint

	}
	
	@Test
	public void testACTSTranslator() {
		String path = "/Users/andrea/Downloads/CTWedge/";
		List<File> fileList = new ArrayList<>();
		listFiles(new File(path), fileList);
		for (File file : fileList) {
			try {
				System.out.println("Processing file " + file.getPath());
				CitModel ctwedgeModel = Utility.loadModelFromPath(file.getPath());
				ACTSTranslator translator = new ACTSTranslator();
				translator.convertModel(ctwedgeModel, true, 2, "./examples/");
			} catch (NotConvertableModel e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();			
			}
		}
	}
	
	@Test
	public void testACTSTranslatorAdditionalModels() {
		String path = "./examples/";
		List<File> fileList = new ArrayList<>();
		listFiles(new File(path), fileList);
		for (File file : fileList) {
			try {
				if (file.getName().contains("ADD")) {
					System.out.println("Processing file " + file.getPath());
					CitModel ctwedgeModel = Utility.loadModelFromPath(file.getPath());
					ACTSTranslator translator = new ACTSTranslator();
					translator.convertModel(ctwedgeModel, true, 2, "./examples/");
				}
			} catch (NotConvertableModel e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();			
			}
		}
	}
	
	@Test
	public void testModelACTS() throws Exception {
		String path = "./examples/ADD_MCAC_0.ctw";
		ACTSTranslator translator = new ACTSTranslator();
		assert (translator.getTestSuite(Utility.loadModelFromPath(path), 2, 
				false).getTests().size() > 0);
	}

}
