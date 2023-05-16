package benchmark.generator.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

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
		// Extract the selected text in the benchmarkType combo-box
		JPanel panel = parentFrame.getPanelConfigurations();
		JComboBox<String> benchmarkType = (JComboBox<String>) Arrays.stream(panel.getComponents())
				.filter(x -> x.getName() != null && x.getName().equals("benchmarkType")).findFirst().get();
		String selectedText = (String) benchmarkType.getSelectedItem();

		// Based on the selected text, lock the items
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

	/**
	 * Locks the ratio-related fields
	 * 
	 * @param lock the boolean setting the lock status
	 */
	private void lockRatio(boolean lock) {
		parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtRatio")).setEnabled(!lock);
		parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtRatioTest")).setEnabled(!lock);
		parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtEpsilonTest")).setEnabled(!lock);
		parentFrame.getPanelConfigurations().getComponent(componentsMap.get("txtTTest")).setEnabled(!lock);
		
		((JCheckBox)parentFrame.getPanelConfigurations().getComponent(componentsMap.get("chkTestRatio"))).setSelected(!lock);
		((JCheckBox)parentFrame.getPanelConfigurations().getComponent(componentsMap.get("chkTupleRatio"))).setSelected(!lock);
	}

	/**
	 * Locks the integer-related fields
	 * 
	 * @param lock the boolean setting the lock status
	 */
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
		((JCheckBox)parentFrame.getPanelConfigurations().getComponent(componentsMap.get("chkTestRatio"))).setEnabled(!lock);
		((JCheckBox)parentFrame.getPanelConfigurations().getComponent(componentsMap.get("chkTupleRatio"))).setEnabled(!lock);
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
