package Model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import Model.ARP;
import NetworkLayer.ARPLayer;
import NetworkLayer.EthernetLayer;
import NetworkLayer.IPLayer;

public class OutgoingQueue {

	class Packet{
		byte[] receiver;
		byte[] data;
		
		Packet(byte[] receiver, byte[] data){
			this.receiver = new byte[4];
			this.data = new byte[data.length];
			
			System.arraycopy(receiver, 0, this.receiver, 0, 4);
			System.arraycopy(data, 0, this.data, 0, data.length);
		}
	}
	List< Queue<Packet> > queueList;
	
	public OutgoingQueue(int interfaceCount) {
		queueList = new LinkedList<Queue<Packet> >();
		for(int i = 0; i < interfaceCount; i++)
			queueList.add(new LinkedList<Packet>());
		
		new WaitForVaildMac().start();
	}

	public synchronized boolean add(int interfaceNumber, byte[] receiverIP, byte[] data) {
		Packet packet = new Packet(receiverIP,data);
		return queueList.get(interfaceNumber).add(packet);
	}
	
	public class WaitForVaildMac extends Thread{
		public WaitForVaildMac() {
			setName("Thread-Wait-for-MAC");
		}
		
		public synchronized void run() {
			while(true) {
				Iterator< Queue<Packet> > it = queueList.iterator();
				while(it.hasNext()) {
					Queue<Packet> queue = it.next();
					
					Iterator<Packet> itq = queue.iterator();
					
					while(itq.hasNext()) {
						Packet packet = itq.next();
						int interfaceNumber = ARPLayer.arp.getInterface(packet.receiver);
						if(interfaceNumber >= 0) { // valid ethernet information
							EthernetLayer ethernetLayer = (EthernetLayer)IPLayer.routingIPLayer.get(interfaceNumber).GetUnderLayer(0);
							ethernetLayer.setDstEthernetAddress( ARPLayer.arp.getEthernet(packet.receiver));
							ethernetLayer.setEthernetType(ETHERNET.PROT_IP);
							ethernetLayer.Send(packet.data,packet.data.length);
							itq.remove();
						}
					}
				}
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public String ipToString(byte[] ipAddr) {
		return (ipAddr[0] & 0x000000ff) + "." + (ipAddr[1] & 0x000000ff) + "." + (ipAddr[2] & 0x000000ff) + "." + (ipAddr[3] & 0x000000ff);
	}
	public byte[] stringToIP(String str) {
		String[] addr = str.split("\\.");
		byte[] ip = new byte[4];
		for(int i = 0; i < 4; i++)
			ip[i] = (byte)Integer.parseInt(addr[i]);
		return ip;
	}
}
