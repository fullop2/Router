package Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import EventHandlers.ARPTableEventHandlers;

public class ARP {
	private static List<ARPCache> arpCacheTable = new ArrayList<ARPCache>();
	public static HashMap<String,byte[]> proxyTable=new HashMap<String, byte[]>();
	
	
	public ARP() {
		new ARPTimer().start();
	}
	
	public class ARPCache{
		_IP_ADDR ip = new _IP_ADDR();
		_ETHERNET_ADDR ethernet = new _ETHERNET_ADDR();
		int interfaceNumber;
		int timeToLive;
		
		public ARPCache(byte[] ip, byte[] ethernet, int interfaceNumber) {
			setEthernet(ethernet);
			setIp(ip);
			setInterfaceNumber(interfaceNumber);
			if(ethernet == null || isNIL(ethernet))
				setTimeToLive(180000);
			else
				setTimeToLive(1200000);
		}
		public void setInterfaceNumber(int interfaceNumber) {
			this.interfaceNumber = interfaceNumber;
		}
		public void setTimeToLive(int milliSecond) {
			timeToLive = milliSecond;
		}
		public void setEthernet(byte[] ethernet) {
			if(ethernet== null)
				return;
			assert(ethernet.length==6);
			System.arraycopy(ethernet, 0, this.ethernet.addr, 0, 6);
		}
		
		public void setIp(byte[] ip) {
			assert(ip.length == 4);
			System.arraycopy(ip, 0, this.ip.addr, 0, 4);
		}
		
		public String toString() {
			
			StringBuffer stringBuffer = new StringBuffer();
			
			for(int i = 0; i < 3; i++)
				stringBuffer.append((int)(ip.addr[i] & 0xff)+".");
			stringBuffer.append((int)(ip.addr[3] & 0xff)+" ");
			if(Arrays.equals(ETHERNET.NIL, ethernet.addr)) {
				stringBuffer.append("???????????? ??? ");
			}
			else {
				for(int i = 0; i < 5; i++) {
					stringBuffer.append(String.format("%02X-", (ethernet.addr[i] & 0xff)).toUpperCase());
				}
				stringBuffer.append(String.format("%02X ", (ethernet.addr[5] & 0xff)).toUpperCase());
				stringBuffer.append(interfaceNumber+" ");
			}
			
			
			if(Arrays.equals(ETHERNET.NIL, ethernet.addr)) {
				stringBuffer.append("incompleted");
			}
			else {
				stringBuffer.append("completed");
			}
			
			return stringBuffer.toString();
		}
	}
	

	private boolean isNIL(byte[] ethernetAddr){
		return Arrays.equals(ETHERNET.NIL, ethernetAddr);
	}
	
	
	class ARPTimer extends Thread {
		
		private long beforeTime;
		
		ARPTimer(){
			beforeTime = System.currentTimeMillis();
		}
		@Override
		public void run() {
			while(true) {
				synchronized(arpCacheTable) {
					long timeElipse = System.currentTimeMillis() - beforeTime;
					beforeTime =  System.currentTimeMillis();
					int index = 0;
					Iterator<ARPCache> iter = arpCacheTable.iterator();
					while(iter.hasNext()) {
						ARPCache cache = iter.next();
						cache.timeToLive -= timeElipse;
						if(cache.timeToLive <= 0) {
							iter.remove();
							ARPTableEventHandlers.remove(index);
						}
						else {
							index++;
						}
					}
				}
				try {	
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}				
					
								
			}
		}
	}
	
	public boolean addARPCache(byte[] ipAddr, byte[] ethe, int interfaceNumber) {
		
		byte[] ip = new byte[4];
		System.arraycopy(ipAddr, 0, ip, 0, 4);
		
		byte[] ethernet = new byte[6];
		System.arraycopy(ethe, 0, ethernet, 0, 6);
		
		synchronized(arpCacheTable) {
			Iterator<ARPCache> iter = arpCacheTable.iterator();
			
			int index = 0;
			while(iter.hasNext()) {
				ARPCache cache = iter.next();
				if(Arrays.equals(cache.ip.addr,ip)) {
					//printARPInfo("Remove Cache", cache.ip.addr, cache.ethernet.addr);
					iter.remove();
					ARPTableEventHandlers.remove(index);
					break;
				}
				index++;
			}
			ARPCache currentCache = new ARPCache(ip,ethernet,interfaceNumber);
			arpCacheTable.add(currentCache);
			ARPTableEventHandlers.add(currentCache.toString().split(" "));
		}		
		return true;
	}

	public void deleteARPCache(byte[] ip) {
		synchronized(arpCacheTable) {
			Iterator<ARPCache> iter = arpCacheTable.iterator();
			int index = 0;
			while(iter.hasNext()) {
				ARPCache arpCache = iter.next();
				if(Arrays.equals(arpCache.ip.addr,ip)) {
					arpCacheTable.remove(arpCache);
					ARPTableEventHandlers.remove(index);
					return;
				}
				index++;
			}
		}
	}

	
	public byte[] getEthernet(byte[] ip) {
		synchronized(arpCacheTable) {
			if(ip != null && ip.length == 4)
				for(ARPCache arpCache : arpCacheTable) {
					if(Arrays.equals(arpCache.ip.addr,ip)) {
						return arpCache.ethernet.addr;
					}
				}
		}
		return null; // not exist
	}
	
	public int getInterface(byte[] ip) {
		synchronized(arpCacheTable) {
			if(ip != null && ip.length == 4)
				for(ARPCache arpCache : arpCacheTable) {
					if(Arrays.equals(arpCache.ip.addr,ip)) {
						if(Arrays.equals(arpCache.ethernet.addr, ETHERNET.NIL)) { // ARP Reply not received yet.
							return -2;
						}
						else {
							return arpCache.interfaceNumber;
						}
					}
					 
				}
		}
		return -1; // not exist
	}
		
	public synchronized static void printARPInfo(String who, byte[] ip, byte[] eth) {
		System.out.print(who + " : [ ETH : ");
		for(int i = 0; i < 5; i++)
			System.out.print(String.format("%02X ", eth[i] & 0xff));
		System.out.print(String.format("%02X", eth[5] & 0xff));
		System.out.print(", IP : ");
		for(int i = 0; i < 3; i++)
			System.out.print(String.format("%d.", (int)(ip[i] & 0xff)));
		System.out.print(String.format("%d", (int)(ip[3] & 0xff)));
		System.out.println("]");
	}
	
	public boolean hasIPInProxyTable(byte[] receiveIP) {
		for(byte[] address : proxyTable.values()) {
			byte[] ip = new byte[4];
			System.arraycopy(address, 0, ip, 0, 4);
			if(Arrays.equals(ip, receiveIP)) {
				System.out.println("proxy Table에 해당 IP 존재");
				return true;
			}
		}
		return false;
	}
	/*
	 * proxy ARP 저장
	 * author : Hyoin
	 * key : device, value: IP Addr + MAC Addr
	 */
	public void setProxyTable(String device,byte[] ipAddress, byte[] ethernetAddress){
		assert(ipAddress.length == 4);
		assert(ethernetAddress.length == 6);
		
		byte [] proxy=new byte[10];
		for(int i=0;i<4;i++){
			proxy[i]=ipAddress[i];
		}
		for(int i=4;i<10;i++){
			proxy[i]=ethernetAddress[i-4];
		}
		proxyTable.put(device,proxy);		
	}
	
	/*
	 * proxy ARP 삭제
	 * author : Hyoin
	 * key : device, value: IP Addr + MAC Addr
	 */
	public void removeProxyTable(String deviceaddr){
		proxyTable.remove(deviceaddr);
		System.out.println(proxyTable.size());
	}
}
