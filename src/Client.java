import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame{
	//initialise connection etc
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	String changedHost;
	private Socket connection;
	private JMenuBar menuBar;
	//initialise menu's
	private JMenu file;
	private JMenu edit;
	//initialise menu items
	private JMenuItem mb_e_setHost;
	
	//constructor
	public Client(String host){
		super("Tom's IM Client");
		initClient(host);
	}
	
	public final void initClient(String host){
		menuBar = new JMenuBar();
		file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);
		edit = new JMenu("Edit");
		edit.setMnemonic(KeyEvent.VK_E);
		menuBar.add(file);
		menuBar.add(edit);
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event){
					sendMessage(event.getActionCommand());
					userText.setText("");
				}
			}
		);
		initMenuItems(host);
		setJMenuBar(menuBar);
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(350, 500);
		setVisible(true);
	}
	
	public final void initMenuItems(String host){
		JMenuItem exitMI = new JMenuItem("Exit");
		exitMI.setMnemonic(KeyEvent.VK_C);
		exitMI.setToolTipText("Exit application");
		exitMI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
		file.add(exitMI);
		mb_e_setHost = new JMenuItem("Set host IP");
		mb_e_setHost.setMnemonic(KeyEvent.VK_I);
		mb_e_setHost.setToolTipText("Set host IP address");
		/*mb_e_setHost.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                showMessage("\n...Setting IP address...");
            }
        });*/
		initSetIPActionListener(host);
		edit.add(mb_e_setHost);
	}
	
	public final void initSetIPActionListener(final String host){
		mb_e_setHost.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e)  
			{  
				class GetHostIP extends JDialog  
				{  
				  	JLabel setIPlbl = new JLabel("Enter new host IP address: ");
					JTextField tf = new JTextField(host, 30);  
					JButton submit = new JButton("Submit");
					public GetHostIP()  
					{  
						setModal(true);  //?
						setLocation(400,300);  //change??
						getContentPane().setLayout(new BorderLayout());
						getContentPane().add(setIPlbl, BorderLayout.NORTH);
						getContentPane().add(tf, BorderLayout.CENTER);    
						submit.addActionListener(new ActionListener(){  
							public void actionPerformed(ActionEvent e){  
								changedHost = tf.getText();  
							    dispose();
							}
						});  
						getContentPane().add(submit, BorderLayout.SOUTH);  
						pack();  
						setVisible(true);  
					}
				}
				new GetHostIP();
				serverIP = changedHost;
				try {
					connectToServer();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});  
	}
	
	//connect to server
	public void startRunning(){
		try{
			connectToServer();
			setupStreams();
			whileChatting();
		}catch(EOFException eofEx){
			showMessage("\nClient terminated connection");
		}catch(IOException ioEx){
			ioEx.printStackTrace();
		}finally{
			closeCrap();
		}
	}
	
	//connect to server
	private void connectToServer() throws IOException{
		if(serverIP!="127.0.0.1"){
			showMessage("\n");
		}
		showMessage("Attempting connection... \n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessage("Connected to: " + connection.getInetAddress().getHostName());
	}
	
	//setup streams to send and recieve messages
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\nYour streams are now connected \n");
		
	}
	
	//while chatting with server
	private void whileChatting() throws IOException{
		ableToType(true);
		do{
			try{
				message = (String)input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException classNotFoundEx){
				showMessage("\nI don't know that object type");
			}
		}while(!message.equals("SERVER - END"));
	}
	
	//close the streams and sockets
	private void closeCrap(){
		showMessage("\n...Closing down connection...");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioEx){
			ioEx.printStackTrace();
		}
	}
	
	//send messages to server
	private void sendMessage(String message){
		try{
			output.writeObject("CLIENT - " + message);
			output.flush();
			showMessage("\nCLIENT  - " + message);
		}catch(IOException ioEx){
			chatWindow.append("\nERROR: Something went wrong while sending message!");
		}
	}
	
	//update chatWindow
	private void showMessage(final String m){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					chatWindow.append(m);
				}
			}
		);
	}
	
	//gives user to type into the text field
	private void ableToType(final boolean state){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					userText.setEditable(state);
				}
			}
		);
	}
	
}




















