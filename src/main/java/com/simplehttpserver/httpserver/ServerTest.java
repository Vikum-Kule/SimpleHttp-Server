package com.simplehttpserver.httpserver;


import com.sun.net.httpserver.HttpHandler;

public class ServerTest {
    private static final String CONTEXT = "/app";
    private static final int PORT = 8000;

    public static void main(String[] args) throws Exception{
        SimpleHttpServer simpleHttpServer = new SimpleHttpServer(PORT, CONTEXT, new HttpRequestHandler());

        simpleHttpServer.start();
        System.out.println("Server is listening on port: "+PORT);
    }
}
