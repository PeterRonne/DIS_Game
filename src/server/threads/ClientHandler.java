package server.threads;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private final Socket connectionSocket;
    private DataOutputStream dataOutputStream;
    private String playerName;

    public ClientHandler(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
        initialize();
    }

    public void initialize() {
        try {
            dataOutputStream = new DataOutputStream(connectionSocket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void receiveMessage(String message) {
        try {
            dataOutputStream.writeBytes(message + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getConnectionSocket() {
        return connectionSocket;
    }
}
