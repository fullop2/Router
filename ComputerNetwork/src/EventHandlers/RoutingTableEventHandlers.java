package EventHandlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import NetworkLayer.ARPLayer;
import NetworkLayer.IPLayer;
import NetworkLayer.LayerManager;
import View.ARPCachePanel;
import View.StaticRoutingTablePanel;

public class RoutingTableEventHandlers implements EventHandlers {

	@Override
	public void setEventHandlers() {
		StaticRoutingTablePanel.btnAddRoute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				StaticRoutingTablePanel.routeAddFrame.setVisible(!StaticRoutingTablePanel.routeAddFrame.isVisible());
			}
		
		});
		
		StaticRoutingTablePanel.btnDeleteRoute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				int row = StaticRoutingTablePanel.RoutingTable.getSelectedRow();
				if(row < 0) return;
				String data = StaticRoutingTablePanel.RoutingTable.getValueAt(row, 0).toString();
				if(data == null) return;
				
				IPLayer.router.remove(Address.ip(data));
				
			}
		
		});
	}
	
	public synchronized static void add(String[] data){
		StaticRoutingTablePanel.btnAddRoute.setEnabled(false);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				((DefaultTableModel)StaticRoutingTablePanel.RoutingTable.getModel()).addRow(data);		
				StaticRoutingTablePanel.btnAddRoute.setEnabled(true);
			}
		});
		
	}
	
	public synchronized static void remove(int index) {
		StaticRoutingTablePanel.btnDeleteRoute.setEnabled(false);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				((DefaultTableModel)StaticRoutingTablePanel.RoutingTable.getModel()).removeRow(index-1);
				StaticRoutingTablePanel.btnDeleteRoute.setEnabled(true);
			}
		});
	}
}
