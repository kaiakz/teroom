package connection;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {
    ArrayList<Connection> clients;
    ServerEvent serverEvent;   // Client Event


    public Server(ServerEvent serverEvent) {
        this.serverEvent = serverEvent;

        clients = new ArrayList<>();
        new Thread(new Listener()).start();
        new Thread(new BroadCaster()).start();
    }

    public void RelayText(int sender, String name, String text) throws IOException{
        for (int i = 0; i < clients.size(); i++) {
            if (i == sender || clients.get(i) == null)    continue;
            String tmp = name;
            if (tmp == null) {
                tmp = "Student" + i;
            }
            clients.get(i).sendText(tmp, text);
        }
    }

    public void broadcastText(String text) throws IOException{
        for(Connection c:clients) {
            if (c == null)  continue;
            c.sendText("教师", text);
        }
    }

    public void broadcastFile(String path) throws IOException{
        for(Connection c:clients) {
            if (c == null)  continue;
            c.sendFile(path);
        }
    }

    public void broadcastQuiz(String quiz) throws IOException {
        for(Connection c:clients) {
            if (c == null)  continue;
            c.sendQuiz(quiz);
        }
    }


    class Listener implements Runnable {
        @Override
        public void run() {
            try {
                ServerSocket server = new ServerSocket(8924);
                while (true) {
                    Socket csocket = server.accept();
                    Connection client = new Connection(csocket, serverEvent);
                    client.setID(clients.size());
                    clients.add(client);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class BroadCaster implements Runnable {
        private final static long delay = 1000;
        @Override
        public void run() {
            try {
                MulticastSocket ms = new MulticastSocket(8924);
                InetAddress broadAddr = InetAddress.getByName("230.0.0.1");
                DatagramSocket ds = new DatagramSocket();

                ms.joinGroup(broadAddr);
                ms.setLoopbackMode(false);

                byte[] data = ("ThisIsServer").getBytes();
                DatagramPacket outPacket = new DatagramPacket(data, data.length, broadAddr, 8924);

                while (true) {
                    ms.send(outPacket);
//                    System.out.println("Broadcasting");
                    Thread.sleep(delay);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class Connection  {
        private Socket connection;
        private ServerEvent serverEvent;

        private int ID;

        private String name = "STUDENT";
        private String stuid;

        private DataOutputStream dataOutputStream;
        private DataInputStream dataInputStream;

        public Connection(Socket socket, ServerEvent serverEvent) {
            this.connection = socket;
            this.serverEvent = serverEvent;

            try {
                initDataStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            new Thread(new Receiver()).start();
        }

        private void initDataStream() throws IOException{
            dataOutputStream = new DataOutputStream(new BufferedOutputStream(connection.getOutputStream()));
            dataInputStream = new DataInputStream(new BufferedInputStream(connection.getInputStream()));
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public int getID() {
            return ID;
        }

        public void sendText(String sender, String text) throws IOException {
            dataOutputStream.writeInt(MsgCode.TEXT);
            dataOutputStream.flush();
            dataOutputStream.writeUTF(sender);
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

        public void sendQuiz(String quiz) throws IOException {
            dataOutputStream.writeInt(MsgCode.QUIZ);
            dataOutputStream.flush();
            dataOutputStream.writeUTF(quiz);
            dataOutputStream.flush();
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
                                String text = dataInputStream.readUTF();
                                serverEvent.onReceiveText(name, text);
                                RelayText(getID(), name, text);
                                break;
                            case MsgCode.FILE:
                                String fname = getFile();
                                serverEvent.onReceiveFile(fname);
                                break;
                            case MsgCode.LOGIN:
                                name = dataInputStream.readUTF();
                                stuid = dataInputStream.readUTF();
                                boolean res = serverEvent.onLogin(stuid, name);
                                dataOutputStream.writeBoolean(res);
                                dataOutputStream.flush();
                                break;
                            case MsgCode.ANSWER:
                                String answer = dataInputStream.readUTF();
                                serverEvent.onReceiveAnswer(stuid, name, answer);
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    clients.set(ID, null);
                    try {
                        dataInputStream.close();
                        dataOutputStream.close();
                        connection.shutdownInput();
                        connection.shutdownOutput();
                        connection.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
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

    }

    public static void main(String[] args) {
        Server s = new Server(new ServerEvent() {
            @Override
            public void onReceiveText(String sender, String text) {
                System.out.println(text);
            }

            @Override
            public void onReceiveFile(String filename) {
                System.out.println("Received: "+ filename);
            }

            @Override
            public void onReceiveAnswer(String id, String name, String answer) {

            }

            @Override
            public boolean onLogin(String id, String name) {
                System.out.println("Login" + id + name);
                return true;
            }


        });

//        s.broadcastText("Hello Classmate");
    }
}