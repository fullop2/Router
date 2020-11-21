package Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.jnetpcap.PcapIf;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import EventHandlers.ARPTableEventHandlers;
import EventHandlers.EventHandlers;
import EventHandlers.ProxyARPEventHandlers;
import EventHandlers.RouteAddEventHandlers;
import EventHandlers.RoutingTableEventHandlers;
import NetworkLayer.ARPLayer;
import NetworkLayer.EthernetLayer;
import NetworkLayer.IPLayer;
import NetworkLayer.LayerManager;
import NetworkLayer.NILayer;
import NetworkLayer.TestLayer;
import View.AppView;
import View.StaticRoutingTablePanel;

class RouterTest {

	static TestLayer[] testLayers = new TestLayer[2];
	
	@BeforeAll
	static void init() {
		new AppView();
		NILayer.SetAdapterList();
		for(int i = 0; i < 2; i++) {

			LayerManager layerManager = new LayerManager();
			testLayers[i] = new TestLayer("NI");
			TestLayer niLayer = testLayers[i];
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
				PcapIf pcapIf = NILayer.GetAdapterObject(i);
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
	@Test
	void test() {
		
		byte[] ping = {00,(byte)0x50,(byte)0x56,(byte)0xfa,(byte)0x47,(byte)0x11,(byte)0x00,(byte)0x0c,(byte)0x29,(byte)0x6b,(byte)0x29,(byte)0x10,(byte)0x08,(byte)0x00,(byte)0x45,(byte)0x00,
				00,(byte)0x3c,(byte)0xe2,(byte)0xbe,(byte)0x00,(byte)0x00,(byte)0x80,(byte)0x01,(byte)0xbd,(byte)0x2b,(byte)0xc0,(byte)0xa8,(byte)0x0a,(byte)0x81,(byte)0xc0,(byte)0xa8,
				(byte)0x0f,(byte)0x05,(byte)0x08,(byte)0x00,(byte)0x4d,(byte)0x06,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x55,(byte)0x61,(byte)0x62,(byte)0x63,(byte)0x64,(byte)0x65,(byte)0x66,
				67,(byte)0x68,(byte)0x69,(byte)0x6a,(byte)0x6b,(byte)0x6c,(byte)0x6d,(byte)0x6e,(byte)0x6f,(byte)0x70,(byte)0x71,(byte)0x72,(byte)0x73,(byte)0x74,(byte)0x75,(byte)0x76,
				77,(byte)0x61,(byte)0x62,(byte)0x63,(byte)0x64,(byte)0x65,(byte)0x66,(byte)0x67,(byte)0x68,(byte)0x69};


		
		byte[] dest = {(byte)192, (byte)168, 15, 0};
		byte[] netmask = {(byte)255, (byte)255, (byte)255, 0};
		byte[] gateway = {(byte)192, (byte)168, 15, 1};
		int flag = 6;
		int interfaceNumber = 1;
		int metric = 1;
		
		String[] data = {"192.168.15.0","255.255.255.0","192.168.15.1","6","1","1"};
		IPLayer.router.add(dest, netmask, gateway, flag, interfaceNumber, metric);
		((DefaultTableModel)StaticRoutingTablePanel.RoutingTable.getModel()).addRow(data);
		
		testLayers[0].Receive(ping);
		
	}

}
