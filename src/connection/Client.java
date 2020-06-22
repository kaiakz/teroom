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

    private ScreenReceiver screenReceiver = null;

    public Client(ClientEvent clientEvent) {
        this.clientEvent = clientEvent;

        try {
            ConnectServer();
            initDataStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        new Thread(new Receiver()).start();
    }

    private DataInputStream getDataInputStream() {
        return dataInputStream;
    }

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
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
        dataOutputStream.writeInt(MsgCode.TEXT);
        dataOutputStream.flush();
        dataOutputStream.writeUTF(text);
        dataOutputStream.flush();
    }

    public void sendFile(String filepath) throws IOException {
        dataOutputStream.writeInt(MsgCode.FILE);
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

    public void sendAnswer(String answer) throws IOException {
        dataOutputStream.writeInt(MsgCode.ANSWER);
        dataOutputStream.flush();
        dataOutputStream.writeUTF(answer);
        dataOutputStream.flush();
    }

    public boolean Login(String name, String id) throws IOException {
        dataOutputStream.writeInt(MsgCode.LOGIN);
        dataOutputStream.flush();
        dataOutputStream.writeUTF(name);
        dataOutputStream.flush();
        dataOutputStream.writeUTF(id);
        dataOutputStream.flush();
        boolean res = dataInputStream.readBoolean();
        if (res) {
            new Thread(new Receiver()).start();
        }
        return res;
    }

    class Receiver implements Runnable {
        @Override
        public void run() {
            try {
                int code;
                while (true) {
                    code = dataInputStream.readInt();
                    switch (code) {
                        case MsgCode.TEXT:
                            String sender = dataInputStream.readUTF();
                            String text = dataInputStream.readUTF();
                            clientEvent.onReceiveText(sender, text);
                            break;
                        case MsgCode.FILE:
                            String fname = getFile();
//                            String sender = dataInputStream.readUTF();
                            clientEvent.onReceiveFile("TEACHER", fname);
                            break;
                        case MsgCode.QUIZ:
                            String quiz = dataInputStream.readUTF();
                            clientEvent.onReceivedQuiz(quiz);
                            break;
                        case MsgCode.SCREEN:
                            if (screenReceiver == null) {
                                screenReceiver = new ScreenReceiver(connection.getInetAddress());
                                new Thread(screenReceiver).start();
                            } else {
                                screenReceiver.reverseFrame();
                            }
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("教师端已断开连接");
                System.exit(-1);
            }
        }

        String getFile() throws IOException {
            String fname = dataInputStream.readUTF();
            long flen = dataInputStream.readLong();

            final String path = "download";

            File f = new File("download");
            if(!f.exists()){
                f.mkdir(); //create temp directory on current location in which all images will be stored if not exists
            }

            FileOutputStream fos = new FileOutputStream(new File(path + '/' + fname));

            int dlen = 0;
            byte[] buf = new byte[4096];
            while (dlen < flen) {
                int len = dataInputStream.read(buf, 0, buf.length);
                fos.write(buf, 0, len);
                fos.flush();
                dlen += len;
            }
            fos.close();
            return path + '/' + fname;
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

            @Override
            public void onReceivedQuiz(String quiz) {

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