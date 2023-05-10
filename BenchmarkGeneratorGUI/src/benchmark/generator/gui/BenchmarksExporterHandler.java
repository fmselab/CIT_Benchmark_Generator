package benchmark.generator.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JOptionPane;

import ctwedge.ctWedge.CitModel;
import ctwedge.generator.acts.ACTSTranslator;
import ctwedge.util.ext.Utility;
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
					exportACTS(m);
				if (parentFrame.isCTWedge())
					exportCTWedge(m);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		JOptionPane.showMessageDialog(null, 
                "Benchmarks exported", 
                "Operation completed", 
                JOptionPane.INFORMATION_MESSAGE);
	}

	private void exportCTWedge(Model m) throws IOException {
		FileWriter fo = new FileWriter(new File(m.getName() + ".ctw"));
		fo.write(m.toString());
		fo.close();
	}

	private void exportACTS(Model m) {
		CitModel ctwedgeModel = Utility.loadModel(m.toString());
		ACTSTranslator translator = new ACTSTranslator();
		translator.convertModel(ctwedgeModel, true, 2, ".");
	}

}