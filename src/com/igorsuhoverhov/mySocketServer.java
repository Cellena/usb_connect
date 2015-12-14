package com.igorsuhoverhov;

import sun.rmi.runtime.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Created by Igor.Suhoverhov on 14.12.2015.
 */
public class mySocketServer {

    public static ServerSocket server;
    public static Socket socket;
    public static boolean ready = false;
    private static BufferedReader br;
    private static PrintWriter out;

    private int port;

    mySocketServer (int mPort){
        port = mPort;
    }

    public static String sendUrl (String url){
        String msg, fullMsg = "";
        System.out.println("Begin transferring...");
        try {
            out.println(url);
            while ((msg = br.readLine()) != null) {
                if (msg.equals("buy")) {
                    System.out.println("End transferring...");
                    return fullMsg;}
                else fullMsg += msg;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fullMsg;
    }

    public void startServer () {
        try {
            server = new ServerSocket(port);
            System.out.println("Begin session...");
            socket = server.accept();
            InputStreamReader reader = new InputStreamReader(socket.getInputStream());
            br = new BufferedReader(reader);
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")), true);
            String msg;
            //br.close();
            while ((msg = br.readLine()) !=null) {
                if (msg.equals("hey")){
                    ready = true;
                    System.out.println("Ready to transfer...");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean stopServer () {
        try {
            server.close();
            ready = false;
            if (server.isClosed()) {
                System.out.println("End session...");
                return true;}
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
