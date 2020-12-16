package EventHandlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.table.DefaultTableModel;

import NetworkLayer.IPLayer;
import NetworkLayer.LayerManager;
import NetworkLayer.NILayer;
import View.ARPCachePanel;
import View.RouteAddFrame;

public class RouteAddEventHandlers implements EventHandlers {

	@Override
	public void setEventHandlers() {
		RouteAddFrame.btnAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				byte[] dest = Address.ip(RouteAddFrame.txtDestination.getText());
				byte[] netmask = Address.ip(RouteAddFrame.txtNetmask.getText());
				byte[] gateway = Address.ip(RouteAddFrame.txtGateway.getText());
				
				if(dest == null || netmask == null || gateway == null)
					return;
				boolean isUp = RouteAddFrame.chkUp.isSelected();
				boolean isGateway = RouteAddFrame.chkGateway.isSelected();
				boolean isHost = RouteAddFrame.chkHost.isSelected();			
	
				int flag = (isUp ? 4 : 0) | (isGateway? 2 : 0) | (isHost? 1 : 0);
				
				int _int = RouteAddFrame.cboInterface.getSelectedIndex();
				
				String[] data = new String[6];
				data[0] = RouteAddFrame.txtDestination.getText();
				data[1] = RouteAddFrame.txtNetmask.getText();
				data[2] = RouteAddFrame.txtGateway.getText(); 
				data[3] = (isUp ? "U" : "") + (isGateway ? "G" : "") + (isHost ? "H" : "");
				data[4] = Integer.toString(_int);
				data[5] = "1";
				
				IPLayer.router.add(dest, netmask, gateway, flag, _int, 1);
				RoutingTableEventHandlers.add(data);
			}
		});
	}
	
}
