package server.threads;

import server.model.Player;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private final Socket connectionSocket;
    private DataOutputStream dataOutputStream;
    private Player player;

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

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public Socket getConnectionSocket() {
        return connectionSocket;
    }
}
