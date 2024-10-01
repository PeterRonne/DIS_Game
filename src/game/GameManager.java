package game;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class GameManager {
    private static Socket clientSocket;
    private static DataOutputStream outToServer;
    private static ThreadIn threadIn;
    private static String playerName;
    private static final HashMap<String, Integer> players = new HashMap<>();

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
        threadIn = new ThreadIn(clientSocket);
        threadIn.start();
    }

    public static void requestAddPlayer(String playerName) {
        try {
            outToServer.writeBytes("addplayer" + "," + playerName + "\n");
            outToServer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to add player", e);
        }
    }

    public static void requestRemovePlayer() {
        try {
            outToServer.writeBytes("removeplayer" + "," + playerName + "\n");
        } catch (IOException e) {
            throw new RuntimeException("Failed to remove player", e);
        }
    }

    public static void requestGameState() {
        try {
            outToServer.writeBytes("gamestate" + "\n");
            outToServer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to request game state", e);
        }
    }

    public static void requestMove(int delta_x, int delta_y, String direction) {
        try {
            outToServer.writeBytes("moveplayer" + "," + playerName + "," + delta_x + "," + delta_y + "," + direction + "\n");
            outToServer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to request move ", e);
        }
    }

    public static void update(String serverMessage) {
        System.out.println(serverMessage);
        if (serverMessage == null) return;
        String[] serverMessageSplit = serverMessage.split("/");
        String update = serverMessageSplit[0];

        switch (update) {
            case "gamestate": {
                System.out.println("Get current gamestate");
                String[] incomingPlayers = serverMessageSplit[1].split("#");
                System.out.println("Incomming players" + incomingPlayers.toString());
                for (String player : incomingPlayers) {
                    String[] playerAttributes = player.split(",");
                    String name = playerAttributes[0];
                    int x_position = Integer.parseInt(playerAttributes[1]);
                    int y_position = Integer.parseInt(playerAttributes[2]);
                    String direction = playerAttributes[3];
                    int point = Integer.parseInt(playerAttributes[4]);
                    players.put(name, point);
                    Gui.placePlayerOnScreen(new Pair(x_position, y_position), direction);
                }
            }
            break;
            case "addplayer": {
                System.out.println("player added");
                String[] player = serverMessageSplit[1].split(",");
                String name = player[0];
                players.put(name, 0); // Player name and initial score
                int x_position = Integer.parseInt(player[1]);
                int y_position = Integer.parseInt(player[2]);
                String direction = player[3];
                Gui.placePlayerOnScreen(new Pair(x_position, y_position), direction);
            }
            break;
            case "removeplayer": {
                String[] playerInfo = serverMessageSplit[1].split(",");
                int xPos = Integer.parseInt(playerInfo[0]);
                int yPos = Integer.parseInt(playerInfo[1]);
                String name = playerInfo[2];
                players.remove(name);
                Pair position = new Pair(xPos, yPos);
                Gui.removePlayerOnScreen(position);
            }
            break;
            case "moveplayer": {
                System.out.println("player moved");
                String[] updatedMoves = serverMessageSplit[1].split(",");
                int old_x_position = Integer.parseInt(updatedMoves[0]);
                int old_y_position = Integer.parseInt(updatedMoves[1]);
                Pair oldpos = new Pair(old_x_position, old_y_position);
                int new_x_position = Integer.parseInt(updatedMoves[2]);
                int new_y_position = Integer.parseInt(updatedMoves[3]);
                Pair newpos = new Pair(new_x_position, new_y_position);
                String direction = updatedMoves[4];
                int point = Integer.parseInt(updatedMoves[5]);
                String name = updatedMoves[6];
                players.put(name, point);
                Gui.movePlayerOnScreen(oldpos, newpos, direction);
            }
            break;
            default:
                throw new IllegalArgumentException("Unknown update type: " + update);
        }
    }


    public static void setPlayerName(String name) {
        playerName = name;
    }

    public static HashMap<String, Integer> getPlayers() {
        return players;
    }


}
