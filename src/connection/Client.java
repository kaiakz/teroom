package connection;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

public class Client {
    private Socket connection;
    private ClientEvent clientEvent;

    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    public Client(ClientEvent clientEvent) {
        this.clientEvent = clientEvent;

        try {
            ConnectServer();
            initDataStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(new Receiver()).start();
    }

    private void ConnectServer() throws IOException{

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
    }

    private void initDataStream() throws IOException{
        dataOutputStream = new DataOutputStream(new BufferedOutputStream(connection.getOutputStream()));
        dataInputStream = new DataInputStream(new BufferedInputStream(connection.getInputStream()));
    }

    public void sendText(String text) throws IOException {
        dataOutputStream.writeUTF("MSG:TEXT");
        dataOutputStream.flush();
        dataOutputStream.writeUTF(text);
        dataOutputStream.flush();
    }

    public void sendFile(String filepath) throws IOException {
        dataOutputStream.writeUTF("MSG:FILE");
        dataOutputStream.flush();

        File f = new File(filepath);

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

    public void Login(String name, String id) throws IOException {
        dataOutputStream.writeUTF("MSG:LOGIN");
        dataOutputStream.flush();
        dataOutputStream.writeUTF(name);
        dataOutputStream.flush();
        dataOutputStream.writeUTF(id);
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
                        String sender = dataInputStream.readUTF();
                        String text = dataInputStream.readUTF();
                        clientEvent.onReceiveText(sender, text);
                    } else if (msg.equals("MSG:FILE")) {
                        String fname = getFile();
                        String sender = dataInputStream.readUTF();
                        clientEvent.onReceiveFile(sender, fname);
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
        Client c = new Client(new ClientEvent() {
            @Override
            public void onReceiveText(String sender, String text) {
                System.out.println(sender + " Says " + text);
            }

            @Override
            public void onReceiveFile(String sender, String filename) {

            }
        });

        try {
            c.Login("王小明", "11111");
            c.sendText("Hello");
//            c.sendFile("/run/media/kai/Dev/Telegram/Telegram");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}