package benchmark.generator.handlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.math3.analysis.function.Expm1;

import benchmark.generator.gui.BenchmarkGenerator;
import generators.Category;
import generators.Generator;
import generators.GeneratorConfiguration;
import generators.WithoutConstraintGenerator;
import models.Model;

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
		
		ArrayList<Model> models = new ArrayList<>(); 
		
		switch (selectedText) {
		case BenchmarkGenerator.BOOLC:

			break;
		case BenchmarkGenerator.CNF:

			break;
		case BenchmarkGenerator.EMPTY_TYPE:

			break;
		case BenchmarkGenerator.HIGHLY_CONSTRAINED:

			break;
		case BenchmarkGenerator.MCA:

			break;
		case BenchmarkGenerator.MCAC:

			break;
		case BenchmarkGenerator.NUMC:

			break;
		case BenchmarkGenerator.UNIFORM_ALL:

			break;
		case BenchmarkGenerator.UNIFORM_BOOLEAN:
			Generator g = new WithoutConstraintGenerator();
			int nModels = GeneratorConfiguration.N_PARAMS_MIN = Integer.parseInt(((JTextField) parentFrame.getPanelConfigurations()
					.getComponent(componentsMap.get("txtNumBenchmarks"))).getText());
			GeneratorConfiguration.N_PARAMS_MAX = Integer.parseInt(((JTextField) parentFrame.getPanelConfigurations()
					.getComponent(componentsMap.get("txtNMaxParams"))).getText());
			GeneratorConfiguration.N_PARAMS_MIN = Integer.parseInt(((JTextField) parentFrame.getPanelConfigurations()
					.getComponent(componentsMap.get("txtNMinParams"))).getText());
			
			for (int i=0; i<nModels; i++) {
				Model m1 = g.generate(Category.ONLY_BOOLEAN);
				m1.setName(BenchmarkGenerator.UNIFORM_BOOLEAN + i);
				models.add(m1);
			}
			break;
		}
		
		showModels(models);
	}

	private void showModels(ArrayList<Model> models) {
		DefaultTableModel model = parentFrame.getModel();
		// Fill the table with the model names
		for (Model m : models) {
			model.addRow(new Object[] {m.getName()});
		}
	}

}
