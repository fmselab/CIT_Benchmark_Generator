package benchmark.generator.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
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
import generators.GeneratorConfiguration;
import generators.Track;
import main.BenchmarkGeneratorCLI;
import models.Model;
import models.ModelList;

public class GenerateHandler implements ActionListener {

	private BenchmarkGenerator parentFrame;
	private HashMap<String, Integer> componentsMap;
	private BenchmarkGeneratorCLI cli;

	private static final Logger LOGGER = LogManager.getLogger(BenchmarkGenerator.class);

	/**
	 * Creates a new GenerateHandler
	 * 
	 * @param frame the frame to be handled
	 */
	public GenerateHandler(BenchmarkGenerator frame) {
		cli = new BenchmarkGeneratorCLI();
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
		Track t = Track.valueOf(selectedText);

		// Clear the previously generated models and set the configuration
		parentFrame.getModelList().clearModels();
		setConfiguration(t);

		// First generate all IPMs
		try {
			cli.generateIPMs();

			// Then add the IPMs to the list
			parentFrame.getModelList().addModel(cli.getModelsList());
		} catch (IOException | InterruptedException | InvalidConfigurationException | SolverException e1) {
			e1.printStackTrace();
		}

		// Show the benchmarks generated
		showModels(parentFrame.getModelList());
	}

	/**
	 * Set the configuration in the GeneratorConfiguration based on the value
	 * inserted by the user in the GUI
	 * 
	 * @param t the track
	 */
	private void setConfiguration(Track t) {
		GeneratorConfiguration.N_BENCHMARKS = Integer.parseInt(
				((JTextField) parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtNumBenchmarks")))
						.getText());
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
		GeneratorConfiguration.MIN_CONSTRAINTS_COMPLEXITY = Integer.parseInt(((JTextField) parentFrame
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
		GeneratorConfiguration.FORBIDDEN_TUPLES = ((JCheckBox) (parentFrame.getPanelConfigurations()
				.getComponent(componentsMap.get("chkForbiddenTuples")))).isSelected();
		GeneratorConfiguration.CNF = ((JCheckBox) (parentFrame.getPanelConfigurations()
				.getComponent(componentsMap.get("chkCNF")))).isSelected();
		GeneratorConfiguration.TRACK = t;
		GeneratorConfiguration.CHECK_TEST_RATIO = parentFrame.isRatioTest();
		GeneratorConfiguration.CHECK_TUPLE_RATIO = parentFrame.isRatioTuple();
		GeneratorConfiguration.ACTS = parentFrame.isACTS();
		GeneratorConfiguration.CTWEDGE = parentFrame.isCTWedge();
		GeneratorConfiguration.PICT = parentFrame.isPICT();
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

}
