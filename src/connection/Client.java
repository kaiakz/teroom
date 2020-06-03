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
            dataOutputStream = new DataOutputStream(new BufferedOutputStream(connection.getOutputStream()));
            dataInputStream = new DataInputStream(new BufferedInputStream(connection.getInputStream()));
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

    public void Login(String name, String id) throws IOException {
        dataOutputStream.writeUTF("MSG:LOGIN");
        dataOutputStream.flush();
        dataOutputStream.writeUTF(name);
        dataOutputStream.flush();
        dataOutputStream.writeUTF(id);
        dataOutputStream.flush();
    }

    public void sendFile(String fname) throws IOException {
        dataOutputStream.writeUTF("MSG:FILE");
        dataOutputStream.flush();

        File f = new File(fname);

        dataOutputStream.writeUTF(f.getName());
        dataOutputStream.flush();

        dataOutputStream.writeLong(f.length());
        dataOutputStream.flush();
        
        FileInputStream fos = new FileInputStream(f);
        int len = 0;
        byte[] buf = new byte[4096];
        while ((len = fos.read(buf)) >= 0) {
            dataOutputStream.write(buf, 0, len);
            dataOutputStream.flush();
        }
        fos.close();
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
                        String fname = getFile();
                        event.onReceiveFile(fname);
                    } else if (msg.equals("MSG:LOGIN")) {
                        String name = dataInputStream.readUTF();
                        String id = dataInputStream.readUTF();
                        event.onLogin(name, id);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String getFile() throws IOException {
            String fname = dataInputStream.readUTF();
            long flen = dataInputStream.readLong();

            FileOutputStream fos = new FileOutputStream(new File("e-" + fname));

            int dlen = 0;
            byte[] buf = new byte[4096];
            while (dlen < flen) {
                int len = dataInputStream.read(buf, 0, buf.length);
                fos.write(buf, 0, len);
                fos.flush();
                dlen += len;
            }
            fos.close();
            return fname;
        }
    }

    public static void main(String[] args) {
        Client c = new Client(new Event() {
            @Override
            public void onReceiveText(String text) {
                System.out.println(text);
            }

            @Override
            public void onReceiveFile(String filename) {

            }

            @Override
            public void onLogin(String name, String id) {

            }
        });

        try {
            c.sendText("Hello");
            c.sendFile("D:\\VSCodeUserSetup-x64-1.40.1.exe");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}