package View;

import javax.swing.*;

public class AppView extends JFrame{
	
	public static JButton btnOpenNetwork;
	
	public AppView() {
		this(true);
	}	
	
	public AppView(boolean visible) {

		setTitle("Router");

		setBounds(250, 250, 920, 440);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(null);
		
		add(new StaticRoutingTablePanel(0, 0));
		add(new ARPCachePanel(500, 0));
		add(new ProxyARPPanel(500, 200));
		

		
		setVisible(visible);
	}
}
