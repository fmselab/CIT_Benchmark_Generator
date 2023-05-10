package benchmark.generator.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;

public class TableClickListener implements MouseListener {

	private BenchmarkGenerator parentFrame;

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
			final String modelName = (String) jTable.getValueAt(row, column);
			parentFrame.getTestModel().setText(parentFrame.getModelList().getModelByName(modelName).toString());
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
