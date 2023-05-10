package benchmark.generator.handlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import benchmark.generator.gui.BenchmarkGenerator;
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
			// TODO: Set the timeout and check the feasibility of the benchmark
			for (int i = 0; i < nModels; i++) {
				Model m1 = gWC.generate(Category.ONLY_BOOLEAN);
				m1.setName(BenchmarkGenerator.BOOLC + "_" + i);
				parentFrame.getModelList().addModel(m1);
			}
			break;
		case BenchmarkGenerator.CNF:
			// TODO: Set the timeout and check the feasibility of the benchmark
			for (int i = 0; i < nModels; i++) {
				Model m1 = gCNF.generate(Category.ALSO_ENUMS);
				m1.setName(BenchmarkGenerator.CNF + "_" + i);
				parentFrame.getModelList().addModel(m1);
			}
			break;
		case BenchmarkGenerator.HIGHLY_CONSTRAINED:
			// TODO
			break;
		case BenchmarkGenerator.MCA:
			for (int i = 0; i < nModels; i++) {
				Model m1 = gWOC.generate(Category.ALSO_ENUMS);
				m1.setName(BenchmarkGenerator.MCA + "_" + i);
				parentFrame.getModelList().addModel(m1);
			}
			break;
		case BenchmarkGenerator.MCAC:
			// TODO: Set the timeout and check the feasibility of the benchmark
			for (int i = 0; i < nModels; i++) {
				Model m1 = gWC.generate(Category.ALSO_ENUMS);
				m1.setName(BenchmarkGenerator.MCAC + "_" + i);
				parentFrame.getModelList().addModel(m1);
			}
			break;
		case BenchmarkGenerator.NUMC:
			// TODO: Set the timeout and check the feasibility of the benchmark
			for (int i = 0; i < nModels; i++) {
				Model m1 = gWC.generate(Category.CONSTRAINTS_WITH_RELATIONAL);
				m1.setName(BenchmarkGenerator.NUMC + "_" + i);
				parentFrame.getModelList().addModel(m1);
			}
			break;
		case BenchmarkGenerator.UNIFORM_ALL:
			for (int i = 0; i < nModels; i++) {
				Model m1 = gWSC.generate(Category.ALSO_ENUMS);
				m1.setName(BenchmarkGenerator.UNIFORM_ALL + "_" + i);
				parentFrame.getModelList().addModel(m1);
			}
			break;
		case BenchmarkGenerator.UNIFORM_BOOLEAN:
			for (int i = 0; i < nModels; i++) {
				Model m1 = gWOC.generate(Category.ONLY_BOOLEAN);
				m1.setName(BenchmarkGenerator.UNIFORM_BOOLEAN + "_" + i);
				parentFrame.getModelList().addModel(m1);
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
		GeneratorConfiguration.TIMEOUT = Double.parseDouble(
				((JTextField) parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtTimeout")))
						.getText());
		GeneratorConfiguration.RATIO = Double.parseDouble(
				((JTextField) parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtRatio")))
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
		for (int i = 0; i < model.getRowCount(); i++)
			model.removeRow(0);

		// Fill the table with the model names
		for (Model m : models) {
			model.addRow(new Object[] { m.getName() });
		}
	}

}
