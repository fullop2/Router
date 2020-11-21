package NetworkLayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

import Model.ETHERNET;
import Model.Route;
import Model.Router;
import Model._IP_ADDR;

public class IPLayer implements BaseLayer {
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public List<BaseLayer> p_aUnderLayer = new ArrayList<BaseLayer>();
	public List<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	
	public static List<IPLayer> routingIPLayer = new ArrayList<IPLayer>();
	public static Router router = new Router();
	
	public _IP_ADDR myIP;
	
	@SuppressWarnings("unused")
	private class _IP_HEADER {
		byte VER;
		byte HLEN;
		byte service;
		byte[] totalLength;
		
		byte[] identification;
		byte flag;
		byte[] fragmentOffset;
		
		byte timeToLive;
		byte protocol;
		byte[] headerChecksum;
		
		_IP_ADDR ipDstAddr;
		_IP_ADDR ipSrcAddr;

		public _IP_HEADER() {
			VER = 0x04; 						// IPv4 : 		0000 0100			USE LSB 4
			HLEN = 0x05; 						// 5block : 	0000 0101			USE LSB 4
			service = 0x00;						// service : 	0000 0000
			totalLength = new byte[2];			
			
			identification = new byte[2];
			flag = 0x02;						// flag : 		0000 0010			USE LSB 3
			fragmentOffset = new byte[2];		// fragOff: 	0000 0000 0000 0000 USE LSB 13
			
			headerChecksum = new byte[2];		
			
			timeToLive = 0x7F; 					// timeToLive :	0111 1111			127 hop
			protocol = 0x06; 					// protocol	:	0000 0110			TCP 6
					
			this.ipDstAddr = new _IP_ADDR();
			this.ipSrcAddr = new _IP_ADDR();
		}
		
		public _IP_HEADER(byte[] header) {
			this();
			VER = (byte) ((header[0] >> 4 ) & 0x0f);
			HLEN = (byte) (header[0] & 0x0f);
			
			service = header[1];
			totalLength[0] = header[2]; 
			totalLength[1] = header[3];
			
			identification[0] = header[4];
			identification[1] = header[5];
			flag = (byte) ((header[6] >> 5 ) & 0x7);
			fragmentOffset[0] = (byte) (header[6] & 0x1f);
			fragmentOffset[1] = header[7];
			
			timeToLive = header[8];
			protocol = header[9];
			
			headerChecksum[0] = header[10];
			headerChecksum[1] = header[11];

			
			System.arraycopy(header, 16, ipDstAddr.addr, 0, 4);
			System.arraycopy(header, 12, ipSrcAddr.addr, 0, 4);
		}
		
		public void setTotalLength(int length) {
			assert(length <= 65535);
			totalLength[0] = (byte) ((length >> 8) & 0xFF);				
			totalLength[1] = (byte) (length & 0xFF);	
		}
		
		public int getTotalLength() {
			return (int)(totalLength[0] << 8) + (int)(totalLength[1]);
		}
		
		// 데이터 전송을 위한 헤더를 만드는 함수. 
		public byte[] makeHeader() {
			byte[] header = new byte[20];
			
			header[0] = (byte) (((VER << 4) & 0xf0) | ((HLEN) & 0x0f));
			header[1] = service;
			header[2] = totalLength[0]; 
			header[3] = totalLength[1];
			
			header[4] = identification[0];
			header[5] = identification[1];
			header[6] = (byte) (((flag << 5) & 0xe0) | ((fragmentOffset[0]) & 0x1f)); 
			header[7] = fragmentOffset[1];
			
			header[8] = timeToLive;
			header[9] = protocol;
			
			header[10] = headerChecksum[0];
			header[11] = headerChecksum[1];
			
			
			for(int i = 0; i < 4; i++) {
				header[16+i] = ipDstAddr.addr[i];
				header[12+i] = ipSrcAddr.addr[i];
			}
			return header;
		}
	}
	
//	private _IP_HEADER ipHeader = new _IP_HEADER();
	
	public IPLayer(String string) {
		pLayerName = string;
		myIP = new _IP_ADDR();
	}

	void setIPDstAddr(_IP_HEADER ipHeader, byte[] addr) {
		for(int i = 0; i < 4; i++)
			ipHeader.ipDstAddr.addr[i] = addr[i];
	}
	
	void setIPSrcAddr(_IP_HEADER ipHeader, byte[] addr) {
		for(int i = 0; i < 4; i++)
			ipHeader.ipSrcAddr.addr[i] = addr[i];
	}
	
	public void setMyIP(byte[] ip) {
		System.arraycopy(ip, 0, myIP.addr, 0, 4);
	}
	
	// 해당 ip가 내 ip인지 확인
	private boolean isMine(byte[] ip) {
		return Arrays.equals(myIP.addr, ip);
	}
	
	private boolean versionValid(byte versionLength) {
		return versionLength == 0x04;
	}
	private boolean lengthValid(byte versionLength) {
		return versionLength == 0x05;
	}
	
	@Override
	public synchronized boolean Receive(byte[] input) {

		if(input.length < 20)
			return false;
		
		_IP_HEADER receiveHeader = new _IP_HEADER(input);
		if(versionValid(receiveHeader.VER) && lengthValid(receiveHeader.HLEN)) {
			printIPInfo("IP RECV", receiveHeader.ipSrcAddr.addr, receiveHeader.ipDstAddr.addr);				
		}
		return false;
		
	}
	
	public static void printIPInfo(String msg, byte[] send, byte[] recv) {
		System.out.print(msg + " [ SRC : ");
		for(int i = 0; i < 3; i++)
			System.out.print(String.format("%d.", (int)(send[i] & 0xff)));
		System.out.print(String.format("%d", (int)(send[5] & 0xff)));
		System.out.print(", DST : ");
		for(int i = 0; i < 3; i++)
			System.out.print(String.format("%d.", (int)(recv[i] & 0xff)));
		System.out.print(String.format("%d", (int)(recv[5] & 0xff)));
		System.out.println("]");
	}
	
	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		// TODO Auto-generated method stub
		if (pUnderLayer == null)
			return;
		this.p_aUnderLayer.add(pUnderLayer);
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
		if (p_aUnderLayer.size() == 0)
			return null;
		return p_aUnderLayer.get(0);
	}
	
	@Override
	public BaseLayer GetUnderLayer(int index) {
		// TODO Auto-generated method stub
		if (p_aUnderLayer.size() == 0)
			return null;
		return p_aUnderLayer.get(index);
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
