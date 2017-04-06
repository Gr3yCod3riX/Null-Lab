package com.simulator.networking;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.function.Consumer;

public abstract class NetworkConnection {

    public ConnectionThread connThread = new ConnectionThread();
    private Consumer<Serializable> onReceiveCallback;

    public NetworkConnection(Consumer<Serializable> onReceiveCallback) {
        this.onReceiveCallback = onReceiveCallback;
        connThread.setDaemon(true);
    }

    public void startConnection() throws Exception {
        connThread.start();
    }

    public void send(Serializable data) throws Exception {
        connThread.out.writeObject(data);
    }

    public void closeConnection() throws Exception {
        connThread.socket.close();
    }
    public void waitConnection()throws Exception{
    	connThread.wait();
    }

    protected abstract boolean isServer();
    protected abstract String getIP();
    protected abstract int getPort();

    public class ConnectionThread extends Thread {
        public Socket socket;
        public ObjectOutputStream out;
        ObjectInputStream in;
        boolean trRunning = true;
        @Override
        public void run() {
            try (ServerSocket server = isServer() ? new ServerSocket(getPort()) : null;
                    Socket socket = isServer() ? server.accept() : new Socket(getIP(), getPort());
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                this.socket = socket;
                this.out = out;
                socket.setTcpNoDelay(true);

                while (trRunning) {
                    Serializable data;
				
						data = (Serializable) in.readObject();
						onReceiveCallback.accept(data);
					
                    
                }
               
            }
            catch (Exception e) {
                onReceiveCallback.accept("Connection closed");
            }
        }
   
        
      
       
    }
    
}