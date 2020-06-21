package connection;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ScreenReceiver extends JFrame implements Runnable {
    Socket socket;
    DataInputStream dataInputStream;

    ScreenReceiver(String host) throws IOException {
        socket = new Socket(host, 8925);
        dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        InitFrame();
    }

    ScreenReceiver(InetAddress host) throws IOException {
        socket = new Socket(host, 8925);
        dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        InitFrame();
    }

    private JLabel jLabel;

    @Override
    protected void processWindowEvent(WindowEvent e){
        if(e.getID() == WindowEvent.WINDOW_CLOSING){
            try {
                dataInputStream.close();
                socket.shutdownInput();
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            dispose();
        }
    }

    void InitFrame() {
        this.setLayout(null);
        this.setTitle("屏幕广播");
        this.setSize(1080, 720);
//        this.setBounds(0, 0, 1080, 720);
        jLabel = new JLabel();
//        label.setBounds(0, 0, 1920, 1080);
        JScrollPane jScrollPane = new JScrollPane(jLabel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setContentPane(jScrollPane);
//        this.getContentPane().add(label);
//        this.setVisible(true);
        //窗口关闭时，程序退出。两种方法都可以
//        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
//        this.addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent e ) {
//                System.exit(-1);
//            }
//        });
    }

    @Override
    public void run() {
        this.setVisible(true);
        while (true) {
            try {
                int len = dataInputStream.readInt();
                byte[] img = new byte[len];
                dataInputStream.readFully(img);
                BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(img));
                Image bimg = ImageIO.read(new ByteArrayInputStream(img));
                jLabel.setIcon(new ImageIcon(bimg));
//                ImageIO.write(bufferedImage, "jpg", new File(System.currentTimeMillis() + ".jpeg"));
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        dispose();
    }

    public static void main(String[] args) {
        try {
//            BFrame b = new BFrame();
            new Thread(new ScreenReceiver("127.0.0.1")).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}