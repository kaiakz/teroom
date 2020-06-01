package connection;

import java.net.*;
import java.util.ArrayList;

public class Server {
    ArrayList<Client> clients;
    Event cevent;   // Client Event
//    Event sevent;   // Server Event

    public Server(Event cevent) {
        cevent = cevent;

        clients = new ArrayList<>();
        new Thread(new Listener()).start();
        new Thread(new BroadCaster()).start();
    }

    class Listener implements Runnable {
        @Override
        public void run() {
            try {
                ServerSocket server = new ServerSocket(8924);
                while (true) {
                    Socket csocket = server.accept();
                    Client client = new Client(csocket, cevent);
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
                    System.out.println("Broadcasting");
                    Thread.sleep(delay);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}