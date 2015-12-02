package com.igorsuhoverhov;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws IOException {

        Runtime.getRuntime().exec("C:\\Users\\Igor.Suhoverhov\\AppData\\Local\\Android\\sdk\\platform-tools\\adb.exe forward tcp:59900 tcp:59900");
        System.out.print("Send hello");
        Socket socket = new Socket("127.0.0.1", 59900);

        BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
        bos.write("hello".getBytes());
        bos.flush();
        bos.close();

        InputStreamReader reader = new InputStreamReader(socket.getInputStream());
        BufferedReader br = new BufferedReader(reader);

        while (!socket.isClosed()){
            String msg = br.readLine();
            if (msg != null )System.out.print(msg);
        }
    }

}