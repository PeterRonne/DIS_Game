package game.threads;

import game.controller.GameManager;
import game.gui.WelcomeScreen;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BroadcastReceiverThread extends Thread {

    @Override
    public void run() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(9876, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String[] receivedMessage = new String(packet.getData(), 0, packet.getLength()).split("/");
            GameManager.setConnectionAddress(receivedMessage[0]);
            GameManager.setPort(Integer.parseInt(receivedMessage[1]));

            WelcomeScreen.updateConnectionInfo(receivedMessage[0], receivedMessage[1]);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
