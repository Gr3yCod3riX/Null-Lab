package com.networking.chat.server;

public class ServerMain {

	private int port;
	private Server server;

	public ServerMain(int port) {
		this.port = port;
		server = new Server(port);
	}
	
	public Server getServer(){
		return server;
	}
	public void stopServer(){
		server.stopServer();
	}



}
