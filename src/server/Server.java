package server;

import server.controller.GameLogic;
import server.threads.BroadcastServerThread;
import server.threads.ClientHandler;
import server.threads.ServerThread;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
	private static final List<ClientHandler> clientHandlers = new CopyOnWriteArrayList<>();
	
	public static void main(String[] args)throws Exception {
		BroadcastServerThread server = new BroadcastServerThread();
		server.start();

		GameLogic.addTestPlayers();
		ServerSocket welcomeSocket = new ServerSocket(6789);
		Thread thread = new Thread(() -> {
			while (!welcomeSocket.isClosed()) {
				System.out.println("[SERVER] Waiting for connection...");
                Socket connectionSocket = null;
                try {
                    connectionSocket = welcomeSocket.accept();
                } catch (IOException e) {
					System.out.println("[SERVER] Connection failed");
                }
				if (connectionSocket != null) {
					ClientHandler clientHandler = new ClientHandler(connectionSocket);
					addClient(clientHandler);
					System.out.println("[SERVER] Connection accepted.");
					(new ServerThread(connectionSocket, clientHandler)).start();
				}
			}
		});
		thread.start();
	}

	synchronized public static void addClient(ClientHandler clientHandler) {
		clientHandlers.add(clientHandler);
	}

	synchronized public static void removeClient(Socket connSocket) {
		clientHandlers.removeIf((clientHandler -> clientHandler.getConnectionSocket().equals(connSocket)));
	}

	synchronized public static void sendUpdateToAll(String update) {
		clientHandlers.forEach(clientHandler -> clientHandler.receiveMessage(update));
	}

}
