package com.simplehttpserver.httpserver;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class SimpleHttpServer {
    private HttpServer httpServer;


    public SimpleHttpServer(int port, String context, HttpHandler handler){
        try {
            //to listning to the given port
            httpServer = HttpServer.create(new InetSocketAddress(port), 0);

            httpServer.createContext(context, handler);
            httpServer.setExecutor(null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        this.httpServer.start();
    }
}
