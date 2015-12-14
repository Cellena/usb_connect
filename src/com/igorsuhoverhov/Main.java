package com.igorsuhoverhov;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Main {

    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static String adbPath = "C:\\Users\\Igor.Suhoverhov\\AppData\\Local\\Android\\sdk\\platform-tools\\adb";

    public static void main(String[] args) throws IOException {
        System.setProperty("file.encoding", "UTF8");
        try {
            //Runtime.getRuntime().exec("adb.exe forward tcp:59900 tcp:59900");
            //Runtime.getRuntime().exec("C:\\Users\\Igor.Suhoverhov\\AppData\\Local\\Android\\sdk\\platform-tools\\adb.exe forward tcp:59900 tcp:59900");
                System.out.println("Wait for device...");
                Runtime.getRuntime().exec(adbPath + " wait-for-device").waitFor();
                BufferedReader mDevices = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(adbPath + " devices").getInputStream()));
                String buffer = null;
                String device;
                boolean check = false;
                while ((buffer = mDevices.readLine()) != null) {

                    if (check) {
                        System.out.println(buffer);
                        device = buffer;
                    } else if (buffer.equals("List of devices attached")) check = true;
                }

                Runtime.getRuntime().exec(adbPath + " -s 4d008003402130cb forward tcp:59900 tcp:59900").waitFor();


                System.out.println("Wait for connect...");
                do {
                    socket = new Socket("127.0.0.1", 59900);
                    out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    buffer = in.readLine();
                    if (buffer != null) break;
                } while (buffer == null);
                System.out.println("Socket Created");

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        System.out.println("\nReading From Server");
                        try {

                            String buffer;
                            String check = null;
                            while ((buffer = in.readLine()) != null) {
                                System.out.println(buffer);
                                if (buffer.equals("soap")) {
                                    check = "soap";
                                } else if (buffer.equals("putSoap")) {
                                    check = "putSoap";
                                } else if (check == null) {
                                    out.println(excuteGet(buffer));
                                } else if (check.equals("soap")) {
                                    out.println(excuteSoap("http://10.35.38.42:81/meaweb/os/KMO_STATION_NSI", "'" + buffer + "'"));
                                    check = null;
                                } else if (check.equals("putSoap")) {
                                    out.println(putSoap("http://10.35.38.42:81/meaweb/ss/KMO", buffer));
                                    check = null;
                                }
                                out.println("buy");
                            }
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
        }
        catch (UnknownHostException e) {
            System.out.println("Socket connection problem (Unknown host)" + e.getStackTrace());
        } catch (IOException e) {
            System.out.println("Could not initialize I/O on socket " + e.getStackTrace());
        } catch (InterruptedException e) {
            e.printStackTrace();
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
            full = e.getMessage();
            e.printStackTrace();
        }
        return full;
    }
    public  static String putSoap (String mUrl, String params){
        String full = "";

            /*
            <max:kmocreateIncident xmlns:max="http://www.ibm.com/maximo">
                <max:jsonStringIncident>{}</max:jsonStringIncident>
            </max:kmocreateIncident>
             */

        try {
            URL u = new URL(mUrl);
            URLConnection uc = u.openConnection();
            HttpURLConnection connection = (HttpURLConnection) uc;

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept-Language", "ru-RU,en;q=0.5");
            connection.setRequestProperty("Accept-Encoding", "gzip,deflate");
            connection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
            connection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
            connection.setRequestProperty("SoapAction", "");
            //connection.setRequestProperty("SOAPAction", mUrl);

            OutputStream out = connection.getOutputStream();


           String req = "<max:kmocreateIncident xmlns:max=\"http://www.ibm.com/maximo\"><max:jsonStringIncident> "+ params +"</max:jsonStringIncident> </max:kmocreateIncident> ";
            System.out.println(req);

            //connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.write(req.getBytes("UTF-8"));
            wr.flush();
            wr.close();


            InputStream in = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                //System.out.println(inputLine);
                full += inputLine;
            }
            br.close();
            in.close();

            DocumentBuilder db = null;
            try {
                db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(full));

                Document doc = null;

                doc = db.parse(is);
                NodeList nodes = doc.getElementsByTagName("jsonResponse");
                Element element = (Element) nodes.item(0);
                full = getCharacterDataFromElement(element);
                //System.out.println("Title: " + full);

            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
                //full = e.getMessage();
            }

        }
        catch (IOException e) {
            System.err.println(e);
            //full = e.getMessage();
        }

        return full;
    }
    public  static String excuteSoap (String mUrl, String params){
        String full = "";

            /*
            <QueryKMO_STATION_NSI xmlns="http://www.ibm.com/maximo">
                <KMO_STATION_NSIQuery>
                    <WHERE>location in('STAN_20319','STAN_19891')</WHERE>
                </KMO_STATION_NSIQuery>
             </QueryKMO_STATION_NSI>
             */

        try {
            URL u = new URL(mUrl);
            URLConnection uc = u.openConnection();
            HttpURLConnection connection = (HttpURLConnection) uc;

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            //connection.setRequestProperty("SOAPAction", mUrl);
            connection.setRequestProperty("Accept-Language", "ru-RU,en;q=0.5");
            connection.setRequestProperty("Accept-Encoding", "gzip,deflate");
            connection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
            connection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
            connection.setRequestProperty("SoapAction", "");

            OutputStream out = connection.getOutputStream();
            Writer wout = new OutputStreamWriter(out);

            wout.write("<QueryKMO_STATION_NSI ");
            wout.write("xmlns=");
            wout.write(" \"http://www.ibm.com/maximo\" >");
            wout.write("<KMO_STATION_NSIQuery>");
            wout.write("<WHERE>location in(");
            wout.write(params);
            wout.write(")</WHERE>");
            wout.write("</KMO_STATION_NSIQuery>");
            wout.write("    </QueryKMO_STATION_NSI>");
            wout.flush();
            wout.close();

            InputStream in = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                //System.out.println(inputLine);
                full += inputLine;
            }
            br.close();
            in.close();

        }
        catch (IOException e) {
            System.err.println(e);
            full = e.getMessage();
        }

        DocumentBuilder db = null;
        try {
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(full));

        Document doc = null;

            doc = db.parse(is);
            NodeList nodes = doc.getElementsByTagName("KMO_STATION_NSISet");
            Element element = (Element) nodes.item(0);
            NodeList name = element.getElementsByTagName("STATION");
            element = (Element) name.item(0);
            name = element.getElementsByTagName("JSON");
            element = (Element) name.item(0);
            full = getCharacterDataFromElement(element);
            System.out.println("Title: " + full);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            //full = e.getMessage();
        }

        return full;
    }
    public static String getCharacterDataFromElement(Element e) {
        org.w3c.dom.Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
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