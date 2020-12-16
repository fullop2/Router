package Model;

public class ETHERNET {
	public final static byte[] NIL = {0,0,0,0,0,0};
	public final static byte[] BROADCAST = {(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255};
	public final static byte[] HW_TYPE = {0x00, 0x01};
	public final static byte[] PROT_IP = {0x08,0x00};
	public final static byte[] PROT_ARP = {0x08,0x06};
}
