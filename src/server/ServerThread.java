package server;

import java.net.*;
import java.io.*;

public class ServerThread extends Thread {
    private final Socket connSocket;

    public ServerThread(Socket connSocket) {
        this.connSocket = connSocket;
    }

    public void run() {
        try {
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connSocket.getOutputStream());

            String newPlayerName = inFromClient.readLine();
            ServerPlayer player = GameLogic.addPlayerToGame(newPlayerName);
            System.out.println("[SERVER] Added new player, " + newPlayerName + "\n");
//            outToClient.writeBytes("addplayer," + player.toString() + "\n");

            Server.sendUpdateToAll("addplayer," + player.toString());

            String message;
            while ((message = inFromClient.readLine()) != null) {
                System.out.println(message);
                String[] messageSplit = message.split(",");
                String request = messageSplit[0];
                switch (request) {
                    case "moveplayer": {
                        String name = messageSplit[1];
                        int delta_x = Integer.parseInt(messageSplit[2]);
                        int delta_y = Integer.parseInt(messageSplit[3]);
                        String direction = messageSplit[4];
                        GameLogic.updatePlayer(name, delta_x, delta_y, direction);
                    }
                    break;
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        // do the work here
    }
}
