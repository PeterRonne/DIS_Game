package game.threads;

import game.controller.GameManager;

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
        while (!clientSocket.isClosed()) {
            try {
                String serverMessage = inFromServer.readLine();
                String[] request = serverMessage.split("/");

                if (GameManager.isGameStarted() || request[0].equals("sendplayers"))
                    GameManager.update(request);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
