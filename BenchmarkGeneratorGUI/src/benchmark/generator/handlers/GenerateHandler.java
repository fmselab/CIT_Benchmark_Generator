package benchmark.generator.handlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import benchmark.generator.gui.BenchmarkGenerator;
import generators.Category;
import generators.Generator;
import generators.GeneratorConfiguration;
import generators.WithoutConstraintGenerator;
import generators.WithoutConstraintGeneratorSameCardinality;
import models.Model;
import models.ModelList;

public class GenerateHandler implements ActionListener {

	private BenchmarkGenerator parentFrame;
	private HashMap<String, Integer> componentsMap;

	public GenerateHandler(BenchmarkGenerator frame) {
		parentFrame = frame;
		componentsMap = frame.getConfigurationComponents();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e) {
		JPanel panel = parentFrame.getPanelConfigurations();
		JComboBox<String> benchmarkType = (JComboBox<String>) Arrays.stream(panel.getComponents())
				.filter(x -> x.getName() != null && x.getName().equals("benchmarkType")).findFirst().get();
		String selectedText = (String) benchmarkType.getSelectedItem();
		Generator gWOC = new WithoutConstraintGenerator();
		Generator gWSC = new WithoutConstraintGeneratorSameCardinality();
		int nModels = GeneratorConfiguration.N_PARAMS_MIN = Integer.parseInt(
				((JTextField) parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtNumBenchmarks")))
						.getText());

		parentFrame.getModelList().clearModels();
		setConfiguration();

		switch (selectedText) {
		case BenchmarkGenerator.BOOLC:

			break;
		case BenchmarkGenerator.CNF:

			break;
		case BenchmarkGenerator.HIGHLY_CONSTRAINED:

			break;
		case BenchmarkGenerator.MCA:
			for (int i = 0; i < nModels; i++) {
				Model m1 = gWOC.generate(Category.ALSO_ENUMS);
				m1.setName(BenchmarkGenerator.MCA + "_" + i);
				parentFrame.getModelList().addModel(m1);
			}
			break;
		case BenchmarkGenerator.MCAC:

			break;
		case BenchmarkGenerator.NUMC:

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

		showModels(parentFrame.getModelList());
	}

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
	}

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
