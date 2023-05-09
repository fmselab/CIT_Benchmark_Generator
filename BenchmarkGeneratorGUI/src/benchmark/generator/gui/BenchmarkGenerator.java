package benchmark.generator.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import java.awt.GridLayout;
import javax.swing.JTextField;

import benchmark.generator.handlers.BenchmarkTypeChangeHandler;
import benchmark.generator.handlers.BenchmarksExporterHandler;
import benchmark.generator.handlers.GenerateHandler;
import generators.GeneratorConfiguration;

import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.awt.event.ActionEvent;

public class BenchmarkGenerator {

	public static final String HIGHLY_CONSTRAINED = "HIGHLY_CONSTRAINED";
	public static final String CNF = "CNF";
	public static final String NUMC = "NUMC";
	public static final String MCAC = "MCAC";
	public static final String BOOLC = "BOOLC";
	public static final String MCA = "MCA";
	public static final String UNIFORM_ALL = "UNIFORM_ALL";
	public static final String UNIFORM_BOOLEAN = "UNIFORM_BOOLEAN";
	public static final String EMPTY_TYPE = "";
	private JFrame frame;
	private JTextField txtNMaxParams;
	private JTextField txtNMinParams;
	private JTextField txtNMaxEnums;
	private JTextField txtLowerBoundInt;
	private JTextField txtUpperBoundInt;
	private JTextField txtMaxCardinality;
	private JTextField txtNMinConstraints;
	private JTextField txtNMaxConstraints;
	private JTextField txtMaxConstraintsComplexity;
	private JTextField txtMinSize;
	private JTextField txtMaxSize;
	private JTextField txtMinCardinality;
	private JTextField txtMinConstraintComplexity;
	private JTextField txtNumBenchmarks;
	private JTable tblTestCases;
	private JComboBox<String> benchmarkType;
	private JSplitPane splitView;
	private JSplitPane splitLeftView;
	private JPanel panelConfigurations;
	private JLabel lblBenchmarkType;
	private JLabel lblNMinParams;
	private JLabel lblNMaxParams;
	private JLabel lblNMaxEnums;
	private JLabel lblLowerBoundInt;
	private JLabel lblUpperBoundInt;
	private JPanel panelTestSuite;
	private JPanel panelTests;
	private JButton btnExportAll;
	private JButton btnGenerate;
	private JLabel lblNumBenchmarks;
	private JLabel lblMinCardinality;
	private JCheckBox chkConstraintsBetweenParams;
	private JLabel lblMaxCardinality;
	private JLabel lblMaxSize;
	private JLabel lblMinSize;
	private JLabel lblMinConstraintComplexity;
	private JLabel lblMaxConstraintComplexity;
	private JLabel lblNMaxConstraints;
	private JLabel lblNMinConstraints;
	private JLabel lblPlaceHolder;
	private HashMap<String, Integer> configurationComponents;

	/**
	 * Returns the mapping between component's name and index
	 * 
	 * @return the mapping between component's name and index
	 */
	public HashMap<String, Integer> getConfigurationComponents() {
		return configurationComponents;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BenchmarkGenerator window = new BenchmarkGenerator();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public BenchmarkGenerator() {
		configurationComponents = new HashMap<>();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 715, 663);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		splitView = new JSplitPane();
		frame.getContentPane().add(splitView, BorderLayout.CENTER);

		splitLeftView = new JSplitPane();
		splitLeftView.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitView.setLeftComponent(splitLeftView);

		panelConfigurations = new JPanel();
		splitLeftView.setLeftComponent(panelConfigurations);
		panelConfigurations.setLayout(new GridLayout(0, 2, 0, 0));

		lblBenchmarkType = new JLabel("Benchmark type");
		addToPanelConfigurations(lblBenchmarkType, "lblBenchmarkType");

		benchmarkType = new JComboBox<String>();
		fillBenchmarkTypes();
		addToPanelConfigurations(benchmarkType, "benchmarkType");

		lblNMinParams = new JLabel("N Min Params");
		addToPanelConfigurations(lblNMinParams, "lblNMinParams");

		txtNMinParams = new JTextField();
		txtNMinParams.setColumns(10);
		addToPanelConfigurations(txtNMinParams, "txtNMinParams");

		lblNMaxParams = new JLabel("N Max Params");
		addToPanelConfigurations(lblNMaxParams, "lblNMaxParams");

		txtNMaxParams = new JTextField();
		addToPanelConfigurations(txtNMaxParams, "txtNMaxParams");
		txtNMaxParams.setColumns(10);

		lblNMaxEnums = new JLabel("Dim Max Enums");
		addToPanelConfigurations(lblNMaxEnums, "lblNMaxEnums");

		txtNMaxEnums = new JTextField();
		txtNMaxEnums.setColumns(10);
		addToPanelConfigurations(txtNMaxEnums, "txtNMaxEnums");

		lblLowerBoundInt = new JLabel("Lower Bound Ints");
		addToPanelConfigurations(lblLowerBoundInt, "lblLowerBoundInt");

		txtLowerBoundInt = new JTextField();
		txtLowerBoundInt.setColumns(10);
		addToPanelConfigurations(txtLowerBoundInt, "txtLowerBoundInt");

		lblUpperBoundInt = new JLabel("Upper Bound Ints");
		addToPanelConfigurations(lblUpperBoundInt, "lblUpperBoundInt");

		txtUpperBoundInt = new JTextField();
		txtUpperBoundInt.setColumns(10);
		addToPanelConfigurations(txtUpperBoundInt, "txtUpperBoundInt");

		lblMinCardinality = new JLabel("Min cardinality");
		addToPanelConfigurations(lblMinCardinality, "lblMinCardinality");

		txtMinCardinality = new JTextField();
		txtMinCardinality.setColumns(10);
		addToPanelConfigurations(txtMinCardinality, "txtMinCardinality");

		lblMaxCardinality = new JLabel("Max cardinality");
		addToPanelConfigurations(lblMaxCardinality, "lblMaxCardinality");

		txtMaxCardinality = new JTextField();
		txtMaxCardinality.setColumns(10);
		addToPanelConfigurations(txtMaxCardinality, "txtMaxCardinality");

		lblNMinConstraints = new JLabel("N Min Constraints");
		addToPanelConfigurations(lblNMinConstraints, "lblNMinConstraints");

		txtNMinConstraints = new JTextField();
		txtNMinConstraints.setColumns(10);
		addToPanelConfigurations(txtNMinConstraints, "txtNMinConstraints");

		lblNMaxConstraints = new JLabel("N Max Constraints");
		addToPanelConfigurations(lblNMaxConstraints, "lblNMaxConstraints");

		txtNMaxConstraints = new JTextField();
		txtNMaxConstraints.setColumns(10);
		addToPanelConfigurations(txtNMaxConstraints, "txtNMaxConstraints");

		lblMinConstraintComplexity = new JLabel("Min Constraints Complexity");
		addToPanelConfigurations(lblMinConstraintComplexity, "lblMinConstraintComplexity");

		txtMinConstraintComplexity = new JTextField();
		txtMinConstraintComplexity.setColumns(10);
		addToPanelConfigurations(txtMinConstraintComplexity, "txtMinConstraintComplexity");

		lblMaxConstraintComplexity = new JLabel("Max Constraints Complexity");
		addToPanelConfigurations(lblMaxConstraintComplexity, "lblMaxConstraintComplexity");

		txtMaxConstraintsComplexity = new JTextField();
		txtMaxConstraintsComplexity.setColumns(10);
		addToPanelConfigurations(txtMaxConstraintsComplexity, "txtMaxConstraintsComplexity");
		
		lblMinSize = new JLabel("Min Size");
		addToPanelConfigurations(lblMinSize, "lblMinSize");

		txtMinSize = new JTextField();
		txtMinSize.setColumns(10);
		addToPanelConfigurations(txtMinSize, "txtMinSize");

		lblMaxSize = new JLabel("Max Size");
		addToPanelConfigurations(lblMaxSize, "lblMaxSize");

		txtMaxSize = new JTextField();
		txtMaxSize.setColumns(10);
		addToPanelConfigurations(txtMaxSize, "txtMaxSize");

		chkConstraintsBetweenParams = new JCheckBox("Use constraints between params");
		addToPanelConfigurations(chkConstraintsBetweenParams, "chkConstraintsBetweenParams");

		lblPlaceHolder = new JLabel(EMPTY_TYPE);
		addToPanelConfigurations(lblPlaceHolder, "lblPlaceHolder");

		lblNumBenchmarks = new JLabel("Num. Benchmarks");
		addToPanelConfigurations(lblNumBenchmarks, "lblNumBenchmarks");

		txtNumBenchmarks = new JTextField("1");
		txtNumBenchmarks.setColumns(10);
		addToPanelConfigurations(txtNumBenchmarks, "txtNumBenchmarks");

		btnGenerate = new JButton("Generate");
		addToPanelConfigurations(btnGenerate, "btnGenerate");

		btnExportAll = new JButton("ExportAll");
		addToPanelConfigurations(btnExportAll, "btnExportAll");

		panelTests = new JPanel();
		splitLeftView.setRightComponent(panelTests);

		tblTestCases = new JTable();
		panelTests.add(tblTestCases);

		panelTestSuite = new JPanel();
		splitView.setRightComponent(panelTestSuite);

		addListeners();
		getDefaultParams();
	}

	/**
	 * Adds the component given as parameter to the panel configuration
	 * 
	 * @param component the component
	 * @param name the name of the component
	 */
	private void addToPanelConfigurations(JComponent component, String name) {
		int index = panelConfigurations.getComponentCount();
		component.setName(name);
		panelConfigurations.add(component);
		configurationComponents.put(name, index);
	}

	/**
	 * Returns the panel containing the configuration parameters
	 * 
	 * @return the panel containing the configuration parameters
	 */
	public JPanel getPanelConfigurations() {
		return panelConfigurations;
	}

	/**
	 * Fill the benchmark types
	 */
	private void fillBenchmarkTypes() {
		String[] bnckTypes = { EMPTY_TYPE, UNIFORM_BOOLEAN, UNIFORM_ALL, MCA, BOOLC, MCAC, NUMC, CNF,
				HIGHLY_CONSTRAINED };
		for (String s : bnckTypes) {
			benchmarkType.addItem(s);
		}
	}

	/**
	 * Fill all text boxes with default values, taken from the BenchmarkGenerator
	 * CLI project
	 */
	private void getDefaultParams() {
		txtNMaxEnums.setText(Integer.toString(GeneratorConfiguration.DIM_ENUMS_MAX));
		txtLowerBoundInt.setText(Integer.toString(GeneratorConfiguration.LOWER_BOUND_INT));
		txtUpperBoundInt.setText(Integer.toString(GeneratorConfiguration.UPPER_BOUND_INT));
		txtMaxCardinality.setText(Integer.toString(GeneratorConfiguration.MAX_CARDINALITY));
		txtMinCardinality.setText(Integer.toString(GeneratorConfiguration.MIN_CARDINALITY));
		txtMaxConstraintsComplexity.setText(Integer.toString(GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY));
		txtMinConstraintComplexity.setText(Integer.toString(GeneratorConfiguration.MIN_CONSTRAINTS_COMPLEXITY));
		txtMaxSize.setText(Integer.toString(GeneratorConfiguration.MAX_SIZE));
		txtMinSize.setText(Integer.toString(GeneratorConfiguration.MIN_SIZE));
		txtNMinConstraints.setText(Integer.toString(GeneratorConfiguration.N_CONSTRAINTS_MIN));
		txtNMaxConstraints.setText(Integer.toString(GeneratorConfiguration.N_CONSTRAINTS_MAX));
		txtNumBenchmarks.setText("1");
		chkConstraintsBetweenParams.setSelected(GeneratorConfiguration.USE_CONSTRAINTS_BETWEEN_PARAMETERS);
		txtNMinParams.setText(Integer.toString(GeneratorConfiguration.N_PARAMS_MIN));
		txtNMaxParams.setText(Integer.toString(GeneratorConfiguration.N_PARAMS_MAX));
	}

	/**
	 * Add the listeners to the components
	 */
	private void addListeners() {
		benchmarkType.addActionListener(new BenchmarkTypeChangeHandler(this));
		btnGenerate.addActionListener(new GenerateHandler());
		btnExportAll.addActionListener(new BenchmarksExporterHandler());
	}

}
