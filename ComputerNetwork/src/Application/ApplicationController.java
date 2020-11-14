package Application;

import NetworkLayer.ARPLayer;
import NetworkLayer.EthernetLayer;
import NetworkLayer.IPLayer;
import NetworkLayer.LayerManager;
import NetworkLayer.NILayer;
import NetworkLayer.TCPLayer;
import View.AppView;

import java.util.ArrayList;
import java.util.List;

import EventHandlers.*;


public class ApplicationController {
	
	
	public void init() {
		new AppView();
		
		LayerManager layerManager = new LayerManager();
		layerManager.AddLayer(new NILayer("NI"));
		layerManager.AddLayer(new EthernetLayer("Ethernet"));
		layerManager.AddLayer(new ARPLayer("ARP"));
		layerManager.AddLayer(new IPLayer("IP"));
		layerManager.AddLayer(new TCPLayer("TCP"));
		layerManager.ConnectLayers("NI ( *Ethernet ( *IP ( *TCP ) *ARP ( *IP ) ) )" );
		
		
		// initialization event handler in here
		List<EventHandlers> listEventHandlers = new ArrayList<EventHandlers>();
		listEventHandlers.add(new ARPTableEventHandlers());
		listEventHandlers.add(new ProxyARPEventHandlers());
		listEventHandlers.add(new RoutingTableEventHandlers());
		
		for(EventHandlers eventHandlers : listEventHandlers) {
			eventHandlers.setEventHandlers(layerManager);
		}
		
		((NILayer)layerManager.GetLayer("NI")).SetAdapterList();
		((NILayer)layerManager.GetLayer("NI")).SetAdapterNumber(2);
	}
}

