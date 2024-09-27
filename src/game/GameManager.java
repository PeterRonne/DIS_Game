package game;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class GameManager {
    private static Socket clientSocket;
    private static DataOutputStream outToServer;

    public static void initializeConnection(Socket clientSocket) {
        GameManager.clientSocket = clientSocket;
        try {
            GameManager.outToServer = new DataOutputStream(clientSocket.getOutputStream());
            startInThread();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create connection ", e);
        }
    }

    public static void startInThread() {
        ThreadIn threadIn = new ThreadIn(clientSocket);
        threadIn.start();
    }


    public static void requestMove(int delta_x, int delta_y, String direction) {
//        new Thread(() -> {
            try {
                outToServer.writeBytes("Delta X: " + delta_x + " Delta Y: " + delta_y + " Direction: " + direction + "\n");
                outToServer.flush();
            } catch (IOException e) {
                throw new RuntimeException("Failed to request move ", e);
            }
//        }).start();
    }

    public static void update(String serverMessage) {
        System.out.println(serverMessage);
    }


}
