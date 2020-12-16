package Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import EventHandlers.ARPTableEventHandlers;
import EventHandlers.RoutingTableEventHandlers;
import Model.ARP.ARPCache;
import View.StaticRoutingTablePanel;

public class Router {
	
	List<Route> routeList;
	
	public Router(){
		routeList = new ArrayList<Route>();
		
		byte[] zero = {0,0,0,0};
		byte[] gateway = {(byte) 192,(byte) 168,0,1};
		int flag = 6;
		int _int = -1;
		int met = 1;
		add(zero,zero,gateway,flag,_int, met);
	}
	
	public void add(byte[] dest, byte[] netmask, byte[] gateway, int flag, int _interface, int metric){
		routeList.add(new Route(dest,netmask, gateway, flag, _interface, metric));
	}
	
	public synchronized void remove(byte[] ip) {
		synchronized(routeList) {
			Iterator<Route> iter = routeList.iterator();
			int index = 0;
			while(iter.hasNext()) {
				Route route = iter.next();
				if(Arrays.equals(route.destination.addr,ip)) {
					iter.remove();
					RoutingTableEventHandlers.remove(index);
					return;
				}
				index++;
			}
		}
	}
	
	byte[] masking(byte[] addr, byte[] mask) {		
		byte[] masked = new byte[4];
		for(int i = 0; i < 4; i++)
			masked[i] = (byte) (addr[i] & mask[i]);
		return masked;
	}
	
	int countMatchAddr(byte[] masked) {
		int count = 0;
		for(int i = 0; i < 4; i++) {
			byte subMask = masked[i];
			for(int j = 0; j < 8; j++) {
				if((0x00000001 & subMask) == 1)
					count++;
				subMask = (byte) (subMask >> 1);
			}
		}
		return count;
	}
	
	public Route getRoute(byte[] addr) {
		Route foundRoute = null;
		int maxMatched = -1;
		for(Route route : routeList) {
			byte[] masked = masking(addr, route.netMask.addr);
			
			if(Arrays.equals(route.destination.addr, masked)) {
				int tmpCount = countMatchAddr(route.netMask.addr);
				maxMatched = Math.max(maxMatched, tmpCount);
				foundRoute = route;
			}
		}
		return foundRoute;
	}
}
