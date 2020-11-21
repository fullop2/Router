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
	/*
	 * 송신 전 헤더의 송신자 수신자 설정 필요
	 */
	public boolean Send(byte[] input, int length) {	
		
		
		if(!isMine(arpHeader.ipTargetAddr.addr)) {// 메세지 수신자가 나인 경우 : GARP
			arp.addARPCache(arpHeader.ipTargetAddr.addr, ETHERNET.NIL, index);
		}
		
		byte[] header = arpHeader.makeHeader();
		p_UnderLayer.Send(header,header.length);
		
		System.out.println("Send ARP request");
		ARP.printARPInfo("Sender", arpHeader.ipSenderAddr.addr, arpHeader.enetSenderAddr.addr);
		ARP.printARPInfo("Target", arpHeader.ipTargetAddr.addr, arpHeader.enetTargetAddr.addr);
		System.out.println();
		return false;
	}


	public synchronized boolean Receive(byte[] input) {
				
		_ARP_HEADER receivedHeader = new _ARP_HEADER(input);
		
		if(isMine(receivedHeader.ipSenderAddr.addr)) {// 메세지 전송자가 나인 경우 아무것도 하지 않음
			return true;
		}
		
		// 내가 보낸 ARP가 아니라면 ARP Table에 추가
		
		byte[] ip = new byte[4];
		System.arraycopy(receivedHeader.ipSenderAddr.addr, 0, ip, 0, 4);		
		byte[] eth = new byte[6];
		System.arraycopy(receivedHeader.enetSenderAddr.addr, 0, eth, 0, 6);
		
		
		arp.addARPCache(ip,eth,index);
		
		System.out.println("Receive ARP Request");
		ARP.printARPInfo("Sender", receivedHeader.ipSenderAddr.addr, receivedHeader.enetSenderAddr.addr);
		ARP.printARPInfo("Target", receivedHeader.ipTargetAddr.addr, receivedHeader.enetTargetAddr.addr);
		
		/*
		 * 1. ARP 요청이고
		 * 
		 * 1) 나에게 온 요청일 경우
		 * 2) 프록싱 가능할 경우
		 * 3) GARP인 경우
		 * 
		 * 내 맥을 넣어서 답장
		 */
		if(isRequest(receivedHeader.opcode) && 
				(isMine(receivedHeader.ipTargetAddr.addr) || needProxy(receivedHeader)|| isGARP(receivedHeader))
		   )
		{	
			receivedHeader.opcode[1] = 0x02; // make reply
			System.arraycopy(receivedHeader.enetTargetAddr.addr, 0, myETH.addr, 0, 6);
			
			/*
			 * 일반 ARP 요청은 목적지가 수신한 ARP 메세지에 존재하므로 건드리지 않아도 됨
			 * 하지만 GARP의 경우 Target과 Sender가 동일함. 따라서 현재 자신의 IP 정보를 넣어서 답장을 해줘야 한다
			 */
			if(Arrays.equals(receivedHeader.ipTargetAddr.addr,receivedHeader.ipSenderAddr.addr)) {
				System.out.println("[ TYPE : GARP Request]\n");
				System.arraycopy(receivedHeader.ipTargetAddr.addr, 0, myIP.addr, 0, 6);
			}
			else {
				System.out.println("[ TYPE : ARP Request]\n");
			}

			// swap sender and target
			_IP_ADDR ipSender = receivedHeader.ipSenderAddr;
			_ETHERNET_ADDR ethSender = receivedHeader.enetSenderAddr;
			
			receivedHeader.ipSenderAddr = receivedHeader.ipTargetAddr;
			receivedHeader.enetSenderAddr = receivedHeader.enetTargetAddr;
			
			receivedHeader.ipTargetAddr = ipSender;
			receivedHeader.enetTargetAddr = ethSender;
			
			/* HW Type을 ARP 프로토콜 정보에서 알 수 있다 
			 * 따라서 ARP Layer에서 하위 레이어의 정보에 접근 가능 
			 */
			if(Arrays.equals(ETHERNET.HW_TYPE,receivedHeader.hardwareType))  
				((EthernetLayer)p_UnderLayer).setDstEthernetAddress(receivedHeader.enetTargetAddr.addr);
			
			byte[] header = receivedHeader.makeHeader();
			p_UnderLayer.Send(header,header.length);	
			
			System.out.println("Send ARP Reply");
		}
		else { // 나에게 오지 않은 경우, 들어온 쪽을 제외한 나머지 어뎁터로 브로드캐스팅

			for(int i = 0; i < IPLayer.routingIPLayer.size();i++) {
				if(i == index) continue;
				IPLayer otherIPLayer = IPLayer.routingIPLayer.get(i);
				ARPLayer otherARPLayer = (ARPLayer)otherIPLayer.GetUnderLayer(1);
				
				otherARPLayer.arpHeader = receivedHeader;
				otherARPLayer.Send(null,0);
			}
		}
		
		

		
		
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
