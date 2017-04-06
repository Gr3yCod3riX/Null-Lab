package com.simulator.networking;

import java.net.Socket;
import java.util.function.Consumer;
import java.io.*;
public class Client extends NetworkConnection {

    public String ip;
    public int port;

    public Client(String ip, int port, Consumer<Serializable> onReceiveCallback) {
        super(onReceiveCallback);
        this.ip = ip;
        this.port = port;
    }

    @Override
    protected boolean isServer() {
        return false;
    }

    @Override
    protected String getIP() {
        return ip;
    }

    @Override
    protected int getPort() {
        return port;
    }
    public void setIp(String ipAdd){
    	ip = ipAdd;
    }
    public void setPort(int portNum){
    	port = portNum;
    }
}
