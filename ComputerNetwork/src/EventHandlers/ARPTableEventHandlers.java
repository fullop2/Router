package EventHandlers;

import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.table.DefaultTableModel;

import NetworkLayer.ARPLayer;
import NetworkLayer.IPLayer;
import NetworkLayer.LayerManager;
import NetworkLayer.NILayer;
import View.ARPCachePanel;
import View.StaticRoutingTablePanel;

public class ARPTableEventHandlers implements EventHandlers {
	private static DefaultTableModel ArpTableModel;
		
	@Override
	public void setEventHandlers() {
	
		ARPCachePanel.btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int row = ARPCachePanel.table.getSelectedRow();
				if(row < 0) return;
				String data = ARPCachePanel.table.getValueAt(row, 0).toString();
				if(data == null) return;
								
				ARPLayer.arp.deleteARPCache(Address.ip(data));
			}
		});
	}

	public static void updateARPTable(String[] stringData) {
		DefaultTableModel dm = ((DefaultTableModel)ARPCachePanel.table.getModel());
		int rc = dm.getRowCount();
		for(int i = rc-1; i >=0; i--) {
			dm.removeRow(i);
		}
		for(String str : stringData) {
			String data[] = str.split(" ");
			data[2] = NILayer.GetAdapterObject(Integer.parseInt(data[2])).getDescription();
			dm.addRow(data);
		}
	}
}
