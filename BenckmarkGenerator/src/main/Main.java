package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ctwedge.ctWedge.CitModel;
import ctwedge.generator.acts.ACTSTranslator;
import ctwedge.generator.medici.MediciCITGenerator;
import ctwedge.generator.util.Utility;
import ctwedge.util.TestSuite;
import generators.Category;
import generators.Generator;
import generators.GeneratorConfiguration;
import generators.WithConstraintGenerator;
import generators.WithoutConstraintGenerator;
import generators.WithoutConstraintGeneratorSameCardinality;
import models.Model;

public class Main {
	
	private static final String BENCHMARK_FOLDER = "./examples/CTComp/";
	static int N_MODELS = 5;
	static String PREFIX = "UNIFORM_BOOLEAN_";
	
	public static void generateUniformBooleanNoConstraints(boolean verify) throws IOException {
		PREFIX = "UNIFORM_BOOLEAN_";
		
		// Define a new generator without considering the constraints
		Generator g = new WithoutConstraintGenerator();
		
		// Using k in the range [2, 20]
		GeneratorConfiguration.N_PARAMS_MAX = 20;
		GeneratorConfiguration.N_PARAMS_MIN = 2;
		
		// Generate models with only boolean params
		for (int i = 0; i<N_MODELS; i++) {
			Model m1 = g.generate(Category.ONLY_BOOLEAN);
			m1.setName(PREFIX + i);
			// Store the results in a new CTW file
			FileWriter fo = new FileWriter(new File(BENCHMARK_FOLDER + PREFIX + i + ".ctw"));
			fo.write(m1.toString());
			fo.close();
			// Convert the file in ACTS
			try{
				CitModel ctwedgeModel = Utility.loadModel(m1.toString());
				ACTSTranslator translator = new ACTSTranslator();
				translator.convertModel(ctwedgeModel, true, 2, BENCHMARK_FOLDER);
				
				// Check if the test suite is empty
				if (verify) {
					TestSuite ts = null;
					ExecutorService executor = Executors.newCachedThreadPool();
					Callable<Object> task = new Callable<Object>() {
					   public Object call() throws Exception {
					      return Utility.getTestSuite(m1.toString(), new ACTSTranslator(), 2, false, null);
					   }
					};
					Future<Object> future = executor.submit(task);
					try {
					   ts = (TestSuite) future.get(6, TimeUnit.MINUTES); 
					} catch (TimeoutException ex) {
					   ts = null;
					} catch (InterruptedException e) {
					   ts = null;
					} catch (ExecutionException e) {
					   ts = null;
					} finally {
					   future.cancel(true); // may or may not desire this
					}
					
					if (ts == null || (ts != null && ts.getTests().size() <= 0)) {
						i--;
						continue;
					}
				}
			}catch (Exception e) {
				System.out.println(m1.toString());
				i--;
				continue;
			}
		}		
	}
	
	public static void generateUniformNoConstraints(boolean verify) throws IOException {
		PREFIX = "UNIFORM_ALL_";
		
		// Define a new generator without considering the constraints
		Generator g = new WithoutConstraintGeneratorSameCardinality();
		
		// Using k in the range [2, 20]
		GeneratorConfiguration.N_PARAMS_MAX = 20;
		GeneratorConfiguration.N_PARAMS_MIN = 2;
		
		// Using v in the range [2, 20]
		GeneratorConfiguration.MIN_CARDINALITY=2;
		GeneratorConfiguration.MAX_CARDINALITY=20;
		
		// Generate models with also enum parameters
		for (int i = 0; i<N_MODELS; i++) {
			Model m1 = g.generate(Category.ALSO_ENUMS);
			m1.setName(PREFIX + i);
			// Store the results in a new CTW file
			FileWriter fo = new FileWriter(new File(BENCHMARK_FOLDER + PREFIX + i + ".ctw"));
			fo.write(m1.toString());
			fo.close();
			// Convert the file in ACTS
			try{
				CitModel ctwedgeModel = Utility.loadModel(m1.toString());
				ACTSTranslator translator = new ACTSTranslator();
				translator.convertModel(ctwedgeModel, true, 2, BENCHMARK_FOLDER);
				
				// Check if the test suite is empty
				if (verify) {
					TestSuite ts = null;
					ExecutorService executor = Executors.newCachedThreadPool();
					Callable<Object> task = new Callable<Object>() {
					   public Object call() throws Exception {
					      return Utility.getTestSuite(m1.toString(), new ACTSTranslator(), 2, false, null);
					   }
					};
					Future<Object> future = executor.submit(task);
					try {
					   ts = (TestSuite) future.get(6, TimeUnit.MINUTES); 
					} catch (TimeoutException ex) {
					   ts = null;
					} catch (InterruptedException e) {
					   ts = null;
					} catch (ExecutionException e) {
					   ts = null;
					} finally {
					   future.cancel(true); // may or may not desire this
					}
					
					if (ts == null || (ts != null && ts.getTests().size() <= 0)) {
						i--;
						continue;
					}
				}
			}catch (Exception e) {
				System.out.println(m1.toString());
				i--;
				continue;
			}
		}		
	}
	
	public static void generateMCANoConstraints(boolean verify) throws IOException {
		PREFIX = "MCA_";
		
		// Define a new generator without considering the constraints
		Generator g = new WithoutConstraintGenerator();
		
		// Using k in the range [2, 20]
		GeneratorConfiguration.N_PARAMS_MAX = 20;
		GeneratorConfiguration.N_PARAMS_MIN = 2;
		
		// Using v in the range [2, 20]
		GeneratorConfiguration.MIN_CARDINALITY=2;
		GeneratorConfiguration.MAX_CARDINALITY=20;
		
		// Generate models with also enum parameters
		for (int i = 0; i<N_MODELS; i++) {
			Model m1 = g.generate(Category.ALSO_ENUMS);
			m1.setName(PREFIX + i);
			// Store the results in a new CTW file
			FileWriter fo = new FileWriter(new File(BENCHMARK_FOLDER + PREFIX + i + ".ctw"));
			fo.write(m1.toString());
			fo.close();
			// Convert the file in ACTS
			try{
				CitModel ctwedgeModel = Utility.loadModel(m1.toString());
				ACTSTranslator translator = new ACTSTranslator();
				translator.convertModel(ctwedgeModel, true, 2, BENCHMARK_FOLDER);
				
				// Check if the test suite is empty
				if (verify) {
					TestSuite ts = null;
					ExecutorService executor = Executors.newCachedThreadPool();
					Callable<Object> task = new Callable<Object>() {
					   public Object call() throws Exception {
					      return Utility.getTestSuite(m1.toString(), new ACTSTranslator(), 2, false, null);
					   }
					};
					Future<Object> future = executor.submit(task);
					try {
					   ts = (TestSuite) future.get(6, TimeUnit.MINUTES); 
					} catch (TimeoutException ex) {
					   ts = null;
					} catch (InterruptedException e) {
					   ts = null;
					} catch (ExecutionException e) {
					   ts = null;
					} finally {
					   future.cancel(true); // may or may not desire this
					}
					
					if (ts == null || (ts != null && ts.getTests().size() <= 0)) {
						i--;
						continue;
					}
				}
			}catch (Exception e) {
				System.out.println(m1.toString());
				i--;
				continue;
			}
		}		
	}
	
	public static void generateBoolConstraints(boolean verify) throws IOException {
		PREFIX = "BOOLC_";
		
		// Define a new generator without considering the constraints
		Generator g = new WithConstraintGenerator();
		
		// Using k in the range [2, 20]
		GeneratorConfiguration.N_PARAMS_MAX = 20;
		GeneratorConfiguration.N_PARAMS_MIN = 2;
		
		// Using c in the range [1, 100]
		GeneratorConfiguration.N_CONSTRAINTS_MIN = 1;
		GeneratorConfiguration.N_CONSTRAINTS_MAX = 100;
		
		// Using d in the range [1, 20]
		GeneratorConfiguration.MIN_CONSTRAINTS_COMPLEXITY = 1;
		GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY = 20;
		
		// Generate models with only boolean parameters
		for (int i = 0; i<N_MODELS; i++) {
			Model m1 = g.generate(Category.ONLY_BOOLEAN);
			m1.setName(PREFIX + i);
			// Store the results in a new CTW file
			FileWriter fo = new FileWriter(new File(BENCHMARK_FOLDER + PREFIX + i + ".ctw"));
			fo.write(m1.toString());
			fo.close();
			// Convert the file in ACTS
			try{
				CitModel ctwedgeModel = Utility.loadModel(m1.toString());
				ACTSTranslator translator = new ACTSTranslator();
				translator.convertModel(ctwedgeModel, true, 2, BENCHMARK_FOLDER);
				
				// Check if the test suite is empty
				if (verify) {
					TestSuite ts = null;
					ExecutorService executor = Executors.newCachedThreadPool();
					Callable<Object> task = new Callable<Object>() {
					   public Object call() throws Exception {
					      return Utility.getTestSuite(m1.toString(), new ACTSTranslator(), 2, false, null);
					   }
					};
					Future<Object> future = executor.submit(task);
					try {
					   ts = (TestSuite) future.get(6, TimeUnit.MINUTES); 
					} catch (TimeoutException ex) {
					   ts = null;
					} catch (InterruptedException e) {
					   ts = null;
					} catch (ExecutionException e) {
					   ts = null;
					} finally {
					   future.cancel(true); // may or may not desire this
					}
					
					if (ts == null || (ts != null && ts.getTests().size() <= 0)) {
						i--;
						continue;
					}
				}
			}catch (Exception e) {
				System.out.println(m1.toString());
				i--;
				continue;
			}
		}		
	}
	
	public static void generateMCAConstraints(boolean verify) throws IOException {
		PREFIX = "MCAC_";
		
		// Define a new generator without considering the constraints
		Generator g = new WithConstraintGenerator();
		
		// Using k in the range [2, 20]
		GeneratorConfiguration.N_PARAMS_MAX = 20;
		GeneratorConfiguration.N_PARAMS_MIN = 2;
		
		// Using c in the range [1, 100]
		GeneratorConfiguration.N_CONSTRAINTS_MIN = 1;
		GeneratorConfiguration.N_CONSTRAINTS_MAX = 100;
		
		// Using d in the range [1, 20]
		GeneratorConfiguration.MIN_CONSTRAINTS_COMPLEXITY = 1;
		GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY = 20;
		
		// Using v in the range [2, 20]
		GeneratorConfiguration.MIN_CARDINALITY=2;
		GeneratorConfiguration.MAX_CARDINALITY=20;
		
		// Generate models with only boolean parameters
		for (int i = 0; i<N_MODELS; i++) {
			Model m1 = g.generate(Category.ALSO_ENUMS);
			m1.setName(PREFIX + i);
			// Store the results in a new CTW file
			FileWriter fo = new FileWriter(new File(BENCHMARK_FOLDER + PREFIX + i + ".ctw"));
			fo.write(m1.toString());
			fo.close();
			// Convert the file in ACTS
			try{
				CitModel ctwedgeModel = Utility.loadModel(m1.toString());
				ACTSTranslator translator = new ACTSTranslator();
				translator.convertModel(ctwedgeModel, true, 2, BENCHMARK_FOLDER);
				
				// Check if the test suite is empty
				if (verify) {
					TestSuite ts = null;
					ExecutorService executor = Executors.newCachedThreadPool();
					Callable<Object> task = new Callable<Object>() {
					   public Object call() throws Exception {
					      return Utility.getTestSuite(m1.toString(), new ACTSTranslator(), 2, false, null);
					   }
					};
					Future<Object> future = executor.submit(task);
					try {
					   ts = (TestSuite) future.get(6, TimeUnit.MINUTES); 
					} catch (TimeoutException ex) {
					   ts = null;
					} catch (InterruptedException e) {
					   ts = null;
					} catch (ExecutionException e) {
					   ts = null;
					} finally {
					   future.cancel(true); // may or may not desire this
					}
					
					if (ts == null || (ts != null && ts.getTests().size() <= 0)) {
						i--;
						continue;
					}
				}
			}catch (Exception e) {
				System.out.println(m1.toString());
				i--;
				continue;
			}
		}		
	}
	
	public static void generateNUMConstraints(boolean verify) throws IOException {
		PREFIX = "NUMC_";
		
		// Define a new generator without considering the constraints
		Generator g = new WithConstraintGenerator();
		
		// Using k in the range [2, 20]
		GeneratorConfiguration.N_PARAMS_MAX = 20;
		GeneratorConfiguration.N_PARAMS_MIN = 2;
		
		// Using c in the range [1, 100]
		GeneratorConfiguration.N_CONSTRAINTS_MIN = 1;
		GeneratorConfiguration.N_CONSTRAINTS_MAX = 100;
		
		// Using d in the range [1, 20]
		GeneratorConfiguration.MIN_CONSTRAINTS_COMPLEXITY = 1;
		GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY = 20;
		
		// Using v in the range [2, 20]
		GeneratorConfiguration.MIN_CARDINALITY=2;
		GeneratorConfiguration.MAX_CARDINALITY=20;
		
		// Generate models with only boolean parameters
		for (int i = 0; i<N_MODELS; i++) {
			Model m1 = g.generate(Category.CONSTRAINTS_WITH_RELATIONAL);
			m1.setName(PREFIX + i);
			// Store the results in a new CTW file
			FileWriter fo = new FileWriter(new File(BENCHMARK_FOLDER + PREFIX + i + ".ctw"));
			fo.write(m1.toString());
			fo.close();
			// Convert the file in ACTS
			try{
				CitModel ctwedgeModel = Utility.loadModel(m1.toString());
				ACTSTranslator translator = new ACTSTranslator();
				translator.convertModel(ctwedgeModel, true, 2, BENCHMARK_FOLDER);
				
				// Check if the test suite is empty
				if (verify) {
					TestSuite ts = null;
					ExecutorService executor = Executors.newCachedThreadPool();
					Callable<Object> task = new Callable<Object>() {
					   public Object call() throws Exception {
					      return Utility.getTestSuite(m1.toString(), new ACTSTranslator(), 2, false, null);
					   }
					};
					Future<Object> future = executor.submit(task);
					try {
					   ts = (TestSuite) future.get(6, TimeUnit.MINUTES); 
					} catch (TimeoutException ex) {
					   ts = null;
					} catch (InterruptedException e) {
					   ts = null;
					} catch (ExecutionException e) {
					   ts = null;
					} finally {
					   future.cancel(true); // may or may not desire this
					}
					
					if (ts == null || (ts != null && ts.getTests().size() <= 0)) {
						i--;
						continue;
					} else {
						System.out.println(ts.getTests().size());
					}
				}
			}catch (Exception e) {
				System.out.println(m1.toString());
				i--;
				continue;
			}
		}		
	}
	
	public static void getenerateHighlyConstrained(Boolean verify) throws IOException {
		PREFIX = "HIGHLY_CONSTRAINED_";
		
		// Define a new generator without considering the constraints
		Generator g = new WithConstraintGenerator();
		
		// Using k in the range [2, 20]
		GeneratorConfiguration.N_PARAMS_MAX = 20;
		GeneratorConfiguration.N_PARAMS_MIN = 2;
		
		// Using c in the range [1, 100]
		GeneratorConfiguration.N_CONSTRAINTS_MIN = 1;
		GeneratorConfiguration.N_CONSTRAINTS_MAX = 100;
		
		// Using d in the range [1, 20]
		GeneratorConfiguration.MIN_CONSTRAINTS_COMPLEXITY = 1;
		GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY = 20;
		
		// Using v in the range [2, 20]
		GeneratorConfiguration.MIN_CARDINALITY=2;
		GeneratorConfiguration.MAX_CARDINALITY=20;
		
		// Generate models with only boolean parameters
		for (int i = 0; i<N_MODELS; i++) {
			int sizeWoConstraints = 0, sizeWConstraints = 0;
			float ratio = 0;
			Model m1 = g.generate(Category.ALSO_ENUMS);
			m1.setName(PREFIX + i);
			// Store the results in a new CTW file
			FileWriter fo = new FileWriter(new File(BENCHMARK_FOLDER + PREFIX + i + ".ctw"));
			fo.write(m1.toString());
			fo.close();
			// Check the ratio using MEDICI
			CitModel loadModel = Utility.loadModel(m1.toString());
			// Convert to MEDICI
			File model = new File("model.txt");
			FileWriter wf = new FileWriter(model);
			MediciCITGenerator gen = new MediciCITGenerator();
			String translateModel = gen.translateModel(loadModel,false);
			wf.write(translateModel);
			System.out.println(translateModel);
			wf.close();
			// Call medici
			List<String> command = new ArrayList<String>();
			command.add("F:/Dati-Andrea/GitHub/ctwedge/ctwedge/ctwedge.parent/ctwedge.generators/ctwedge.generator.medici/medici.exe");
			// Model
			command.add("--m");
			command.add("model.txt");
			// Do not generate
			command.add("--donotgenerate");
			System.out.println(command);
			// Run
			ProcessBuilder pc = new ProcessBuilder(command);
			pc.command(command);
			pc.redirectError();
			Process p = pc.start();
			try {
				BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				while ((line = bri.readLine()) != null) {
					System.out.println(line);
					// save to file
					if (line.contains("Cardinalita di partenza"))
						sizeWoConstraints = new BigDecimal((line.split(" ")[3])).intValue();
					if (line.contains("Cardinalita finale"))
						sizeWConstraints = new BigDecimal((line.split(" ")[2])).intValue();
				}
				bri.close();
				p.waitFor();
				System.out.println("command finished ");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// Compute ratio
			ratio = (float)sizeWConstraints/sizeWoConstraints;
			
			// If ratio is lower than the desired one, repeat the generation
			if (ratio > GeneratorConfiguration.RATIO) {
				i--;
				continue;
			}
			
			// Else, the ratio is Ok, then check the feasibility		
			CitModel ctwedgeModel = Utility.loadModel(m1.toString());
			ACTSTranslator translator = new ACTSTranslator();
			translator.convertModel(ctwedgeModel, true, 2, BENCHMARK_FOLDER);
			
			// Check if the test suite is empty
			if (verify) {
				TestSuite ts = null;
				ExecutorService executor = Executors.newCachedThreadPool();
				Callable<Object> task = new Callable<Object>() {
				   public Object call() throws Exception {
				      return Utility.getTestSuite(m1.toString(), new ACTSTranslator(), 2, false, null);
				   }
				};
				Future<Object> future = executor.submit(task);
				try {
				   ts = (TestSuite) future.get(6, TimeUnit.MINUTES); 
				} catch (TimeoutException ex) {
				   ts = null;
				} catch (InterruptedException e) {
				   ts = null;
				} catch (ExecutionException e) {
				   ts = null;
				} finally {
				   future.cancel(true); // may or may not desire this
				}
				
				if (ts == null || (ts != null && ts.getTests().size() <= 0)) {
					i--;
					continue;
				} else {
					System.out.println(ts.getTests().size());
				}
			}
		}		
	}
	 
	public static void main(String[] args) throws IOException {
//		generateUniformBooleanNoConstraints(false);
//		generateUniformNoConstraints(false);
//		generateMCANoConstraints(false);
//		generateBoolConstraints(true);
//		generateMCAConstraints(true);
//		generateNUMConstraints(true);
		getenerateHighlyConstrained(true);
	}
}
