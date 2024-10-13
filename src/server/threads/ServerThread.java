package server.threads;

import server.controller.GameLogic;
import server.Server;
import server.model.ServerPlayer;

import java.net.*;
import java.io.*;
import java.util.List;
import java.util.Set;

public class ServerThread extends Thread {
    private final Socket connSocket;

    public ServerThread(Socket connSocket) {
        this.connSocket = connSocket;
    }

    public void run() {
        try {
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connSocket.getOutputStream());

            String message;
            while (!connSocket.isClosed() && (message = inFromClient.readLine()) != null) {
                System.out.println(message);
                String[] messageSplit = message.split(",");
                String request = messageSplit[0];
                switch (request) {
                    case "gamestate": {
                        StringBuilder builder = new StringBuilder();
                        List<ServerPlayer> players = GameLogic.getCurrentPlayers();
                        for (ServerPlayer player : players) {
                            builder.append(player.toString()).append("#");
                        }
                        outToClient.writeBytes("gamestate/" + builder + '\n');
                    }
                    break;
                    case "sendplayers": {
                        StringBuilder builder = new StringBuilder();
                        Set<String> players = GameLogic.getPlayerNames();
                        players.forEach((player) -> builder.append(player).append("#"));
                        outToClient.writeBytes("sendplayers/" + builder + '\n');
                    }
                    break;
                    case "addplayer": {
                        String name = messageSplit[1];
                        ServerPlayer player = GameLogic.addPlayerToGame(name);
                        System.out.println("[SERVER] Added new player, " + name + "\n");
                        Server.sendUpdateToAll("addplayer/" + player);
                    }
                    break;
                    case "removeplayer": {
                        String name = messageSplit[1];
                        ServerPlayer player = GameLogic.getPlayer(name);
                        int delta_x = player.getXpos();
                        int delta_y = player.getYpos();
                        Server.sendUpdateToAll("removeplayer/" + delta_x + "," + delta_y + "," + name);
                        GameLogic.removePlayer(name);
                        Server.removeClient(connSocket);
                        connSocket.close();
                    }
                    break;
                    case "moveplayer": {
                        String name = messageSplit[1];
                        int delta_x = Integer.parseInt(messageSplit[2]);
                        int delta_y = Integer.parseInt(messageSplit[3]);
                        String direction = messageSplit[4];
                        GameLogic.updatePlayer(name, delta_x, delta_y, direction);
                    }
                    break;
                    case "fireweapon": {
                        String name = messageSplit[1];
                        GameLogic.fireWeapon(name);
                    }
                    break;
                    default:
                        System.out.println("Unknown update type: " + request);
                }
            }

        } catch (IOException e) {
            System.out.println("Client disconnected suddenly");
        } finally {
            Server.removeClient(connSocket);
//            GameLogic.removePlayer();
            try {
                connSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
