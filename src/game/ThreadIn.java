package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ThreadIn extends Thread {
    private final Socket clientSocket;
    private final BufferedReader inFromServer;

    public ThreadIn(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            this.inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while(!clientSocket.isClosed()) {
            try {
                String serverMessage = inFromServer.readLine();
                GameManager.update(serverMessage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
