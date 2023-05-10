package benchmark.generator.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JOptionPane;
import models.Model;
import models.ModelList;

public class BenchmarksExporterHandler implements ActionListener {

	private BenchmarkGenerator parentFrame;

	/**
	 * Creates a new BenchmarksExporterHandler
	 * 
	 * @param frame the frame containing the components to be handled
	 */
	public BenchmarksExporterHandler(BenchmarkGenerator frame) {
		parentFrame = frame;
	}

	/**
	 * Export all models in the selected format
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		ModelList list = parentFrame.getModelList();
		for (Model m : list) {
			try {
				if (parentFrame.isACTS())
					// TODO FIXME: Fix
					m.exportACTS();
				if (parentFrame.isCTWedge())
					m.exportCTWedge();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		JOptionPane.showMessageDialog(null, "Benchmarks exported", "Operation completed",
				JOptionPane.INFORMATION_MESSAGE);
	}
}