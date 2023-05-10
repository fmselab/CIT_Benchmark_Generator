package benchmark.generator.handlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import benchmark.generator.gui.BenchmarkGenerator;

public class BenchmarksExporterHandler implements ActionListener {

	private BenchmarkGenerator parentFrame;
	private HashMap<String, Integer> componentsMap;

	/**
	 * Creates a new BenchmarksExporterHandler
	 * 
	 * @param frame the frame containing the components to be handled
	 */
	public BenchmarksExporterHandler(BenchmarkGenerator frame) {
		parentFrame = frame;
		componentsMap = parentFrame.getConfigurationComponents();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO: Export all benchmarks. Let the user choose the desired format
	}

}