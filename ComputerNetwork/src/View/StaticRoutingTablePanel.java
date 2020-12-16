package View;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class StaticRoutingTablePanel extends JPanel {
	
	public static JTable RoutingTable;
	
	public static JButton btnAddRoute;
	public static JButton btnDeleteRoute;
	
	public static JFrame routeAddFrame = new RouteAddFrame();
	
	private static String columnHeaders[] = {"Dest","NetMask","Gateway","Flag","Interface","Metric"};
	private static String data[][];
	
	public StaticRoutingTablePanel(int x, int y) {
		setBounds(x,y,500,400);
		setBorder(BorderFactory.createTitledBorder("Static Routing Table"));
		setLayout(null);
				
		RoutingTable = new UneditableJTable(data, columnHeaders);
		
		add(RoutingTable);
		
		((UneditableJTable)RoutingTable).updateColWidth(columnHeaders);
		
		JScrollPane scrollPane = new JScrollPane(RoutingTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(15,15,470,300);
		scrollPane.setVisible(true);
		add(scrollPane);
		
		btnAddRoute = new JButton("Add");
		btnAddRoute.setBounds(105,340,120,30);
		add(btnAddRoute);
		
		btnDeleteRoute = new JButton("Delete");
		btnDeleteRoute.setBounds(265,340,120,30);
		add(btnDeleteRoute);

	}

}
