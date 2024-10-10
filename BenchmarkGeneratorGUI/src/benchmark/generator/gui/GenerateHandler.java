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

import ctwedge.util.smt.SMTConstraintTranslator;
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
		GeneratorConfiguration config = setConfiguration(t);

		// First generate all IPMs
		try {
			cli.generateIPMs(config);

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
	 * @return the generator configuration
	 */
	private GeneratorConfiguration setConfiguration(Track t) {
		GeneratorConfiguration config = new GeneratorConfiguration();
		config.N_BENCHMARKS = Integer.parseInt(
				((JTextField) parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtNumBenchmarks")))
						.getText());
		config.N_PARAMS_MAX = Integer.parseInt(
				((JTextField) parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtNMaxParams")))
						.getText());
		config.N_PARAMS_MIN = Integer.parseInt(
				((JTextField) parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtNMinParams")))
						.getText());
		config.MIN_CARDINALITY = Integer.parseInt(
				((JTextField) parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtMinCardinality")))
						.getText());
		config.MAX_CARDINALITY = Integer.parseInt(
				((JTextField) parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtMaxCardinality")))
						.getText());
		config.LOWER_BOUND_INT = Integer.parseInt(
				((JTextField) parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtLowerBoundInt")))
						.getText());
		config.UPPER_BOUND_INT = Integer.parseInt(
				((JTextField) parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtUpperBoundInt")))
						.getText());
		config.N_CONSTRAINTS_MAX = Integer.parseInt(((JTextField) parentFrame.getPanelConfigurations()
				.getComponent(componentsMap.get("txtNMaxConstraints"))).getText());
		config.N_CONSTRAINTS_MIN = Integer.parseInt(((JTextField) parentFrame.getPanelConfigurations()
				.getComponent(componentsMap.get("txtNMinConstraints"))).getText());
		config.MAX_CONSTRAINTS_COMPLEXITY = Integer.parseInt(((JTextField) parentFrame
				.getPanelConfigurations().getComponent(componentsMap.get("txtMaxConstraintsComplexity"))).getText());
		config.MIN_CONSTRAINTS_COMPLEXITY = Integer.parseInt(((JTextField) parentFrame
				.getPanelConfigurations().getComponent(componentsMap.get("txtMinConstraintComplexity"))).getText());
		config.RATIO = Double.parseDouble(
				((JTextField) parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtRatio")))
						.getText());
		config.P = Double.parseDouble(
				((JTextField) parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtPTest")))
						.getText());
		config.EPSILON = Double.parseDouble(
				((JTextField) parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtEpsilonTest")))
						.getText());
		config.RATIO_TEST = Double.parseDouble(
				((JTextField) parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtRatioTest")))
						.getText());
		config.USE_CONSTRAINTS_BETWEEN_PARAMETERS = ((JCheckBox) (parentFrame.getPanelConfigurations()
				.getComponent(componentsMap.get("chkConstraintsBetweenParams")))).isSelected();
		config.FORBIDDEN_TUPLES = ((JCheckBox) (parentFrame.getPanelConfigurations()
				.getComponent(componentsMap.get("chkForbiddenTuples")))).isSelected();
		config.CNF = ((JCheckBox) (parentFrame.getPanelConfigurations()
				.getComponent(componentsMap.get("chkCNF")))).isSelected();
		config.TRACK = t;
		config.CHECK_TEST_RATIO = parentFrame.isRatioTest();
		config.CHECK_TUPLE_RATIO = parentFrame.isRatioTuple();
		config.ACTS = parentFrame.isACTS();
		config.CTWEDGE = parentFrame.isCTWedge();
		config.PICT = parentFrame.isPICT();
		
		return config;
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
			model.addRow(new Object[] { m.getName() });
		}
	}

}
