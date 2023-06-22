package benchmark.generator.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import models.Model;
import models.ModelList;

public class TableClickListener implements MouseListener {

	private BenchmarkGenerator parentFrame;
	private static final Logger LOGGER = LogManager.getLogger(BenchmarkGenerator.class);

	/**
	 * Builds a new TableClickListener
	 * 
	 * @param frame the frame to be listened
	 */
	public TableClickListener(BenchmarkGenerator frame) {
		parentFrame = frame;
	}

	/**
	 * When click on a row in the table showing all the generated benchmarks, show
	 * the text contained in each benchmark on the text area
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 1) {
			final JTable jTable = (JTable) e.getSource();
			final int row = jTable.getSelectedRow();
			final int column = jTable.getSelectedColumn();
			String modelName = (String) jTable.getValueAt(row, column);
			
			if(modelName.contains("("))
				modelName = modelName.split("\\(")[0];
			
			LOGGER.debug("Showing the model " + modelName);
			
			ModelList modelList = parentFrame.getModelList();
			Model model = modelList.getModelByName(modelName);
			parentFrame.getTestModel().setText(model.toString());
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

}
