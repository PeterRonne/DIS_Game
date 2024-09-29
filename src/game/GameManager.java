package game;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

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

    public static void createDefaultPlayers() {
        players.put("harry", 0);
        requestAddPlayer("harry");
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
        String[] serverMessageSplit = serverMessage.split(",");
        String updateType = serverMessageSplit[0];

        switch (updateType) {
            case "gamestate": {
                System.out.println("Get current gamestate");
                String[] incomingPlayers = serverMessageSplit[1].split("/");
                for (String player : incomingPlayers) {
                    String[] playerAttributes = player.split(",");
                    String name = playerAttributes[0];
                    players.put(name, 0); // Score is zero for now but should be the players current score
                    int x_position = Integer.parseInt(playerAttributes[1]);
                    int y_position = Integer.parseInt(playerAttributes[2]);
                    String direction = playerAttributes[3];
                    Gui.placePlayerOnScreen(new Pair(x_position, y_position), direction);
                }
            }

            case "addplayer": {
                System.out.println("player added");
                String name = serverMessageSplit[1];
                players.put(name, 0); // Player name and initial score
                int x_position = Integer.parseInt(serverMessageSplit[2]);
                int y_position = Integer.parseInt(serverMessageSplit[3]);
                String direction = serverMessageSplit[4];
                Gui.placePlayerOnScreen(new Pair(x_position, y_position), direction);
            }
            break;
            case "moveplayer": {
                System.out.println("player moved");
                int old_x_position = Integer.parseInt(serverMessageSplit[1]);
                int old_y_position = Integer.parseInt(serverMessageSplit[2]);
                Pair oldpos = new Pair(old_x_position, old_y_position);
                int new_x_position = Integer.parseInt(serverMessageSplit[3]);
                int new_y_position = Integer.parseInt(serverMessageSplit[4]);
                Pair newpos = new Pair(new_x_position, new_y_position);
                String direction = serverMessageSplit[5];
                Gui.movePlayerOnScreen(oldpos, newpos, direction);
            }
            break;
            default:
                throw new IllegalArgumentException("Unknown update type: " + updateType);
        }
    }


    public static void setPlayerName(String name) {
        playerName = name;
    }


}
