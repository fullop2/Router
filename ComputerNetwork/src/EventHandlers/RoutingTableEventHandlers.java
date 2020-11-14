package EventHandlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import NetworkLayer.LayerManager;
import View.StaticRoutingTablePanel;

public class RoutingTableEventHandlers implements EventHandlers {

	@Override
	public void setEventHandlers(LayerManager layerManager) {
		StaticRoutingTablePanel.btnAddRoute.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				StaticRoutingTablePanel.routeAddFrame.setVisible(!StaticRoutingTablePanel.routeAddFrame.isVisible());
			}
			
		});
	}

}
