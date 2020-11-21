package Model;

public class _ETHERNET_ADDR {
	public byte[] addr = new byte[6];

	public _ETHERNET_ADDR() {
		this.addr[0] = (byte) 0x00;
		this.addr[1] = (byte) 0x00;
		this.addr[2] = (byte) 0x00;
		this.addr[3] = (byte) 0x00;
		this.addr[4] = (byte) 0x00;
		this.addr[5] = (byte) 0x00;
	}
}
