package View;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class UneditableJTable extends JTable {
	
	public UneditableJTable(Object[][] data, Object[] header) {
		super(new DefaultTableModel(data, header) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});
		updateColWidth(header);
	}
	
	public void updateColWidth(Object[] header) {
		TableColumnModel colModel = this.getColumnModel();
		for(int i = 0; i < header.length; i++) {
			String str = (String)header[i];
			if(str.equals("Interface") || str.equals("Metric")) {
				colModel.getColumn(i).setWidth(20);
			}
			else if(str.equals("Flag")) {
				colModel.getColumn(i).setWidth(30);
			}
		}
	}
	
}
