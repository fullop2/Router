package EventHandlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.table.DefaultTableModel;

import NetworkLayer.ARPLayer;
import NetworkLayer.LayerManager;
import View.ARPCachePanel;

public class ARPTableEventHandlers implements EventHandlers {
	private static DefaultTableModel ArpTableModel;
		
	@Override
	public void setEventHandlers(LayerManager layerManager) {
	
		ARPCachePanel.btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int row = ARPCachePanel.table.getSelectedRow();
				if(row < 0) return;
				String data = ARPCachePanel.table.getValueAt(row, 0).toString();
				if(data == null) return;
								
				ARPLayer arpLayer = ((ARPLayer)layerManager.GetLayer("ARP"));
				arpLayer.deleteARPCache(Address.ip(data));
			}
		});
	}

	public static void updateARPTable(String[] stringData) {
		ARPCachePanel.table.removeAll();
		for(String str : stringData) {
//			((DefaultTableModel)ARPCachePanel.table.getModel()).addRow(rowData);
		}
	}
}
