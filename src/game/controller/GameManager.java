package game.controller;

import game.gui.Gui;
import game.model.Pair;
import game.threads.ThreadIn;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class GameManager {
    private static Socket clientSocket;
    private static DataOutputStream outToServer;
    private static String playerName;
    public static boolean gameStarted;
    private static String CONNECTION_ADDRESS;
    private static int PORT;
    private static final HashMap<String, Integer> players = new HashMap<>();

    public static boolean isGameStarted() {
        return gameStarted;
    }

    public static void startGame() {
        gameStarted = true;
    }

    public static void setConnectionAddress(String address) {
        CONNECTION_ADDRESS = address;
    }

    public static void setPort(int port) {
        PORT = port;
    }

    public static void setPlayerName(String name) {
        playerName = name;
    }

    public static boolean initializeConnection() {
        try {
            gameStarted = false;
            clientSocket = new Socket(CONNECTION_ADDRESS, PORT);
            GameManager.outToServer = new DataOutputStream(clientSocket.getOutputStream());
            startInThread();
            return true;
        } catch (IOException e) {
            System.out.println("Failed to create connection ");
            return false;
        }
    }

    public static void startInThread() {
        ThreadIn threadIn = new ThreadIn(clientSocket);
        threadIn.start();
    }

    public static void requestAddPlayer() {
        try {
            outToServer.writeBytes("addplayer" + "," + playerName + "\n");
            outToServer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to add player", e);
        }
    }

    public static void requestRemovePlayer() {
        try {
            outToServer.writeBytes("removeplayer," + playerName + "\n");
        } catch (IOException e) {
            throw new RuntimeException("Failed to remove player", e);
        }
    }

    public static void requestGameState() {
        try {
            outToServer.writeBytes("gamestate," + "\n");
            outToServer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to request game state", e);
        }
    }

    public static void requestCurrentPlayers() {
        try {
            outToServer.writeBytes("sendplayers" + "\n");
            outToServer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to request players", e);
        }
    }

    public static void requestMove(int delta_x, int delta_y, String direction) {
        try {
            outToServer.writeBytes("moveplayer," + playerName + "," + delta_x + "," + delta_y + "," + direction + "\n");
            outToServer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to request move ", e);
        }
    }

    public static void requestShoot() {
        try {
            outToServer.writeBytes("fireweapon," + playerName + "\n");
            outToServer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to request move ", e);
        }
    }

    public static void update(String[] serverMessage) {
        System.out.println(Arrays.toString(serverMessage));
        if (serverMessage == null) return;

        String update = serverMessage[0];

        switch (update) {
            case "gamestate": {
                System.out.println("Current gamestate received");
                if (serverMessage.length > 1) {
                    String[] incomingPlayers = serverMessage[1].split("#");
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
                    Gui.updateScoreTable();
                }
            }
            break;
            case "sendplayers": {
                if (serverMessage.length > 1) {
                    String[] incomingPlayerNames = serverMessage[1].split("#");
                    for (String incomingPlayerName : incomingPlayerNames) {
                        players.put(incomingPlayerName, 0);
                    }
                }
            }
            break;
            case "addplayer": {
                System.out.println("Player added");
                String[] player = serverMessage[1].split(",");
                String name = player[0];
                players.put(name, 0); // Player name and initial score
                int x_position = Integer.parseInt(player[1]);
                int y_position = Integer.parseInt(player[2]);
                String direction = player[3];
                Gui.placePlayerOnScreen(new Pair(x_position, y_position), direction);
                Gui.updateScoreTable();
            }
            break;
            case "removeplayer": {
                System.out.println("Player removed");
                String[] playerInfo = serverMessage[1].split(",");
                int xPos = Integer.parseInt(playerInfo[0]);
                int yPos = Integer.parseInt(playerInfo[1]);
                String name = playerInfo[2];
                players.remove(name);
                Pair position = new Pair(xPos, yPos);
                Gui.removeObjectOnScreen(position);
                Gui.updateScoreTable();
            }
            break;
            case "moveplayer": {
                System.out.println("Player moved");
                String[] updatedMoves = serverMessage[1].split(",");
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
            case "fireweapon": {
                System.out.println("Player shoots");
                if (serverMessage.length > 2) {
                    String direction = serverMessage[1];
                    String[] pairStrings = serverMessage[2].split("#");
                    Pair[] pairs = new Pair[pairStrings.length];
                    for (int i = 0; i < pairStrings.length; i++) {
                        String[] attr = pairStrings[i].split(",");
                        pairs[i] = new Pair(Integer.parseInt(attr[0]), Integer.parseInt(attr[1]));
                    }
                    Gui.fireWeapon(pairs, direction);
                    if (serverMessage.length > 3) {
                        String[] playersHit = serverMessage[3].split("#");
                        for (String player : playersHit) {
                            String[] playerAttributes = player.split(",");
                            String name = playerAttributes[0];
                            int x_position = Integer.parseInt(playerAttributes[1]);
                            int y_position = Integer.parseInt(playerAttributes[2]);
                            direction = playerAttributes[3];
                            int point = Integer.parseInt(playerAttributes[4]);
                            players.put(name, point);
                            Gui.placePlayerOnScreen(new Pair(x_position, y_position), direction);
                        }
                        Gui.updateScoreTable();
                    }
                }
            }
            break;
            case "winnerfound": {
                String winner = serverMessage[1];
                Gui.showWinnerMessage(winner);
            }
            break;
            default:
                System.out.println("Unknown update type: " + update);
        }
    }

    public static HashMap<String, Integer> getPlayers() {
        return players;
    }

    public static boolean validateName(String name) {
        return !players.containsKey(name);
    }
}
