package benchmark.generator.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
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
	private JTextField txtPTest;
	private JTable tblTestCases;
	private JComboBox<String> benchmarkType;
	private JSplitPane splitView;
	private JSplitPane splitLeftView;
	private JPanel panelConfigurations;
	private JLabel lblBenchmarkType;
	private JLabel lblNMinParams;
	private JLabel lblRatio;
	private JLabel lblRatioTest;
	private JLabel lblEpsilonTest;
	private JLabel lblPTest;
	private JLabel lblMin;
	private JLabel lblMax;
	private JLabel lblLowerBoundInt;
	private JPanel panelTestSuite;
	private JPanel panelTests;
	private JButton btnExportAll;
	private JButton btnGenerate;
	private JLabel lblNumBenchmarks;
	private JLabel lblMinCardinality;
	private JCheckBox chkConstraintsBetweenParams;
	private JCheckBox chkForbiddenTuples;
	private JCheckBox chkCNF;
	private JCheckBox chkTestRatio;
	private JCheckBox chkTupleRatio;
	private JLabel lblMinConstraintComplexity;
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
	private JMenuItem btnDictionary;

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
		panelConfigurations.setLayout(new GridLayout(0, 3, 0, 0));

		lblBenchmarkType = new JLabel("Benchmark type");
		lblBenchmarkType.setFont(lblBenchmarkType.getFont().deriveFont(1));
		addToPanelConfigurations(lblBenchmarkType, "lblBenchmarkType");

		benchmarkType = new JComboBox<String>();
		fillBenchmarkTypes();
		addToPanelConfigurations(benchmarkType, "benchmarkType");
		
		lblPlaceHolder = new JLabel(EMPTY_TYPE);
		addToPanelConfigurations(lblPlaceHolder, "lblPlaceHolder");
		
		lblPlaceHolder = new JLabel(EMPTY_TYPE);
		addToPanelConfigurations(lblPlaceHolder, "lblPlaceHolder");
		
		lblMin = new JLabel("MIN");
		lblMin.setFont(lblMin.getFont().deriveFont(1));
		addToPanelConfigurations(lblMin, "lblMin");
		
		lblMax = new JLabel("MAX");
		lblMax.setFont(lblMax.getFont().deriveFont(1));
		addToPanelConfigurations(lblMax, "lblMax");

		lblNMinParams = new JLabel("N Params (k)");
		lblNMinParams.setFont(lblNMinParams.getFont().deriveFont(1));
		addToPanelConfigurations(lblNMinParams, "lblNMinParams");

		txtNMinParams = new JTextField();
		txtNMinParams.setColumns(5);
		addToPanelConfigurations(txtNMinParams, "txtNMinParams");

		txtNMaxParams = new JTextField();
		addToPanelConfigurations(txtNMaxParams, "txtNMaxParams");
		txtNMaxParams.setColumns(5);

		lblLowerBoundInt = new JLabel("Bounds for Ints (v)");
		lblLowerBoundInt.setFont(lblLowerBoundInt.getFont().deriveFont(1));
		addToPanelConfigurations(lblLowerBoundInt, "lblLowerBoundInt");

		txtLowerBoundInt = new JTextField();
		txtLowerBoundInt.setColumns(5);
		addToPanelConfigurations(txtLowerBoundInt, "txtLowerBoundInt");

		txtUpperBoundInt = new JTextField();
		txtUpperBoundInt.setColumns(5);
		addToPanelConfigurations(txtUpperBoundInt, "txtUpperBoundInt");

		lblMinCardinality = new JLabel("Param. Cardinality (v)");
		lblMinCardinality.setFont(lblMinCardinality.getFont().deriveFont(1));
		addToPanelConfigurations(lblMinCardinality, "lblMinCardinality");

		txtMinCardinality = new JTextField();
		txtMinCardinality.setColumns(5);
		addToPanelConfigurations(txtMinCardinality, "txtMinCardinality");

		txtMaxCardinality = new JTextField();
		txtMaxCardinality.setColumns(5);
		addToPanelConfigurations(txtMaxCardinality, "txtMaxCardinality");

		lblNMinConstraints = new JLabel("N Constraints (c)");
		lblNMinConstraints.setFont(lblNMinConstraints.getFont().deriveFont(1));
		addToPanelConfigurations(lblNMinConstraints, "lblNMinConstraints");

		txtNMinConstraints = new JTextField();
		txtNMinConstraints.setColumns(5);
		addToPanelConfigurations(txtNMinConstraints, "txtNMinConstraints");

		txtNMaxConstraints = new JTextField();
		txtNMaxConstraints.setColumns(5);
		addToPanelConfigurations(txtNMaxConstraints, "txtNMaxConstraints");

		lblMinConstraintComplexity = new JLabel("Constraints Complexity (d)");
		lblMinConstraintComplexity.setFont(lblMinConstraintComplexity.getFont().deriveFont(1));
		addToPanelConfigurations(lblMinConstraintComplexity, "lblMinConstraintComplexity");

		txtMinConstraintComplexity = new JTextField();
		txtMinConstraintComplexity.setColumns(5);
		addToPanelConfigurations(txtMinConstraintComplexity, "txtMinConstraintComplexity");

		txtMaxConstraintsComplexity = new JTextField();
		txtMaxConstraintsComplexity.setColumns(5);
		addToPanelConfigurations(txtMaxConstraintsComplexity, "txtMaxConstraintsComplexity");

		chkConstraintsBetweenParams = new JCheckBox("Constraints between params");
		chkConstraintsBetweenParams.setFont(chkConstraintsBetweenParams.getFont().deriveFont(1));
		addToPanelConfigurations(chkConstraintsBetweenParams, "chkConstraintsBetweenParams");

		lblPlaceHolder = new JLabel(EMPTY_TYPE);
		addToPanelConfigurations(lblPlaceHolder, "lblPlaceHolder");
		
		lblPlaceHolder = new JLabel(EMPTY_TYPE);
		addToPanelConfigurations(lblPlaceHolder, "lblPlaceHolder");

		chkForbiddenTuples = new JCheckBox("Only forbidden tuples");
		chkForbiddenTuples.setFont(chkForbiddenTuples.getFont().deriveFont(1));
		addToPanelConfigurations(chkForbiddenTuples, "chkForbiddenTuples");
		chkForbiddenTuples.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (chkCNF.isSelected())
					chkCNF.setSelected(!chkForbiddenTuples.isSelected());
			}
		});

		lblPlaceHolder = new JLabel(EMPTY_TYPE);
		addToPanelConfigurations(lblPlaceHolder, "lblPlaceHolder");
		
		lblPlaceHolder = new JLabel(EMPTY_TYPE);
		addToPanelConfigurations(lblPlaceHolder, "lblPlaceHolder");

		chkCNF = new JCheckBox("Only CNF");
		chkCNF.setFont(chkCNF.getFont().deriveFont(1));
		addToPanelConfigurations(chkCNF, "chkCNF");
		chkCNF.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (chkForbiddenTuples.isSelected())
					chkForbiddenTuples.setSelected(!chkCNF.isSelected());
			}
		});

		lblPlaceHolder = new JLabel(EMPTY_TYPE);
		addToPanelConfigurations(lblPlaceHolder, "lblPlaceHolder");
		
		lblPlaceHolder = new JLabel(EMPTY_TYPE);
		addToPanelConfigurations(lblPlaceHolder, "lblPlaceHolder");

		chkTupleRatio = new JCheckBox("Check Tuple Ratio");
		chkTupleRatio.setFont(chkTupleRatio.getFont().deriveFont(1));
		addToPanelConfigurations(chkTupleRatio, "chkTupleRatio");

		lblRatio = new JLabel("Max Tuple Ratio Accepted (R)");
		lblRatio.setFont(lblRatio.getFont().deriveFont(1));
		addToPanelConfigurations(lblRatio, "lblRatio");

		txtRatio = new JTextField();
		txtRatio.setColumns(5);
		addToPanelConfigurations(txtRatio, "txtRatio");

		chkTestRatio = new JCheckBox("Check Test Ratio");
		chkTestRatio.setFont(chkTestRatio.getFont().deriveFont(1));
		addToPanelConfigurations(chkTestRatio, "chkTestRatio");

		lblRatioTest = new JLabel("Max Test Ratio Accepted (tR)");
		lblRatioTest.setFont(lblRatioTest.getFont().deriveFont(1));
		addToPanelConfigurations(lblRatioTest, "lblRatioTest");

		txtRatioTest = new JTextField();
		txtRatioTest.setColumns(5);
		addToPanelConfigurations(txtRatioTest, "txtRatioTest");
		
		lblPlaceHolder = new JLabel(EMPTY_TYPE);
		addToPanelConfigurations(lblPlaceHolder, "lblPlaceHolder");

		lblPTest = new JLabel("Probability for Ratio");
		lblPTest.setFont(lblPTest.getFont().deriveFont(1));
		addToPanelConfigurations(lblPTest, "lblPTest");

		txtPTest = new JTextField();
		txtPTest.setColumns(5);
		addToPanelConfigurations(txtPTest, "txtPTest");
		
		lblPlaceHolder = new JLabel(EMPTY_TYPE);
		addToPanelConfigurations(lblPlaceHolder, "lblPlaceHolder");

		lblEpsilonTest = new JLabel("Error for Ratio");
		lblEpsilonTest.setFont(lblEpsilonTest.getFont().deriveFont(1));
		addToPanelConfigurations(lblEpsilonTest, "lblEpsilonTest");

		txtEpsilonTest = new JTextField();
		txtEpsilonTest.setColumns(5);
		addToPanelConfigurations(txtEpsilonTest, "txtEpsilonTest");

		chkTestRatio.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtRatioTest.setEnabled(chkTestRatio.isSelected());
				txtPTest.setEnabled(chkTestRatio.isSelected());
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
		lblNumBenchmarks.setFont(lblNumBenchmarks.getFont().deriveFont(1));
		addToPanelConfigurations(lblNumBenchmarks, "lblNumBenchmarks");

		txtNumBenchmarks = new JTextField("1");
		txtNumBenchmarks.setColumns(5);
		addToPanelConfigurations(txtNumBenchmarks, "txtNumBenchmarks");
		
		lblPlaceHolder = new JLabel(EMPTY_TYPE);
		addToPanelConfigurations(lblPlaceHolder, "lblPlaceHolder");

		btnGenerate = new JButton("Generate");
		addToPanelConfigurations(btnGenerate, "btnGenerate");
		
		lblPlaceHolder = new JLabel(EMPTY_TYPE);
		addToPanelConfigurations(lblPlaceHolder, "lblPlaceHolder");

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

		btnDictionary = new JMenuItem("Set dictionary");
		btnDictionary.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.addChoosableFileFilter(new FilesystemFilter("json", "JSON files"));
				int returnVal = fc.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					selectedFile = file.getAbsolutePath();
					// Set the dictionary
					try {
						BenchmarkGeneratorCLI.setDictionary(selectedFile);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
				}

			}
		});
		itemExtension.add(btnDictionary);

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
		String[] bnckTypes = { EMPTY_TYPE, UNIFORM_BOOLEAN, UNIFORM_ALL, MCA, BOOLC, MCAC, NUMC };
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
		txtPTest.setText(Double.toString(GeneratorConfiguration.P));
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
