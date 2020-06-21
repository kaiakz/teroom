package connection;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ScreenBroadcaster extends Thread {
    ArrayList<DataOutputStream> channels;
    Thread listener;

    Robot robot;
    Rectangle rectangle;
    int delay = 1;

    ScreenBroadcaster() throws AWTException {
        channels = new ArrayList<>();
        listener = new Listener();
        listener.start();
        robot = new Robot();
        rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    }

    @Override
    public void run() {
        while (true) {
            BufferedImage screenshot = robot.createScreenCapture(rectangle);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                ImageIO.write(screenshot, "jpg", byteArrayOutputStream);
                byte[] img = byteArrayOutputStream.toByteArray();
                System.out.println("Try sending" + byteArrayOutputStream.size());
                for (DataOutputStream out : channels) {
                    try {
                        out.writeInt(img.length);
                        out.flush();
                        out.write(img);
                        out.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                        channels.remove(out);
                    }
                }
//                Thread.sleep(delay);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

        class Listener extends Thread {
            @Override
            public void run() {
                try {
                    ServerSocket server = new ServerSocket(8925);
                    while (true) {
                        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(server.accept().getOutputStream()));
                        channels.add(dataOutputStream);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    public static void main(String[] args) {
        try {
            new ScreenBroadcaster().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
