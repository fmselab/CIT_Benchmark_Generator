package benchmark.generator.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;

import org.jfree.ui.FilesystemFilter;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.api.SolverException;

import generators.GeneratorConfiguration;
import main.BenchmarkGeneratorCLI;
import models.ModelList;

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
	private JTextField txtLowerBoundInt;
	private JTextField txtUpperBoundInt;
	private JTextField txtMaxCardinality;
	private JTextField txtNMinConstraints;
	private JTextField txtNMaxConstraints;
	private JTextField txtMaxConstraintsComplexity;
	private JTextField txtMinCardinality;
	private JTextField txtMinConstraintComplexity;
	private JTextField txtNumBenchmarks;
	private JTextField txtRatio;
	private JTextField txtRatioTest;
	private JTextField txtEpsilonTest;
	private JTextField txtTTest;
	private JTable tblTestCases;
	private JComboBox<String> benchmarkType;
	private JSplitPane splitView;
	private JSplitPane splitLeftView;
	private JPanel panelConfigurations;
	private JLabel lblBenchmarkType;
	private JLabel lblNMinParams;
	private JLabel lblNMaxParams;
	private JLabel lblRatio;
	private JLabel lblRatioTest;
	private JLabel lblEpsilonTest;
	private JLabel lblTTest;
	private JLabel lblLowerBoundInt;
	private JLabel lblUpperBoundInt;
	private JPanel panelTestSuite;
	private JPanel panelTests;
	private JButton btnExportAll;
	private JButton btnGenerate;
	private JLabel lblNumBenchmarks;
	private JLabel lblMinCardinality;
	private JCheckBox chkConstraintsBetweenParams;
	private JCheckBox chkForbiddenTuples;
	private JCheckBox chkTestRatio;
	private JCheckBox chkTupleRatio;
	private JLabel lblMaxCardinality;
	private JLabel lblMinConstraintComplexity;
	private JLabel lblMaxConstraintComplexity;
	private JLabel lblNMaxConstraints;
	private JLabel lblNMinConstraints;
	private JLabel lblPlaceHolder;
	private HashMap<String, Integer> configurationComponents;
	private DefaultTableModel model;
	private JScrollPane scrollableTable;
	private ModelList modelList;
	private JTextPane textModel;
	private JMenuBar menuBar;
	private JMenu itemFormat;
	private JCheckBoxMenuItem chkBoxACTS;
	private JCheckBoxMenuItem chkBoxCTWedge;
	private JCheckBoxMenuItem chkBoxPICT;
	private JMenu itemExtension;
	private String selectedFile;
	private JMenuItem btnBaseline2;

	/**
	 * Returns the mapping between component's name and index
	 * 
	 * @return the mapping between component's name and index
	 */
	public HashMap<String, Integer> getConfigurationComponents() {
		return configurationComponents;
	}

	/**
	 * Access the model of the table containing benchmarks' name
	 * 
	 * @return the model of the table containing benchmarks' name
	 */
	public DefaultTableModel getModel() {
		return model;
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
		modelList = new ModelList();
		initialize();
		// The GUI does not always export models, but only on request
		GeneratorConfiguration.ALWAYS_EXPORT = false;
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

		lblNMinParams = new JLabel("N Min Params (k)");
		addToPanelConfigurations(lblNMinParams, "lblNMinParams");

		txtNMinParams = new JTextField();
		txtNMinParams.setColumns(10);
		addToPanelConfigurations(txtNMinParams, "txtNMinParams");

		lblNMaxParams = new JLabel("N Max Params (k)");
		addToPanelConfigurations(lblNMaxParams, "lblNMaxParams");

		txtNMaxParams = new JTextField();
		addToPanelConfigurations(txtNMaxParams, "txtNMaxParams");
		txtNMaxParams.setColumns(10);

		lblLowerBoundInt = new JLabel("Lower Bound Ints (v)");
		addToPanelConfigurations(lblLowerBoundInt, "lblLowerBoundInt");

		txtLowerBoundInt = new JTextField();
		txtLowerBoundInt.setColumns(10);
		addToPanelConfigurations(txtLowerBoundInt, "txtLowerBoundInt");

		lblUpperBoundInt = new JLabel("Upper Bound Ints (v)");
		addToPanelConfigurations(lblUpperBoundInt, "lblUpperBoundInt");

		txtUpperBoundInt = new JTextField();
		txtUpperBoundInt.setColumns(10);
		addToPanelConfigurations(txtUpperBoundInt, "txtUpperBoundInt");

		lblMinCardinality = new JLabel("Min cardinality (v)");
		addToPanelConfigurations(lblMinCardinality, "lblMinCardinality");

		txtMinCardinality = new JTextField();
		txtMinCardinality.setColumns(10);
		addToPanelConfigurations(txtMinCardinality, "txtMinCardinality");

		lblMaxCardinality = new JLabel("Max cardinality (v)");
		addToPanelConfigurations(lblMaxCardinality, "lblMaxCardinality");

		txtMaxCardinality = new JTextField();
		txtMaxCardinality.setColumns(10);
		addToPanelConfigurations(txtMaxCardinality, "txtMaxCardinality");

		lblNMinConstraints = new JLabel("N Min Constraints (c)");
		addToPanelConfigurations(lblNMinConstraints, "lblNMinConstraints");

		txtNMinConstraints = new JTextField();
		txtNMinConstraints.setColumns(10);
		addToPanelConfigurations(txtNMinConstraints, "txtNMinConstraints");

		lblNMaxConstraints = new JLabel("N Max Constraints (c)");
		addToPanelConfigurations(lblNMaxConstraints, "lblNMaxConstraints");

		txtNMaxConstraints = new JTextField();
		txtNMaxConstraints.setColumns(10);
		addToPanelConfigurations(txtNMaxConstraints, "txtNMaxConstraints");

		lblMinConstraintComplexity = new JLabel("Min Constraints Complexity (d)");
		addToPanelConfigurations(lblMinConstraintComplexity, "lblMinConstraintComplexity");

		txtMinConstraintComplexity = new JTextField();
		txtMinConstraintComplexity.setColumns(10);
		addToPanelConfigurations(txtMinConstraintComplexity, "txtMinConstraintComplexity");

		lblMaxConstraintComplexity = new JLabel("Max Constraints Complexity (d)");
		addToPanelConfigurations(lblMaxConstraintComplexity, "lblMaxConstraintComplexity");

		txtMaxConstraintsComplexity = new JTextField();
		txtMaxConstraintsComplexity.setColumns(10);
		addToPanelConfigurations(txtMaxConstraintsComplexity, "txtMaxConstraintsComplexity");

		chkConstraintsBetweenParams = new JCheckBox("Use constraints between params");
		addToPanelConfigurations(chkConstraintsBetweenParams, "chkConstraintsBetweenParams");

		lblPlaceHolder = new JLabel(EMPTY_TYPE);
		addToPanelConfigurations(lblPlaceHolder, "lblPlaceHolder");
		
		chkForbiddenTuples = new JCheckBox("Only forbidden tuples");
		addToPanelConfigurations(chkForbiddenTuples, "chkForbiddenTuples");

		lblPlaceHolder = new JLabel(EMPTY_TYPE);
		addToPanelConfigurations(lblPlaceHolder, "lblPlaceHolder");

		chkTupleRatio = new JCheckBox("Check Tuple Ratio");
		addToPanelConfigurations(chkTupleRatio, "chkTupleRatio");

		lblPlaceHolder = new JLabel(EMPTY_TYPE);
		addToPanelConfigurations(lblPlaceHolder, "lblPlaceHolder");

		lblRatio = new JLabel("Max Tuple Ratio Accepted (R)");
		addToPanelConfigurations(lblRatio, "lblRatio");

		txtRatio = new JTextField();
		txtRatio.setColumns(10);
		addToPanelConfigurations(txtRatio, "txtRatio");

		chkTestRatio = new JCheckBox("Check Test Ratio");
		addToPanelConfigurations(chkTestRatio, "chkTestRatio");

		lblPlaceHolder = new JLabel(EMPTY_TYPE);
		addToPanelConfigurations(lblPlaceHolder, "lblPlaceHolder");

		lblRatioTest = new JLabel("Max Test Ratio Accepted (tR)");
		addToPanelConfigurations(lblRatioTest, "lblRatioTest");

		txtRatioTest = new JTextField();
		txtRatioTest.setColumns(10);
		addToPanelConfigurations(txtRatioTest, "txtRatioTest");

		lblTTest = new JLabel("Num. Tests for Ratio");
		addToPanelConfigurations(lblTTest, "lblTTest");

		txtTTest = new JTextField();
		txtTTest.setColumns(10);
		addToPanelConfigurations(txtTTest, "txtTTest");

		lblEpsilonTest = new JLabel("Error for Ratio");
		addToPanelConfigurations(lblEpsilonTest, "lblEpsilonTest");

		txtEpsilonTest = new JTextField();
		txtEpsilonTest.setColumns(10);
		addToPanelConfigurations(txtEpsilonTest, "txtEpsilonTest");

		chkTestRatio.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtRatioTest.setEnabled(chkTestRatio.isSelected());
				txtTTest.setEnabled(chkTestRatio.isSelected());
				txtEpsilonTest.setEnabled(chkTestRatio.isSelected());
			}
		});

		chkTupleRatio.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtRatio.setEnabled(chkTupleRatio.isSelected());
			}
		});

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

		model = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				// Disable the editing in all cells
				return false;
			}
		};
		model.addColumn("Benchmark name");

		tblTestCases = new JTable(model);
		tblTestCases.setAutoResizeMode(JTable.WIDTH);
		panelTests.add(tblTestCases);

		scrollableTable = new JScrollPane(tblTestCases);
		panelTests.add(scrollableTable);

		panelTestSuite = new JPanel();
		splitView.setRightComponent(panelTestSuite);

		textModel = new JTextPane();
		final JScrollPane scrollPane = new JScrollPane(textModel) {
			private static final long serialVersionUID = 1L;

			/**
			 * Auto resize the textarea based on the window size
			 */
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(panelTestSuite.getSize().width - 10, panelTestSuite.getSize().height - 10);
			}
		};
		panelTestSuite.add(scrollPane, BorderLayout.CENTER);

		menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		itemFormat = new JMenu("Export format");
		menuBar.add(itemFormat);

		chkBoxACTS = new JCheckBoxMenuItem("ACTS");
		itemFormat.add(chkBoxACTS);

		chkBoxCTWedge = new JCheckBoxMenuItem("CTWedge");
		itemFormat.add(chkBoxCTWedge);

		chkBoxPICT = new JCheckBoxMenuItem("PICT");
		itemFormat.add(chkBoxPICT);

		itemExtension = new JMenu("Additional funct.");
		menuBar.add(itemExtension);

		btnBaseline2 = new JMenuItem("Set baseline IPM");
		btnBaseline2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.addChoosableFileFilter(new FilesystemFilter("ctw", "CTWedge files"));
				int returnVal = fc.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						File file = fc.getSelectedFile();
						selectedFile = file.getAbsolutePath();
						// Set the configuration params
						BenchmarkGeneratorCLI.setConfigurationsFromFile(selectedFile);
						// Refresh the view
						getDefaultParams();
					} catch (InterruptedException | InvalidConfigurationException | SolverException | IOException e1) {
						e1.printStackTrace();
					}
				}

			}
		});
		itemExtension.add(btnBaseline2);

		chkBoxACTS.setSelected(true);
		chkBoxCTWedge.setSelected(true);
		chkBoxPICT.setSelected(true);

		addListeners();
		getDefaultParams();
	}

	/**
	 * Access the panel containing the text of the benchmark
	 * 
	 * @return the JTextPane textModel
	 */
	public JTextPane getTestModel() {
		return textModel;
	}

	/**
	 * Adds the component given as parameter to the panel configuration
	 * 
	 * @param component the component
	 * @param name      the name of the component
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
		txtLowerBoundInt.setText(Integer.toString(GeneratorConfiguration.LOWER_BOUND_INT));
		txtUpperBoundInt.setText(Integer.toString(GeneratorConfiguration.UPPER_BOUND_INT));
		txtMaxCardinality.setText(Integer.toString(GeneratorConfiguration.MAX_CARDINALITY));
		txtMinCardinality.setText(Integer.toString(GeneratorConfiguration.MIN_CARDINALITY));
		txtMaxConstraintsComplexity.setText(Integer.toString(GeneratorConfiguration.MAX_CONSTRAINTS_COMPLEXITY));
		txtMinConstraintComplexity.setText(Integer.toString(GeneratorConfiguration.MIN_CONSTRAINTS_COMPLEXITY));
		txtNMinConstraints.setText(Integer.toString(GeneratorConfiguration.N_CONSTRAINTS_MIN));
		txtNMaxConstraints.setText(Integer.toString(GeneratorConfiguration.N_CONSTRAINTS_MAX));
		txtNumBenchmarks.setText("1");
		chkConstraintsBetweenParams.setSelected(GeneratorConfiguration.USE_CONSTRAINTS_BETWEEN_PARAMETERS);
		chkForbiddenTuples.setSelected(GeneratorConfiguration.FORBIDDEN_TUPLES);
		txtNMinParams.setText(Integer.toString(GeneratorConfiguration.N_PARAMS_MIN));
		txtNMaxParams.setText(Integer.toString(GeneratorConfiguration.N_PARAMS_MAX));
		txtRatio.setText(Double.toString(GeneratorConfiguration.RATIO));
		txtRatioTest.setText(Double.toString(GeneratorConfiguration.RATIO_TEST));
		txtEpsilonTest.setText(Double.toString(GeneratorConfiguration.EPSILON));
		txtTTest.setText(Integer.toString(GeneratorConfiguration.N));
		benchmarkType.setSelectedItem(GeneratorConfiguration.TRACK.toString());
	}

	/**
	 * Add the listeners to the components
	 */
	private void addListeners() {
		benchmarkType.addActionListener(new BenchmarkTypeChangeHandler(this));
		btnGenerate.addActionListener(new GenerateHandler(this));
		btnExportAll.addActionListener(new BenchmarksExporterHandler(this));
		tblTestCases.addMouseListener(new TableClickListener(this));
	}

	/**
	 * Access the model list, populated by the generators
	 * 
	 * @return the ModelList
	 */
	public ModelList getModelList() {
		return modelList;
	}

	/**
	 * Empties the model list
	 */
	public void emptyModel() {
		model.setRowCount(0);
		tblTestCases.revalidate();
	}

	/**
	 * Export in ACTS format?
	 * 
	 * @return TRUE if ACTS is required, FALSE otherwise
	 */
	public boolean isACTS() {
		return chkBoxACTS.isSelected();
	}

	/**
	 * Export in CTWedge format?
	 * 
	 * @return TRUE if CTWedge is required, FALSE otherwise
	 */
	public boolean isCTWedge() {
		return chkBoxCTWedge.isSelected();
	}

	/**
	 * Export in PICT format?
	 * 
	 * @return TRUE if PICT is required, FALSE otherwise
	 */
	public boolean isPICT() {
		return chkBoxPICT.isSelected();
	}

	/**
	 * Measure the TestRatio?
	 * 
	 * @return TRUE if Test Ratio is required, FALSE otherwise
	 */
	public boolean isRatioTest() {
		return chkTestRatio.isSelected();
	}

	/**
	 * Measure the TupleRatio?
	 * 
	 * @return TRUE if Tuple Ratio is required, FALSE otherwise
	 */
	public boolean isRatioTuple() {
		return chkTupleRatio.isSelected();
	}

}
