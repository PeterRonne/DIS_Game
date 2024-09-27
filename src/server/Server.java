package server;

import java.net.*;
public class Server {
	/**
	 * @param args
	 */
	public static void main(String[] args)throws Exception {
		common c = new common("eksempel");
		ServerSocket welcomeSocket = new ServerSocket(6789);
		while (true) {
			System.out.println("[SERVER] Waiting for connection...");
			Socket connectionSocket = welcomeSocket.accept();
			System.out.println("[SERVER] Connection accepted.");
			(new ServerThread(connectionSocket,c)).start();
		}
	}

}
