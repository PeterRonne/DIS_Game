package server;

import game.GameManager;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class BroadcastServerThread extends Thread {
    private boolean running = true;

    @Override
    public void run() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setBroadcast(true);
            String[] ipAddress = String.valueOf(InetAddress.getLocalHost()).split("/");
            String port = "/6789";
            String message = ipAddress[1] + port;
            byte[] buffer = message.getBytes();

            InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAddress, 9876);

            while (running) {
                socket.send(packet);
                System.out.println("[SERVER] sending: " + message);
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    public void stopBroadcasting() {
        running = false;
    }
}

