package Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	
	void remove(int index) {
		routeList.remove(index);
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
			byte temp = 0x00000001;
			for(int j = 0; j < 8; j++) {
				if((byte)(temp & masked[i]) == 1)
					count++;
				else
					break;
				temp = (byte) (temp << 1);
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
				int tmpCount = countMatchAddr(masked);
				maxMatched = Math.max(maxMatched, tmpCount);
				foundRoute = route;
			}
		}
		return foundRoute;
	}
}
