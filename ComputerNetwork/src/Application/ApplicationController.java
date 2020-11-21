package Application;

import NetworkLayer.ARPLayer;
import NetworkLayer.EthernetLayer;
import NetworkLayer.IPLayer;
import NetworkLayer.LayerManager;
import NetworkLayer.NILayer;
import View.AppView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jnetpcap.PcapAddr;
import org.jnetpcap.PcapIf;

import EventHandlers.*;


public class ApplicationController {
	
	
	public void init(){
		new AppView();
		for(int i = 0; i < 2; i++) {

			LayerManager layerManager = new LayerManager();
			
			NILayer niLayer = new NILayer("NI");
			EthernetLayer ethernetLayer = new EthernetLayer("Ethernet");
			ARPLayer arpLayer = new ARPLayer("ARP",i);
			IPLayer ipLayer = new IPLayer("IP");
			layerManager.AddLayer(niLayer);
			layerManager.AddLayer(ethernetLayer);
			layerManager.AddLayer(arpLayer);
			layerManager.AddLayer(ipLayer);
			layerManager.ConnectLayers("NI ( *Ethernet ( *IP  *ARP ( *IP ) ) )" );
			IPLayer.routingIPLayer.add(ipLayer);

			try {
				niLayer.SetAdapterList();
				PcapIf pcapIf = niLayer.GetAdapterObject(i);
				niLayer.SetAdapterNumber(i);
				byte[] addr = pcapIf.getHardwareAddress();
				ethernetLayer.setSrcEthernetAddress(addr);
				arpLayer.setMyEthernet(addr);
				byte[] ip = pcapIf.getAddresses().get(0).getAddr().getData();
				arpLayer.setMyIP(ip);
				ipLayer.setMyIP(ip);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// initialization event handler in here
		List<EventHandlers> listEventHandlers = new ArrayList<EventHandlers>();
		listEventHandlers.add(new ARPTableEventHandlers());
		listEventHandlers.add(new ProxyARPEventHandlers());
		listEventHandlers.add(new RoutingTableEventHandlers());
		listEventHandlers.add(new RouteAddEventHandlers());
		
		for(EventHandlers eventHandlers : listEventHandlers) {
			eventHandlers.setEventHandlers();
		}
	}
}

