package Model;

public 	class _IP_ADDR {
	public byte[] addr = new byte[4];

	public _IP_ADDR() {
		this.addr[0] = (byte) 0x00;
		this.addr[1] = (byte) 0x00;
		this.addr[2] = (byte) 0x00;
		this.addr[3] = (byte) 0x00;
	}
	public _IP_ADDR(byte[] addr) {
		this.addr[0] = (byte) addr[0];
		this.addr[1] = (byte) addr[1];
		this.addr[2] = (byte) addr[2];
		this.addr[3] = (byte) addr[3];
	}
}