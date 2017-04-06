package com.networking.chat;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class ClientChat implements Runnable {
	private static final long serialVersionUID = 1L;

	private TextField txtMessage;
	private TextArea history;
	private Thread run, listen;
	private Client client;
	private boolean running = false;
	
	TextField tfSender, tfReceiver;
	TextArea taSender, taReceiver;
	
	
	
	boolean connect;
	public static volatile String name = "";
	public static volatile String address = "";
	public static volatile int port = 0;
	boolean disconected = true;
	
	private OnlineUsers users;

	
	/*
	 * OVDE SE SALJU BITOVI PROVERENO!!!!
	 * 
	 * 
	 */
	public ClientChat(String name, String address, int port) {
		client = new Client(name, address, port);
		name = client.getName();
		address = client.getAddress();
		port = client.getPort();
	}
	
	public void clientConnect(String ip, int port,String name){
		address = ip;
		this.name = name;
		this.port = port; 
		connect = client.openConnection(address);
		if (!connect) {
			System.err.println("Connection failed!");
			//console("Connection failed!");
		}else{
			disconected = false;
			System.out.println("Attempting a connection to " + address + ":" + port + ", user: " + name);
			String connection = "/c/" + name + "/e/";
			client.send(connection.getBytes());
			users = new OnlineUsers();
			running = true;
			run = new Thread(this, "Running");
			run.start();
		}
		
	}
	public void clientDisc(){
		if(disconected==false){
			client.close();
		}
	}
	public void run() {
		listen();
	}

	public void send(String message, boolean text) {
		if (message.equals("")) return;
		if (text) {
			message = client.getName() + ": " + message;
			message = "/m/" + message + "/e/";
			txtMessage.setText("");
			client.send(message.getBytes());
		}
		
	}
	public void sendTF(String message, TextField tf){
		tf.appendText(message);
		client.send(message.getBytes());
	}
	public void sendTA(String message, TextArea ta){
	//	ta.appendText(message);   //	---> Ima problem da appenduje text u objekat koji dolazi kao input parametar
		
		message = name + ": " + message;
		
		client.send(message.getBytes());
	//	System.out.println("CLIENT CHAT_____________________________________________________");
	}
	public void getFxComponents(TextArea taSender, TextArea taReceiver, TextField tfSender){
		this.taReceiver = taReceiver;
		this.taSender = taSender;
		this.tfSender = tfSender;
	}
	
	public void listen() {
		listen = new Thread("Listen") {
			public void run() {
				while (running) {
					String message = client.receive();
					
					taReceiver.appendText("Server: " + message+"\n");
					
					
				
				}
			}
		};
		listen.start();
	}

	public void console(String message) {
		
		history.appendText(message + "\n\r");

	}
}
