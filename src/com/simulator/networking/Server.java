package com.simulator.networking;
import java.io.*;
import java.net.*;
import java.util.function.Consumer;


import java.io.Serializable;

public class Server extends NetworkConnection {

    private int port = 0;

    public Server(int port, Consumer<Serializable> onReceiveCallback) {
        super(onReceiveCallback);
        this.port = port;
    }

    @Override
    protected boolean isServer() {
        return true;
    }

    @Override
    protected String getIP() {
        return null;
    }

    @Override
    protected int getPort() {
        return port;
    }
}