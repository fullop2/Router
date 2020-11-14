package View;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

public class ARPCachePanel extends JPanel {

	public static JTable table;
	public static JButton btnDelete;
	
	private static String columnHeaders[] = {"IP Addr","Ethernet Addr","Interface","Flag"};
	private static String data[][];
	
	public ARPCachePanel(int x, int y) {
		setBounds(x,y,400,200);
		setBorder(BorderFactory.createTitledBorder("ARP Cache"));
		setLayout(null);
				
		table = new UneditableJTable(data, columnHeaders);
		add(table);
		
		JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(15,15,370,130);
		scrollPane.setVisible(true);
		add(scrollPane);
				
		btnDelete = new JButton("Delete");
		btnDelete.setBounds(140,150,120,30);
		add(btnDelete);

	}
}
