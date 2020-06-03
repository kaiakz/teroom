package connection;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

public class Client {
    private Socket connection;
    private Event event;

    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;

    public Client(Event event) {
        this.event = event;
        ConnectServer();

        try {
            dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataInputStream = new DataInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(new Receiver()).start();
    }

    public Client(Socket socket, Event event) {
        this.connection = socket;
        this.event = event;

        try {
            dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataInputStream = new DataInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(new Receiver()).start();
    }

    private void ConnectServer() {
        try {
            MulticastSocket ms = new MulticastSocket(8924);
            InetAddress recvAddr = InetAddress.getByName("230.0.0.1");
            ms.joinGroup(recvAddr);
            byte[] inBuff = new byte[1024];
            DatagramPacket inPacket = new DatagramPacket(inBuff, inBuff.length);
            String msg;
            while (true) {
                ms.receive(inPacket);
                msg = new String(inPacket.getData(), 0, inPacket.getLength()).trim();
                if(msg.equals("ThisIsServer")) {
                    System.out.println(inPacket.getAddress());
                    ms.close();
                    break;
                }
            }

            // Init socket & iostream
            connection = new Socket(inPacket.getAddress(), 8924);
//            reader = new BufferedReader(new InputStreamReader(server.getInputStream(), "UTF-8"));
//            writer = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void sendText(String text) throws IOException {
        dataOutputStream.writeUTF("MSG:TEXT");
        dataOutputStream.flush();
        dataOutputStream.writeUTF(text);
        dataOutputStream.flush();
    }



    class Receiver implements Runnable {
        @Override
        public void run() {
            try {
                String msg;
                while (true) {
                    msg=dataInputStream.readUTF();
                    if (msg.equals("MSG:TEXT")) {
                        String text = dataInputStream.readUTF();
                        event.onReceiveText(text);
                    } else if (msg.equals("MSG:FILE")) {

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        Client c = new Client(new Event() {
            @Override
            public void onReceiveText(String text) {
                System.out.println(text);
            }

            @Override
            public void OnReceiveFile() {

            }
        });

        try {
            c.sendText("Hello");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}