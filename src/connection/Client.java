package connection;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

public class Client {
    private Socket connection;
    private Event event;

    public Client(Event event) {
        ConnectServer();

        new Thread(new Receiver()).start();
    }

    public Client(Socket socket, Event event) {
        connection = socket;

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

    //

    class Receiver implements Runnable {

        @Override
        public void run() {
            try {
                String msg;
//                while ((msg=reader.readLine()) != null) {
//                    event.onReceiveText();
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}