package server;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
	private static final List<ClientHandler> clientHandlers = new ArrayList<>();
	
	public static void main(String[] args)throws Exception {
		ServerSocket welcomeSocket = new ServerSocket(6789);
		while (!welcomeSocket.isClosed()) {
			System.out.println("[SERVER] Waiting for connection...");
			Socket connectionSocket = welcomeSocket.accept();
			addClientHandler(new ClientHandler(connectionSocket));
			System.out.println("[SERVER] Connection accepted.");
			(new ServerThread(connectionSocket)).start();
		}
	}

	public static void addClientHandler(ClientHandler clientHandler) {
		clientHandlers.add(clientHandler);
	}

	public static void remover(ClientHandler clientHandler) {
		clientHandlers.remove(clientHandler);
	}

	public static void removeClient(Socket conSocket) {
		ClientHandler cl = null;
		for (ClientHandler clientHandler : clientHandlers) {
			if (clientHandler.getConnectionSocket() == conSocket) {
				cl = clientHandler;
			}
		}
		remover(cl);
	}
	
	public static void sendUpdateToAll(String update) {
		for (ClientHandler clientHandler : clientHandlers) {
			clientHandler.receiveMessage(update);
		}
	}

}
