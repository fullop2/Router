package EventHandlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.table.DefaultTableModel;

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
	}
	
	public static void updateARPTable(String[] stringData) {
//		DefaultTableModel dm = ((DefaultTableModel)StaticRoutingTablePanel.RoutingTable.getModel());
//		int rc = dm.getRowCount();
//		for(int i = rc-1; i >=0; i--) {
//			dm.removeRow(i);
//		}
//		for(String str : stringData) {
//			String data[] = str.split(" ");
//			data[2] = data[2].trim();
//			dm.addRow(data);
//		}
	}
	
	public static void add(String[] data) {
		((DefaultTableModel)StaticRoutingTablePanel.RoutingTable.getModel()).addRow(data);
	}
	
	public static void remove(int index) {
		((DefaultTableModel)StaticRoutingTablePanel.RoutingTable.getModel()).removeRow(index);
	}
}
