package benchmark.generator.handlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import benchmark.generator.gui.BenchmarkGenerator;
import generators.Generator;
import generators.GeneratorConfiguration;
import generators.WithoutConstraintGenerator;

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
			GeneratorConfiguration.N_PARAMS_MAX = Integer.parseInt(((JTextField) parentFrame.getPanelConfigurations()
					.getComponent(componentsMap.get("txtNMaxParams"))).getText());
			GeneratorConfiguration.N_PARAMS_MIN = Integer.parseInt(((JTextField) parentFrame.getPanelConfigurations()
					.getComponent(componentsMap.get("txtNMinParams"))).getText());
			break;
		}
	}

}
