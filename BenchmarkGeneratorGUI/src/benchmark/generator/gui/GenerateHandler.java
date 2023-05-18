package benchmark.generator.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.api.SolverException;

import ctwedge.util.validator.SMTConstraintTranslator;
import generators.Category;
import generators.Generator;
import generators.GeneratorConfiguration;
import generators.WithConstraintGenerator;
import generators.WithConstraintGeneratorCNF;
import generators.WithoutConstraintGenerator;
import generators.WithoutConstraintGeneratorSameCardinality;
import models.Model;
import models.ModelList;

public class GenerateHandler implements ActionListener {

	private BenchmarkGenerator parentFrame;
	private HashMap<String, Integer> componentsMap;
	
	private static final Logger LOGGER = LogManager.getLogger(BenchmarkGenerator.class);

	/**
	 * Generators
	 */
	static Generator gWOC = new WithoutConstraintGenerator();
	static Generator gWSC = new WithoutConstraintGeneratorSameCardinality();
	static Generator gWC = new WithConstraintGenerator();
	static Generator gCNF = new WithConstraintGeneratorCNF();

	/**
	 * Creates a new GenerateHandler
	 * 
	 * @param frame the frame to be handled
	 */
	public GenerateHandler(BenchmarkGenerator frame) {
		parentFrame = frame;
		componentsMap = frame.getConfigurationComponents();
		LOGGER.setLevel(Level.DEBUG);
		Logger.getLogger(SMTConstraintTranslator.class).setLevel(Level.OFF);
	}

	/**
	 * Generates the benchmarks when the listened button is clicked
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e) {
		// Extract the selected benchmark type from the combo box benchmarkType
		JComboBox<String> benchmarkType = (JComboBox<String>) Arrays
				.stream(parentFrame.getPanelConfigurations().getComponents())
				.filter(x -> x.getName() != null && x.getName().equals("benchmarkType")).findFirst().get();
		String selectedText = (String) benchmarkType.getSelectedItem();

		// Number of models to be generated
		int nModels = GeneratorConfiguration.N_PARAMS_MIN = Integer.parseInt(
				((JTextField) parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtNumBenchmarks")))
						.getText());

		// Clear the previously generated models and set the configuration
		parentFrame.getModelList().clearModels();
		setConfiguration();

		// Generate the benchmarks
		switch (selectedText) {
		case BenchmarkGenerator.BOOLC:
			for (int i = 0; i < nModels; i++) {
				Model m1 = null;
				try {
					m1 = generateWithGenerator(gWC, Category.ONLY_BOOLEAN, parentFrame.isRatioTuple(),
							parentFrame.isRatioTest());
					m1.setName(BenchmarkGenerator.BOOLC + "_" + i);
					parentFrame.getModelList().addModel(m1);
					LOGGER.debug("Added a new model: " + m1.getName());
				} catch (InvalidConfigurationException | SolverException | InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			break;
		case BenchmarkGenerator.CNF:
			for (int i = 0; i < nModels; i++) {
				Model m1 = null;
				try {
					m1 = generateWithGenerator(gCNF, Category.ALSO_ENUMS, parentFrame.isRatioTuple(),
							parentFrame.isRatioTest());
					m1.setName(BenchmarkGenerator.CNF + "_" + i);
					parentFrame.getModelList().addModel(m1);
					LOGGER.debug("Added a new model: " + m1.getName());
				} catch (InvalidConfigurationException | SolverException | InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			break;
		case BenchmarkGenerator.HIGHLY_CONSTRAINED:
			for (int i = 0; i < nModels; i++) {
				Model m1 = null;
				try {
					m1 = generateWithGenerator(gWC, Category.CONSTRAINTS_WITH_RELATIONAL, parentFrame.isRatioTuple(),
							parentFrame.isRatioTest());
					m1.setName(BenchmarkGenerator.HIGHLY_CONSTRAINED + "_" + i);
					parentFrame.getModelList().addModel(m1);
					LOGGER.debug("Added a new model: " + m1.getName());
				} catch (InvalidConfigurationException | SolverException | InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			break;
		case BenchmarkGenerator.MCA:
			for (int i = 0; i < nModels; i++) {
				Model m1 = gWOC.generate(Category.ALSO_ENUMS);
				m1.setName(BenchmarkGenerator.MCA + "_" + i);
				parentFrame.getModelList().addModel(m1);
				LOGGER.debug("Added a new model: " + m1.getName());
			}
			break;
		case BenchmarkGenerator.MCAC:
			for (int i = 0; i < nModels; i++) {
				try {
					Model m1 = generateWithGenerator(gWC, Category.ALSO_ENUMS, parentFrame.isRatioTuple(),
							parentFrame.isRatioTest());
					m1.setName(BenchmarkGenerator.MCAC + "_" + i);
					parentFrame.getModelList().addModel(m1);
					LOGGER.debug("Added a new model: " + m1.getName());
				} catch (InvalidConfigurationException | SolverException | InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			break;
		case BenchmarkGenerator.NUMC:
			for (int i = 0; i < nModels; i++) {
				try {
					Model m1 = generateWithGenerator(gWC, Category.CONSTRAINTS_WITH_RELATIONAL,
							parentFrame.isRatioTuple(), parentFrame.isRatioTest());
					m1.setName(BenchmarkGenerator.NUMC + "_" + i);
					parentFrame.getModelList().addModel(m1);
					LOGGER.debug("Added a new model: " + m1.getName());
				} catch (InvalidConfigurationException | SolverException | InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			break;
		case BenchmarkGenerator.UNIFORM_ALL:
			for (int i = 0; i < nModels; i++) {
				Model m1 = gWSC.generate(Category.ALSO_ENUMS);
				m1.setName(BenchmarkGenerator.UNIFORM_ALL + "_" + i);
				parentFrame.getModelList().addModel(m1);
				LOGGER.debug("Added a new model: " + m1.getName());
			}
			break;
		case BenchmarkGenerator.UNIFORM_BOOLEAN:
			for (int i = 0; i < nModels; i++) {
				Model m1 = gWOC.generate(Category.ONLY_BOOLEAN);
				m1.setName(BenchmarkGenerator.UNIFORM_BOOLEAN + "_" + i);
				parentFrame.getModelList().addModel(m1);
				LOGGER.debug("Added a new model: " + m1.getName());
			}
			break;
		default:
			break;
		}

		// Show the benchmarks generated
		showModels(parentFrame.getModelList());
	}

	/**
	 * Set the configuration in the GeneratorConfiguration based on the value
	 * inserted by the user in the GUI
	 */
	private void setConfiguration() {
		GeneratorConfiguration.N_PARAMS_MAX = Integer.parseInt(
				((JTextField) parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtNMaxParams")))
						.getText());
		GeneratorConfiguration.N_PARAMS_MIN = Integer.parseInt(
				((JTextField) parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtNMinParams")))
						.getText());
		GeneratorConfiguration.MIN_CARDINALITY = Integer.parseInt(
				((JTextField) parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtMinCardinality")))
						.getText());
		GeneratorConfiguration.MAX_CARDINALITY = Integer.parseInt(
				((JTextField) parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtMaxCardinality")))
						.getText());
		GeneratorConfiguration.LOWER_BOUND_INT = Integer.parseInt(
				((JTextField) parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtLowerBoundInt")))
						.getText());
		GeneratorConfiguration.UPPER_BOUND_INT = Integer.parseInt(
				((JTextField) parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtUpperBoundInt")))
						.getText());
		GeneratorConfiguration.N_CONSTRAINTS_MAX = Integer.parseInt(((JTextField) parentFrame.getPanelConfigurations()
				.getComponent(componentsMap.get("txtNMaxConstraints"))).getText());
		GeneratorConfiguration.N_CONSTRAINTS_MIN = Integer.parseInt(((JTextField) parentFrame.getPanelConfigurations()
				.getComponent(componentsMap.get("txtNMinConstraints"))).getText());
		GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY = Integer.parseInt(((JTextField) parentFrame
				.getPanelConfigurations().getComponent(componentsMap.get("txtMaxConstraintsComplexity"))).getText());
		GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY = Integer.parseInt(((JTextField) parentFrame
				.getPanelConfigurations().getComponent(componentsMap.get("txtMinConstraintComplexity"))).getText());
		GeneratorConfiguration.RATIO = Double.parseDouble(
				((JTextField) parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtRatio")))
						.getText());
		GeneratorConfiguration.N = Integer.parseInt(
				((JTextField) parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtTTest")))
						.getText());
		GeneratorConfiguration.EPSILON = Double.parseDouble(
				((JTextField) parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtEpsilonTest")))
						.getText());
		GeneratorConfiguration.RATIO_TEST = Double.parseDouble(
				((JTextField) parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtRatioTest")))
						.getText());
		GeneratorConfiguration.USE_CONSTRAINTS_BETWEEN_PARAMETERS = ((JCheckBox) (parentFrame.getPanelConfigurations()
				.getComponent(componentsMap.get("chkConstraintsBetweenParams")))).isSelected();
	}

	/**
	 * Show the models after having generated them
	 * 
	 * @param models the list of models being generated
	 */
	private void showModels(ModelList models) {
		DefaultTableModel model = parentFrame.getModel();
		// Empty the model
		parentFrame.emptyModel();

		// Fill the table with the model names
		for (Model m : models) {
			if (!m.isRatioExact() && parentFrame.isRatioTest())
				model.addRow(new Object[] { m.getName() + " (Prob.: "
						+ m.getProbability(GeneratorConfiguration.EPSILON, GeneratorConfiguration.RATIO_TEST) + ")" });
			else
				model.addRow(new Object[] { m.getName() });
		}
	}

	/**
	 * Generates a model with a given generator and category, and verifies its
	 * solvability
	 * 
	 * @param generator       the generator
	 * @param category        the category
	 * @param checkTupleRatio check the tuple validity ratio?
	 * @param checkTestRatio  check the test validity ratio?
	 * @return the model
	 * @throws SolverException
	 * @throws InvalidConfigurationException
	 * @throws InterruptedException
	 */
	private Model generateWithGenerator(Generator generator, Category category, boolean checkTupleRatio,
			boolean checkTestRatio) throws InvalidConfigurationException, SolverException, InterruptedException {
		boolean isSolvable = false;
		Model m = null;
		do {
			m = generator.generate(category);
			isSolvable = m.isSolvable();
			
			if (isSolvable) {
				LOGGER.debug("The generated model is solvable");
				// Check the tuple validity ratio
				if (checkTupleRatio) {
					LOGGER.debug("Checking TUPLE VALIDITY RATIO");
					try {
						double ratio = m.getTupleValidityRatio();
						LOGGER.debug("TUPLE VALIDITY RATIO " + Double.toString(ratio));
						if (ratio > GeneratorConfiguration.RATIO)
							isSolvable = false;
					} catch (InterruptedException e) {
					}
				}
	
				// Check the test validity ratio
				if (checkTestRatio) {
					LOGGER.debug("Checking TEST VALIDITY RATIO");
					double ratio = m.getTestValidityRatio();
					LOGGER.debug("TEST VALIDITY RATIO " + Double.toString(ratio));
					// If the ratio is exact, it means that it has been computed with the MDD, thus
					// we can use it to decide whether the model is correct or not. Otherwise, we
					// can only work with probabilities
					if (m.isRatioExact() && ratio > GeneratorConfiguration.RATIO_TEST)
						isSolvable = false;
				}
			}

		} while (!isSolvable);
		return m;
	}

}
