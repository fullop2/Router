package EventHandlers;

import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import NetworkLayer.ARPLayer;
import NetworkLayer.IPLayer;
import NetworkLayer.LayerManager;
import NetworkLayer.NILayer;
import View.ARPCachePanel;
import View.StaticRoutingTablePanel;

public class ARPTableEventHandlers implements EventHandlers {
		
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

	public static synchronized void add(String[] data) {
		ARPCachePanel.btnDelete.setEnabled(false);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				((DefaultTableModel)ARPCachePanel.table.getModel()).addRow(data);
				ARPCachePanel.btnDelete.setEnabled(true);
			}
		});
	}
	
	public static synchronized void remove(int index) {
		ARPCachePanel.btnDelete.setEnabled(false);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				((DefaultTableModel)ARPCachePanel.table.getModel()).removeRow(index);
				ARPCachePanel.btnDelete.setEnabled(true);
			}
		});
		
	}
}
