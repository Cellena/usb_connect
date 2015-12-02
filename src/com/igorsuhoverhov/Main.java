package com.igorsuhoverhov;

import java.io.*;
import java.net.*;

public class Main {

    private static Socket socket;

    public static void main(String[] args) throws IOException {
        try {
            Runtime.getRuntime().exec("C:\\Users\\iSuhar\\AppData\\Local\\Android\\sdk\\platform-tools\\adb.exe forward tcp:59900 tcp:59900");
            System.out.print("Send hello");
            socket = new Socket("127.0.0.1", 59900);

            System.out.println("Socket Created");
            final PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("hey");

            new Thread(new Runnable() {

                @Override
                public void run() {
                    System.out.println("\nReading From Server");
                    BufferedReader in = null;
                    try {
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        String buffer;
                        while ((buffer = in.readLine()) != null) {
                            System.out.println(buffer);
                            out.println(excuteGet(buffer));
                            out.println("buy");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();

            Thread closeSocketOnShutdown = new Thread() {
                public void run() {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            Runtime.getRuntime().addShutdownHook(closeSocketOnShutdown);
        }
        catch (UnknownHostException e) {
            System.out.println("Socket connection problem (Unknown host)"+e.getStackTrace());
        } catch (IOException e) {
            System.out.println("Could not initialize I/O on socket "+e.getStackTrace());
        }
    }
    public  static String excuteGet(String mUrl){
        URL yahoo = null;
        String inputLine = null;
        String full = "";
        try {
            yahoo = new URL(mUrl);

            URLConnection yc = yahoo.openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            yc.getInputStream()));


            while ((inputLine = in.readLine()) != null)
                full += inputLine;
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return full;
    }
    public static String excutePost(String targetURL, String urlParameters) {
        HttpURLConnection connection = null;
        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
            String line;
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }
}