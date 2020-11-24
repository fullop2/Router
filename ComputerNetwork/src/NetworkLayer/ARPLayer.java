package NetworkLayer;

import java.util.ArrayList;
import java.util.Arrays;

import Model.ARP;
import Model.ETHERNET;
import Model._ETHERNET_ADDR;
import Model._IP_ADDR;

public class ARPLayer implements BaseLayer {
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	public int index;
	
	// arp cache table
	public static ARP arp = new ARP();
	
	public _IP_ADDR myIP;
	public _ETHERNET_ADDR myETH;	
	


	_ARP_HEADER arpHeader = new _ARP_HEADER();
	
	
	public ARPLayer(String string, int interfaceNumber) {
		pLayerName = string;
		index = interfaceNumber;
		myIP = new _IP_ADDR();
		myETH = new _ETHERNET_ADDR();
	}
  	
	public void setMyIP(byte[] ip) {
		System.arraycopy(ip, 0, myIP.addr, 0, 4);
	}
	public void setMyEthernet(byte[] eth) {
		System.arraycopy(eth, 0, myETH.addr, 0, 6);
	}
	
	private boolean isRequest(byte[] opcode) {
		return (opcode[0] == (byte)0x00) && (opcode[1] == (byte)0x01);
	}
	private boolean isReply(byte[] opcode) {
		return (opcode[0] == (byte)0x00) && (opcode[1] == (byte)0x02);
	}
	
	private boolean isMine(byte[] ipAddr) {
		return Arrays.equals(myIP.addr, ipAddr);
	}
	private boolean isNIL(byte[] ethernetAddr){
		return Arrays.equals(ETHERNET.NIL, ethernetAddr);
	}
	
	private boolean isGARP(_ARP_HEADER header){
		return Arrays.equals(header.ipSenderAddr.addr, header.ipTargetAddr.addr) && isNIL(header.enetTargetAddr.addr);
	}
	
	private boolean needProxy(_ARP_HEADER header){
		return arp.hasIPInProxyTable(header.ipTargetAddr.addr) && isNIL(header.enetTargetAddr.addr);
	}
	
	public synchronized boolean Send(byte[] input, int length) {
		
		if(isMine(arpHeader.ipTargetAddr.addr)) {// 메세지 수신자가 나인 경우 아무것도 하지 않음
			return true;
		}
		arp.addARPCache(arpHeader.ipTargetAddr.addr, ETHERNET.NIL, index);

		
		byte[] header = arpHeader.makeHeader();
		p_UnderLayer.Send(header,header.length);
		
//		System.out.println("SEND ARP\n");
		return false;
	}
	public synchronized boolean Receive(byte[] input) {
		_ARP_HEADER receivedHeader = new _ARP_HEADER(input);
		
		if(isMine(receivedHeader.ipSenderAddr.addr)) {// 메세지 전송자가 나인 경우 아무것도 하지 않음
			return true;
		}
		System.out.println("RECV ARP");
		arp.addARPCache(receivedHeader.ipSenderAddr.addr, receivedHeader.enetSenderAddr.addr, index);
		
		if(isRequest(receivedHeader.opcode)) {
			
			
			EthernetLayer ethernetLayer = ((EthernetLayer)p_UnderLayer);
			ethernetLayer.setDstEthernetAddress(receivedHeader.enetSenderAddr.addr);
			ethernetLayer.setEthernetType(ETHERNET.PROT_ARP);
			
			byte[] targetIP = new byte[4];
			byte[] targetETH = new byte[6];
			
			System.arraycopy(receivedHeader.ipTargetAddr.addr, 0, targetIP, 0, 4);
			System.arraycopy(receivedHeader.enetTargetAddr.addr, 0, targetETH, 0, 6);
			
			System.arraycopy(receivedHeader.ipSenderAddr.addr, 0, receivedHeader.ipTargetAddr.addr, 0, 4);
			System.arraycopy(receivedHeader.enetSenderAddr.addr, 0, receivedHeader.enetTargetAddr.addr, 0, 6);
			
			System.arraycopy(targetIP, 0, receivedHeader.ipSenderAddr.addr, 0, 4);
			System.arraycopy(myETH.addr, 0, receivedHeader.enetSenderAddr.addr, 0, 6);
			
			receivedHeader.opcode[1] = 2;
			
			byte[] header = receivedHeader.makeHeader();
			
			
			p_UnderLayer.Send(header, header.length);
		}
		
		
//		System.out.println("RECV ARP\n");
		
		return true;
	}
	
	
	public void setOpcode(byte[] opcode) {
		assert(opcode.length == 2);
		arpHeader.opcode[0] = opcode[0];
		arpHeader.opcode[1] = opcode[1];
	}
	
	public void setEthernetSenderAddress(byte[] ethernetAddress) {
		assert(ethernetAddress.length == 6);
		for(int i = 0; i < 6; i++)
			arpHeader.enetSenderAddr.addr[i] = ethernetAddress[i];
	}
	
	public void setIPSenderAddress(byte[] ipAddress) {
		assert(ipAddress.length == 4);
		for(int i = 0; i < 4; i++)
			arpHeader.ipSenderAddr.addr[i] = ipAddress[i];
	}
	
	public void setEthernetTargetAddress(byte[] ethernetAddress) {
		assert(ethernetAddress.length == 6);
		for(int i = 0; i < 6; i++)
			arpHeader.enetTargetAddr.addr[i] = ethernetAddress[i];
	}
	
	public void setIPTargetAddress(byte[] ipAddress) {
		assert(ipAddress.length == 4);
		for(int i = 0; i < 4; i++)
			arpHeader.ipTargetAddr.addr[i] = ipAddress[i];
	}

	@SuppressWarnings("unused")
	private class _ARP_HEADER {
		byte[] hardwareType;
		byte[] protocolType;		
		byte hardwareSize;
		byte protocolSize;
		byte[] opcode;
		_ETHERNET_ADDR enetSenderAddr;
		_IP_ADDR ipSenderAddr;
		_ETHERNET_ADDR enetTargetAddr;
		_IP_ADDR ipTargetAddr;
		
		final int headerSize = 28;
		
		public _ARP_HEADER() {
			hardwareType = new byte[2];
			hardwareType[0] = 0x00; hardwareType[1] = 0x01;
			protocolType = new byte[2];
			protocolType[0] = 0x08; protocolType[1] = 0x00;
			hardwareSize = 0x06;
			protocolSize = 0x04;
			opcode = new byte[2];
			opcode[0] = 0x00; opcode[1] = 0x01;
			enetSenderAddr = new _ETHERNET_ADDR();
			ipSenderAddr = new _IP_ADDR();
			enetTargetAddr = new _ETHERNET_ADDR();
			ipTargetAddr = new _IP_ADDR();
		}
		
		public _ARP_HEADER(byte[] header) {
			hardwareType = new byte[2];
			hardwareType[0] = header[0]; hardwareType[1] = header[1];
			protocolType = new byte[2];
			protocolType[0] = header[2]; protocolType[1] = header[3];
			hardwareSize = header[4];
			protocolSize = header[5];
			opcode = new byte[2];
			opcode[0] = header[6]; opcode[1] = header[7];
			enetSenderAddr = new _ETHERNET_ADDR();
			ipSenderAddr = new _IP_ADDR();
			enetTargetAddr = new _ETHERNET_ADDR();
			ipTargetAddr = new _IP_ADDR();
			
			System.arraycopy(header, 8, enetSenderAddr.addr, 0, 6);
			System.arraycopy(header, 18, enetTargetAddr.addr, 0, 6);

			System.arraycopy(header, 14, ipSenderAddr.addr, 0, 4);
			System.arraycopy(header, 24, ipTargetAddr.addr, 0, 4);

		}
		
		public byte[] makeHeader() {
			byte[] header = new byte[headerSize];
			header[0] = hardwareType[0]; 
			header[1] = hardwareType[1];
			header[2] = protocolType[0];
			header[3] = protocolType[1];
			header[4] = hardwareSize;
			header[5] = protocolSize;
			header[6] = opcode[0];
			header[7] = opcode[1];
			
			System.arraycopy(enetSenderAddr.addr, 0, header, 8, 6);
			System.arraycopy(enetTargetAddr.addr, 0, header, 18, 6);

			System.arraycopy(ipSenderAddr.addr, 0, header, 14, 4);
			System.arraycopy(ipTargetAddr.addr, 0, header, 24, 4);
			
			return header;
		}
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
