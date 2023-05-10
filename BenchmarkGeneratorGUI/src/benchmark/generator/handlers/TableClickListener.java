package benchmark.generator.handlers;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;

import benchmark.generator.gui.BenchmarkGenerator;

public class TableClickListener implements MouseListener {

	private BenchmarkGenerator parentFrame;

	public TableClickListener(BenchmarkGenerator frame) {
		parentFrame = frame;
	}

	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 1) {
            final JTable jTable= (JTable)e.getSource();
            final int row = jTable.getSelectedRow();
            final int column = jTable.getSelectedColumn();
            final String modelName = (String)jTable.getValueAt(row, column);
            parentFrame.getTestModel().setText(parentFrame.getModelList().getModelByName(modelName).toString());
        }
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

}
