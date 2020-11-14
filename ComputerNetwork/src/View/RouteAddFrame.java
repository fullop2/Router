package View;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

public class RouteAddFrame extends JFrame{
	private static JLabel lblDestination;
	private static JLabel lblNetmask;
	private static JLabel lblGateway;
	private static JLabel lblFlg;
	private static JLabel lblInterface;
	
	public static JTextField txtDestination;
	public static JTextField txtNetmask;
	public static JTextField txtGateway;
	public static JCheckBox chkUp;
	public static JCheckBox chkGateway;
	public static JCheckBox chkHost;
	public static JComboBox cboInterface;
	
	public static JButton btnAdd;
	public static JButton btnCancel;
	
	public RouteAddFrame() {
		setTitle("Add Route");
		setResizable(false);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setLayout(null);
		setBounds(250, 250, 315, 245);
		
		lblDestination = new JLabel("Destination");
		lblDestination.setBounds(15,15,70,20);
		add(lblDestination);
		lblDestination = new JLabel("Netmask");
		lblDestination.setBounds(15,45,70,20);
		add(lblDestination);
		lblDestination = new JLabel("Gateway");
		lblDestination.setBounds(15,75,70,20);
		add(lblDestination);
		lblDestination = new JLabel("Flag");
		lblDestination.setBounds(15,105,70,20);
		add(lblDestination);
		lblDestination = new JLabel("Interface");
		lblDestination.setBounds(15,135,70,20);
		add(lblDestination);
		
		txtDestination = new JTextField();
		txtDestination.setBounds(105,15,180,20);
		add(txtDestination);
		txtNetmask = new JTextField();
		txtNetmask.setBounds(105,45,180,20);
		add(txtNetmask);
		txtGateway = new JTextField();
		txtGateway.setBounds(105,75,180,20);
		add(txtGateway);
		
		chkUp = new JCheckBox("UP");
		chkUp.setBounds(105,105,50,20);
		add(chkUp);
		chkGateway = new JCheckBox("Gateway");
		chkGateway.setBounds(155,105,80,20);
		add(chkGateway);
		chkHost = new JCheckBox("Host");
		chkHost.setBounds(235,105,80,20);
		add(chkHost);
		
		cboInterface = new JComboBox();
		cboInterface.setBounds(105,135,180,20);
		add(cboInterface);
		
		btnAdd = new JButton("Add");
		btnAdd.setBounds(25,175,100,20);
		add(btnAdd);
		btnCancel = new JButton("Cancel");
		btnCancel.setBounds(175,175,100,20);
		add(btnCancel);
		
		setCombobox();
	}
	
	private void setCombobox() {
		List<PcapIf> m_pAdapterList = new ArrayList<PcapIf>();
		StringBuilder errbuf = new StringBuilder();

		int r = Pcap.findAllDevs(m_pAdapterList, errbuf);
		if (r == Pcap.NOT_OK || m_pAdapterList.isEmpty()) {
			System.err.printf("Can't read list of devices, error is %s", errbuf.toString());
			return;
		}
		for (int i = 0; i < m_pAdapterList.size(); i++)
			this.cboInterface.addItem(m_pAdapterList.get(i).getDescription());
	}
}
