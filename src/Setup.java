import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class Setup extends JFrame{
	private JButton serverButton;
	private JButton clientButton;
	private JLabel label;
	
	public Setup(){
		super("Tom's chooser:");
		serverButton = new JButton("Create server-side \n application");
		serverButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				Server s = new Server();
				s.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				s.startRunning();
				//dispose();
			}
			
		});
		clientButton = new JButton("Create client-side \n application");
		clientButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				Client c = new Client("127.0.0.1");
				c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				c.startRunning();
				//dispose();
			}
			
		});
		label = new JLabel("Choose which type of IM chat to open: ");
		getContentPane().setLayout(new BorderLayout());
		add(label, BorderLayout.NORTH);
		
		add(serverButton, BorderLayout.WEST);
		add(clientButton, BorderLayout.EAST);
		setSize(430, 200);
		
		setVisible(true);
	}
}
