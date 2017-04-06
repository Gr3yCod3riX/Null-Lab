package com.networking.chat.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.networking.*;


import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
public class Server implements Runnable{

	private List<ServerClient> clients = new ArrayList<ServerClient>();
	private List<Integer> clientResponse = new ArrayList<Integer>();

	boolean go = false;
	
	private DatagramSocket socket;
	private int port;
	private boolean running = false;
	private Thread run, manage, send, receive;
	private final int MAX_ATTEMPTS = 5;

	private boolean raw = false;

	public Server(int port) {
		this.port = port;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			return;
		}
		run = new Thread(this, "Server");
		run.start();
	}
	public void startServer(){
		run.start();
	}
	
	
	
	//THIS IS HOW YOU STOP A SERVER
	//**To close a server, socket must be stopped then the server
	public void stopServer(){
		quit();
		socket.close();
		run.stop();
	}
	
	boolean signal = false;
	public void run() {
		/*
		 * Ovde se parsiraju poruke koje SERVER SALJE
		 * 
		 */
		
		running = true;
		System.out.println("Server started on port " + port);
		manageClients();
		receive();
	
		while (running) {
			String text = tfS.getText().toString();
			if (go) {
				if (!text.startsWith("/")) {
					sendToAll("/m/Server: " + text + "/e/");
					continue;
				}
				text = text.substring(1);
				if (text.equals("raw")) {
					if (raw) System.out.println("Raw mode off.");
					else System.out.println("Raw mode on.");
					raw = !raw;
				} else if (text.equals("clients")) {
					System.out.println("Clients:");
					System.out.println("========");
					for (int i = 0; i < clients.size(); i++) {
						ServerClient c = clients.get(i);
						System.out.println(c.name + "(" + c.getID() + "): " + c.address.toString() + ":" + c.port);
					}
					System.out.println("========");
				} else if (text.startsWith("kick")) {
					String name = text.split(" ")[1];
					int id = -1;
					boolean number = true;
					try {
						id = Integer.parseInt(name);
					} catch (NumberFormatException e) {
						number = false;
					}
					if (number) {
						boolean exists = false;
						for (int i = 0; i < clients.size(); i++) {
							if (clients.get(i).getID() == id) {
								exists = true;
								break;
							}
						}
						if (exists) disconnect(id, true);
						else System.out.println("Client " + id + " doesn't exist! Check ID number.");
					} else {
						for (int i = 0; i < clients.size(); i++) {
							ServerClient c = clients.get(i);
							if (name.equals(c.name)) {
								disconnect(c.getID(), true);
								break;
							}
						}
					}
				} else if (text.equals("help")) {
					//printHelp();
				} else if (text.equals("quit")) {
					quit();
				} else {
					System.out.println("Unknown command.");
					//printHelp();
				}
			}
			go = false;
			
			
			}
		
	}

	public void manageClients() {
		manage = new Thread("Manage") {
			public void run() {
				while (running) {
//					sendToAll("/i/server");  // THIS SHIT WORKS
//					sendStatus();
					try {
						Thread.sleep(1100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for (int i = 0; i < clients.size(); i++) {
						ServerClient c = clients.get(i);
						if (!clientResponse.contains(c.getID())) {
							if (c.attempt >= MAX_ATTEMPTS) {
								disconnect(c.getID(), false);
							} else {
								c.attempt++;
							}
						} else {
							clientResponse.remove(new Integer(c.getID()));
							c.attempt = 0;
						}
					}
				}
			}
		};
		manage.start();
	}

	private void sendStatus() {
		
		
		/*
		 * OVA FUNKCIJA DOBRO FUNKCIONISE
		 */     // !PRIVERMENO ISKLJUCENA ZBOG NEPRAVILNOG FUNKCIONISANJA DODAVANJA KLIJENTA! //
		
		
		
//		if (clients.size() <= 0) return;
//		String users = "/u/";
//		for (int i = 0; i < clients.size() - 1; i++) {
//			users += clients.get(i).name + "/n/";
//		}
//		users += clients.get(clients.size() - 1).name + "/e/";
//		sendToAll(users);
	}

	private void receive() {
		receive = new Thread("Receive") {
			public void run() {
				while (running) {
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
					} catch (SocketException e) {
					} catch (IOException e) {
						e.printStackTrace();
					}
					process(packet);
				}
			}
		};
		receive.start();
	}

	public void sendToAll(String message) {
//		if (message.startsWith("/m/")) {
//			String text = message.substring(3);
//			text = text.split("/e/")[0];
//			System.out.println(message);
//		}
		for (int i = 0; i < clients.size(); i++) {
			System.out.println("Server usao " + i + " da svim klijentima posalje");
			ServerClient client = clients.get(i);
			send(message.getBytes(), client.address, client.port);
		}
	}

	public void send(final byte[] data, InetAddress address, final int port) {
		System.out.println("Server.send()");
		send = new Thread("Send") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
				try {
					System.out.println("Server.send()  -BEFORE");
					socket.send(packet);
					System.out.println("Server.send()-> Poslato KLIJENTU... proveri port, ip ili slicno  ako ne stize");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}
	DatagramPacket currentPacket;
	public void send(String message, boolean prihvati) {
		
		send(message.getBytes(), currentPacket.getAddress(), currentPacket.getPort());
		go = prihvati;
	
	}
	
//	public void send(String msg, InetAddress address, final int port){
//		
//	}

	
	/*
	 * 
	 * OVDE TREBA DA SE UMESTO U CONSOLE UPISUJE, TREBA ICI NA GUI
	 * 
	 * PROBLEM SOLVED
	 * 
	 */
	

	TextArea taR;
	TextField tfS;
	public void writeReceived(TextArea taReceiver, TextField tfSender, boolean youCanReceive){
		taR = taReceiver;
		tfS = tfSender;
		signal = youCanReceive;
	}
	
	private void process(DatagramPacket packet) { 
		/*
		 * Ovde se parsiraju poruke koje SERVER DOBIJE
		 */
		
		
		currentPacket = packet;  // Trenutni Datagrami koji se dobiju od klijenta
		String string = new String(packet.getData());
		
		if (raw) System.out.println(string);
		if (string.startsWith("/c/")) {
			// UUID id = UUID.randomUUID();
			int id = UniqueIdentifier.getIdentifier();
			String name = string.split("/c/|/e/")[1];
			System.out.println(name + "(" + id + ") connected!");
			clients.add(new ServerClient(name, packet.getAddress(), packet.getPort(), id));
			String ID = "/c/" + id;
			send(ID.getBytes(), packet.getAddress(), packet.getPort()); // ------------------- funkcija zaista salje nazad klijentu
		} else if (string.startsWith("/m/")) {
			sendToAll(string);
		} else if (string.startsWith("/d/")) {
			String id = string.split("/d/|/e/")[1];
			disconnect(Integer.parseInt(id), true);
		} else if (string.startsWith("/i/")) {
			clientResponse.add(Integer.parseInt(string.split("/i/|/e/")[1]));
		}
		else {
			
			
			taR.setPrefRowCount(2);
			taR.appendText(string+"\n"); // Server prihvata poruke
			
			
			System.out.println(string);
		}
	}

	private void quit() {
		for (int i = 0; i < clients.size(); i++) {
			disconnect(clients.get(i).getID(), true);
		}
		running = false;
		socket.close();
	}

	private void disconnect(int id, boolean status) {
		ServerClient c = null;
		boolean existed = false;
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).getID() == id) {
				c = clients.get(i);
				clients.remove(i);
				existed = true;
				break;
			}
		}
		if (!existed) return;
		String message = "";
		if (status) {
			message = "Client " + c.name + " (" + c.getID() + ") @ " + c.address.toString() + ":" + c.port + " disconnected.";
		} else {
			message = "Client " + c.name + " (" + c.getID() + ") @ " + c.address.toString() + ":" + c.port + " timed out.";
		}
		System.out.println(message);
	}

}
