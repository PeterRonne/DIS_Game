package server.controller;


import server.Server;
import server.model.General;
import server.model.Pair;
import server.model.Player;
import server.threads.ClientHandler;

import java.util.*;


public class GameLogic {
    public static HashMap<String, Player> players = new HashMap<>();

    public synchronized static Player addPlayerToGame(String name, ClientHandler clientHandler) {
        Pair pair = getRandomFreePosition();
        System.out.println("[SERVER] Player added at position X: " + pair.getX() + " Y: " + pair.getY());
        Player player = new Player(name, pair, clientHandler);
        players.put(name, player);
        return player;
    }

    public synchronized static void removePlayer(String name) {
        players.remove(name);
    }

    public static void addTestPlayers() {
        Pair pair = getRandomFreePosition();
        Player test = new Player("test", pair);
        players.put("test", test);

        pair = getRandomFreePosition();
        Player test2 = new Player("test2", pair);
        players.put("test2", test2);
    }

    public static Pair getRandomFreePosition()
    // finds a random new position which is not wall
    // and not occupied by other players
    {
        int x = 1;
        int y = 1;
        boolean foundfreepos = false;
        while (!foundfreepos) {
            Random r = new Random();
            x = Math.abs(r.nextInt() % 18) + 1;
            y = Math.abs(r.nextInt() % 18) + 1;
            if (General.board[y].charAt(x) == ' ') // er det gulv ?
            {
                foundfreepos = true;
                for (Player p : players.values()) {
                    if (p.getXpos() == x && p.getYpos() == y) //pladsen optaget af en anden
                        foundfreepos = false;
                }

            }
        }
        Pair p = new Pair(x, y);
        return p;
    }

    public synchronized static void updatePlayer(String name, int delta_x, int delta_y, String direction) {
        Player player = players.get(name);
        if (player == null) {
            System.out.println("No player to update");
            return;
        }
        player.setDirection(direction);
        int x = player.getXpos(), y = player.getYpos();

        if (General.board[y + delta_y].charAt(x + delta_x) == 'w') {
            player.addPoints(-1);
            Server.sendUpdateToAll("moveplayer/" + x + "," + y + "," + x + "," + y + "," + player.getDirection() + "," + player.getPoint() + "," + player.getName());
        } else {
            // collision detection
            Player otherPlayer = getPlayerAt(x + delta_x, y + delta_y);
            if (otherPlayer != null) {
                player.addPoints(10);
                //update the other player
                otherPlayer.addPoints(-10);
                Pair newpos = getRandomFreePosition();
                otherPlayer.setLocation(newpos);
                Pair oldpos = new Pair(x + delta_x, y + delta_y);
                Server.sendUpdateToAll("moveplayer/" + oldpos.getX() + "," + oldpos.getY() + "," + newpos.getX() + "," + newpos.getY() + "," + otherPlayer.getDirection() + "," + otherPlayer.getPoint() + "," + otherPlayer.getName());
            } else {
                player.addPoints(1);
            }
            Pair oldpos = player.getLocation();
            Pair newpos = new Pair(x + delta_x, y + delta_y);
            player.setLocation(newpos);
            Server.sendUpdateToAll("moveplayer/" + oldpos.getX() + "," + oldpos.getY() + "," + newpos.getX() + "," + newpos.getY() + "," + direction + "," + player.getPoint() + "," + player.getName());

            if (player.getPoint() >= 1000) {
                Server.sendUpdateToAll("winnerfound/" + player.getName());
                resetPlayerScores();
            }
        }
    }

    public synchronized static void fireWeapon(String name) {
        Player player = players.get(name);
        if (player == null) {
            System.out.println("No player like that exists");
            return;
        }

        StringBuilder locations = new StringBuilder();
        StringBuilder playersHit = new StringBuilder();

        String direction = player.getDirection();
        locations.append(direction).append("/");

        Pair result = getAdvanceCoordinates(direction);
        int x = player.getXpos(), y = player.getYpos();

        while (General.board[y += result.getY()].charAt(x += result.getX()) != 'w') {
            Player otherPlayer = getPlayerAt(x, y);
            if (otherPlayer != null) {
                player.addPoints(50);
                otherPlayer.addPoints(-50);
                otherPlayer.setLocation(getRandomFreePosition());
                playersHit.append(otherPlayer).append("#");
            }
            locations.append(new Pair(x, y)).append("#");
        }
        locations.append("/");

        Server.sendUpdateToAll("fireweapon/" + locations + playersHit);
        if (player.getPoint() >= 1000) {
            Server.sendUpdateToAll("winnerfound/" + player.getName());
            resetPlayerScores();
        }
    }

    private static Pair getAdvanceCoordinates(String direction) {
        int delta_x = 0, delta_y = 0;
        switch (direction) {
            case "up" -> delta_y = -1;
            case "down" -> delta_y = 1;
            case "left" -> delta_x = -1;
            case "right" -> delta_x = 1;
        }
        return new Pair(delta_x, delta_y);
    }

    public static Player getPlayerAt(int x, int y) {
        for (Player p : players.values()) {
            if (p.getXpos() == x && p.getYpos() == y) {
                return p;
            }
        }
        return null;
    }

    private static void resetPlayerScores() {
        for (Player player : players.values()) {
            player.resetPoint();
        }
    }

    public static List<Player> getCurrentPlayers() {
        return new ArrayList<>(players.values());
    }
    public static Set<String> getPlayerNames() {
        return new HashSet<>(players.keySet());
    }

    public static Player getPlayer(String name) {
        return players.get(name);
    }
}