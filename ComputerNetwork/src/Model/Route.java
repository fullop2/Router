package Model;

public class Route {
	public _IP_ADDR destination;
	public _IP_ADDR netMask;
	public _IP_ADDR gateway;
	public int flag;
	public int _interface;
	public int metric;
	
	Route(byte[] dest, byte[] netmask, byte[] gateway, int flag, int _interface, int metric){
		destination = new _IP_ADDR(dest);
		netMask = new _IP_ADDR(netmask);
		this.gateway = new _IP_ADDR(gateway);
		this.flag = flag;
		this._interface = _interface;
		this.metric = metric;
	}
}
