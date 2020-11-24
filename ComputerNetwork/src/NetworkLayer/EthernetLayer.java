package NetworkLayer;

import java.util.ArrayList;
import java.util.Arrays;

import Model._ETHERNET_ADDR;

public class EthernetLayer implements BaseLayer {
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();

	private final byte[] BROADCAST_ETHERNET = {(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};
	
	@SuppressWarnings("unused")
	private class _ETHERNET_HEADER {
		_ETHERNET_ADDR enetDstAddr;
		_ETHERNET_ADDR enetSrcAddr;
		byte[] type;

		public _ETHERNET_HEADER() {
			this.enetDstAddr = new _ETHERNET_ADDR();
			this.enetSrcAddr = new _ETHERNET_ADDR();
			this.type = new byte[2];
		}
		
		public _ETHERNET_HEADER(byte[] header) {
			this();
			
			System.arraycopy(header, 0, enetDstAddr.addr, 0, 6);
			System.arraycopy(header, 6, enetSrcAddr.addr, 0, 6);
			type[0] = header[12];
			type[1] = header[13];
		}
		
		public byte[] makeHeader() {
			byte[] header = new byte[14];
			
			System.arraycopy(enetDstAddr.addr, 0, header, 0, 6);
			System.arraycopy(enetSrcAddr.addr, 0, header, 6, 6);
			header[12] = type[0];
			header[13] = type[1];
			
			return header;
		}
	}

	_ETHERNET_HEADER ethernetHeader = new _ETHERNET_HEADER();

	public EthernetLayer(String pName) {
		pLayerName = pName;
		
	}
	
	public void setDstEthernetAddress(byte[] ethernetAddress) {
		assert(ethernetAddress.length == 6);
		System.arraycopy(ethernetAddress, 0, ethernetHeader.enetDstAddr.addr, 0, 6);
	}

	public void setSrcEthernetAddress(byte[] ethernetAddress) {
		assert(ethernetAddress.length == 6);
		System.arraycopy(ethernetAddress, 0, ethernetHeader.enetSrcAddr.addr, 0, 6);
	}
	
	public void setEthernetType(byte[] type) {
		assert(type.length == 2);
		ethernetHeader.type[0] = type[0];
		ethernetHeader.type[1] = type[1];
	}
	
	private boolean isARP(byte[] type) {
		return type[0] == 0x08 && type[1] == 0x06;
	}
	
	private boolean isIP(byte[] type) {
		return type[0] == 0x08 && type[1] == 0x00;
	}
	private boolean isMine(byte[] addr) {
		return Arrays.equals(ethernetHeader.enetSrcAddr.addr, addr);
	}
	
	@Override
	public synchronized boolean Send(byte[] input, int length) {
		byte[] frame = new byte[14+length];
		byte[] header = ethernetHeader.makeHeader();
		
		System.arraycopy(header, 0, frame, 0, 14);
		System.arraycopy(input, 0, frame, 14, length);
		
//		System.out.println("SEND ETH");
		p_UnderLayer.Send(frame,frame.length);
		
		return true;
	}
	
	@Override
	public synchronized boolean Receive(byte[] input) {
		_ETHERNET_HEADER receiveHeader = new _ETHERNET_HEADER(input);
		
		
		if(!isMine(receiveHeader.enetSrcAddr.addr)) {
			byte[] data = new byte[input.length-14];
			System.arraycopy(input, 14, data, 0, input.length-14);
			if(isARP(receiveHeader.type)) {
//				System.out.println("RECV ETH");
//				printMACInfo("ETH RECV", receiveHeader.enetSrcAddr.addr, receiveHeader.enetDstAddr.addr);
				p_aUpperLayer.get(1).Receive(data);
			}
			else if(isIP(receiveHeader.type)) {
//				System.out.println("RECV ETH");
//				printMACInfo("ETH RECV", receiveHeader.enetSrcAddr.addr, receiveHeader.enetDstAddr.addr);
				p_aUpperLayer.get(0).Receive(data);
			}
		}
		return true;
	}

	public synchronized static void printMACInfo(String msg, byte[] send, byte[] recv) {
		System.out.print(msg+" [ SRC : ");
		for(int i = 0; i < 5; i++)
			System.out.print(String.format("%02X ", send[i] & 0xff));
		System.out.print(String.format("%02X", send[5] & 0xff));
		System.out.print(", DST : ");
		for(int i = 0; i < 5; i++)
			System.out.print(String.format("%02X ", recv[i] & 0xff));
		System.out.print(String.format("%02X", recv[5] & 0xff));
		System.out.println("]");
	}
	
	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		// TODO Auto-generated method stub
		if (pUnderLayer == null)
			return;
		this.p_UnderLayer = pUnderLayer;
	}

	@Override
	public void SetUpperLayer(BaseLayer pUpperLayer) {
		// TODO Auto-generated method stub
		if (pUpperLayer == null)
			return;
		this.p_aUpperLayer.add(nUpperLayerCount++, pUpperLayer);
		// nUpperLayerCount++;
	}

	@Override
	public String GetLayerName() {
		// TODO Auto-generated method stub
		return pLayerName;
	}

	@Override
	public BaseLayer GetUnderLayer() {
		// TODO Auto-generated method stub
		if (p_UnderLayer == null)
			return null;
		return p_UnderLayer;
	}

	@Override
	public BaseLayer GetUpperLayer(int nindex) {
		// TODO Auto-generated method stub
		if (nindex < 0 || nindex > nUpperLayerCount || nUpperLayerCount < 0)
			return null;
		return p_aUpperLayer.get(nindex);
	}

	@Override
	public void SetUpperUnderLayer(BaseLayer pUULayer) {
		this.SetUpperLayer(pUULayer);
		pUULayer.SetUnderLayer(this);

	}

}
