import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Server extends JFrame{
	//initialise connection etc
	private static final long serialVersionUID = 1L;
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	//initialise menu's
	private JMenuBar menuBar;
	private JMenu file;
	
	//constructor
	public Server(){
		super("Tom's IM Server");
		initServer();
	}
	
	public final void initServer(){
		menuBar = new JMenuBar();
		file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);
		menuBar.add(file);
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
		initMenuItems();
		setJMenuBar(menuBar);
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(350, 500);
		setVisible(true);
	}
	
	public final void initMenuItems(){
		JMenuItem exitMI = new JMenuItem("Exit");
		exitMI.setMnemonic(KeyEvent.VK_C);
		exitMI.setToolTipText("Exit application");
		exitMI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
		file.add(exitMI);
		JMenuItem mb_f_newClient = new JMenuItem("New Client");
		mb_f_newClient.setMnemonic(KeyEvent.VK_N);
		mb_f_newClient.setToolTipText("Create new client chat window");
		mb_f_newClient.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	Client c = new Client("127.0.0.1");
				c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				c.startRunning();
            }
        });
		file.add(mb_f_newClient);
	}
	
	//set up and run the server
	public void startRunning(){
		try{
			server = new ServerSocket(6789, 100);
			while(true){
				try{
					waitForConnection();
					setupStreams();
					whileChatting();
				}catch(EOFException eofException){
					showMessage("\nServer ended the connection!");
				}finally{
					closeCrap();
				}
			}
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	//wait for connection, then display connection information
	private void waitForConnection() throws IOException{
		showMessage("Waiting for someone to conenct...");
		connection = server.accept();
		showMessage("Now connected to " + connection.getInetAddress().getHostName());
	}
	
	//get stream to send and receive data
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\nStreams are now setup! \n");
	}
	
	//during the chat conversation
	private void whileChatting() throws IOException{
		String message = "You are now connected! ";
		sendMessage(message);
		ableToType(true);
		do{
			//have a conversation
			try{
				message = (String)input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException classNotFoundEx){
				showMessage("\nI don't know what the user sent!");
			}
		}while(!message.equals("CLIENT - END"));{
			
		}
		
	}
	
	//close streams and sockets after you are done chatting
	private void closeCrap(){
		showMessage("\nClosing connections \n");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	private void sendMessage(String message){
		try{
			output.writeObject("SERVER - " + message);
			output.flush();
			showMessage("\nSERVER - " + message);
		}catch(IOException ioEx){
			chatWindow.append("\nERROR: Cannot send message!");
		}
	}
	
	//updates chatWindow
	private void showMessage(final String text){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					//append chat window content
					chatWindow.append(text);
				}
			}
		);
	}
	
	//sets whether the user can type or not
	
	private void ableToType(final boolean state){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					//set text field to editable
					userText.setEditable(state);
				}
			}
		);
	}
}
