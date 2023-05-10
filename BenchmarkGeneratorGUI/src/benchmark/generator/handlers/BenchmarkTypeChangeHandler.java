package benchmark.generator.handlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import benchmark.generator.gui.BenchmarkGenerator;

public class BenchmarkTypeChangeHandler implements ActionListener {

	private BenchmarkGenerator parentFrame;
	private HashMap<String, Integer> componentsMap;

	/**
	 * Creates a new BenchmarkTypeChangeHandler
	 * 
	 * @param frame the frame containing the components to be handled
	 */
	public BenchmarkTypeChangeHandler(BenchmarkGenerator frame) {
		parentFrame = frame;
		componentsMap = parentFrame.getConfigurationComponents();
	}

	/**
	 * Locks the fields depending on the chosen benchmark type
	 * 
	 * @param e the event to be handled
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e) {
		JPanel panel = parentFrame.getPanelConfigurations();
		JComboBox<String> benchmarkType = (JComboBox<String>) Arrays.stream(panel.getComponents())
				.filter(x -> x.getName() != null && x.getName().equals("benchmarkType")).findFirst().get();
		String selectedText = (String) benchmarkType.getSelectedItem();
		switch (selectedText) {
		case BenchmarkGenerator.BOOLC:
			lockConstraints(false);
			lockNonBoolean(false);
			lockIntegers(true);
			lockRatio(true);
			break;
		case BenchmarkGenerator.CNF:
			lockConstraints(false);
			lockNonBoolean(true);
			lockIntegers(true);
			lockRatio(true);
			break;
		case BenchmarkGenerator.EMPTY_TYPE:
			lockConstraints(false);
			lockNonBoolean(false);
			lockIntegers(false);
			lockRatio(true);
			break;
		case BenchmarkGenerator.HIGHLY_CONSTRAINED:
			lockConstraints(false);
			lockNonBoolean(true);
			lockIntegers(true);
			lockRatio(false);
			break;
		case BenchmarkGenerator.MCA:
			lockConstraints(true);
			lockNonBoolean(true);
			lockIntegers(true);
			lockRatio(true);
			break;
		case BenchmarkGenerator.MCAC:
			lockConstraints(false);
			lockNonBoolean(true);
			lockIntegers(true);
			lockRatio(true);
			break;
		case BenchmarkGenerator.NUMC:
			lockConstraints(false);
			lockNonBoolean(true);
			lockIntegers(false);
			lockRatio(true);
			break;
		case BenchmarkGenerator.UNIFORM_ALL:
			lockConstraints(true);
			lockNonBoolean(true);
			lockIntegers(true);
			lockRatio(true);
			break;
		case BenchmarkGenerator.UNIFORM_BOOLEAN:
			lockConstraints(true);
			lockNonBoolean(false);
			lockIntegers(true);
			lockRatio(true);
			break;
		}
	}

	private void lockRatio(boolean lock) {
		parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtRatio")).setEnabled(!lock);

	}

	private void lockIntegers(boolean lock) {
		parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtLowerBoundInt")).setEnabled(!lock);
		parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtUpperBoundInt")).setEnabled(!lock);

	}

	/**
	 * Fetch all the components and lock/unlock those related to the constraints
	 * 
	 * @param lock the boolean setting the lock status
	 */
	private void lockConstraints(boolean lock) {
		parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtMaxConstraintsComplexity"))
				.setEnabled(!lock);
		parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtMinConstraintComplexity"))
				.setEnabled(!lock);
		parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtNMinConstraints")).setEnabled(!lock);
		parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtNMaxConstraints")).setEnabled(!lock);
		parentFrame.getPanelConfigurations().getComponent(componentsMap.get("chkConstraintsBetweenParams"))
				.setEnabled(!lock);
	}

	/**
	 * Fetch all the components and lock/unlock those related to the non boolean
	 * variables
	 * 
	 * @param lock the boolean setting the enabled status
	 */
	private void lockNonBoolean(boolean lock) {
		parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtMinCardinality")).setEnabled(lock);
		parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtMaxCardinality")).setEnabled(lock);
	}

}
