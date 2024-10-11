package server.controller;


import server.Server;
import server.model.General;
import server.model.Pair;
import server.model.ServerPlayer;

import java.util.*;


public class GameLogic {
    //    public static List<ServerPlayer> players = new ArrayList<ServerPlayer>();
    public static HashMap<String, ServerPlayer> players = new HashMap<>();
    public static ServerPlayer me;


    public synchronized static ServerPlayer addPlayerToGame(String name) {
        Pair pair = getRandomFreePosition();
        System.out.println("[SERVER] Player added at position X: " + pair.getX() + " Y: " + pair.getY());
        ServerPlayer player = new ServerPlayer(name, pair);
        players.put(name, player);
        return player;
    }

    public static void removePlayer(String name) {
        players.remove(name);
    }

    public static void addTestPlayers() {
        Pair pair = getRandomFreePosition();
        ServerPlayer test = new ServerPlayer("test", pair);
        players.put("test", test);

        pair = getRandomFreePosition();
        ServerPlayer test2 = new ServerPlayer("test2", pair);
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
                for (ServerPlayer p : players.values()) {
                    if (p.getXpos() == x && p.getYpos() == y) //pladsen optaget af en anden
                        foundfreepos = false;
                }

            }
        }
        Pair p = new Pair(x, y);
        return p;
    }

    public synchronized static void updatePlayer(String name, int delta_x, int delta_y, String direction) {
        ServerPlayer player = players.get(name);
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
            ServerPlayer otherPlayer = getPlayerAt(x + delta_x, y + delta_y);
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

            if (player.getPoint() > 50) {
                Server.sendUpdateToAll("winnerfound/" + player.getName() + player.getPoint());
            }
        }
    }

    public static void fireWeapon(String name) {
        ServerPlayer player = players.get(name);
        if (player == null) {
            System.out.println("No player like that exists");
            return;
        }

        StringBuilder locations = new StringBuilder();
        StringBuilder playersHit = new StringBuilder();

        String direction = player.getDirection();
        locations.append(direction).append("/");
        Pair cur = advanceBulletPath(direction, player.getXpos(), player.getYpos());

        while (General.board[cur.getY()].charAt(cur.getX()) != 'w') {
            ServerPlayer otherPlayer = getPlayerAt(cur.getX(), cur.getY());
            if (otherPlayer != null) {
                player.addPoints(50);
                otherPlayer.addPoints(-50);
                otherPlayer.setLocation(getRandomFreePosition());
                playersHit.append(otherPlayer).append("#");
            }
            locations.append(cur).append("#");
            cur = advanceBulletPath(direction, cur.getX(), cur.getY());
        }
        locations.append("/");

        Server.sendUpdateToAll("fireweapon/" + locations + playersHit);

        if (player.getPoint() > 50) {
            Server.sendUpdateToAll("winnerfound/" + player.getName() + player.getPoint());
        }
    }

    private static Pair advanceBulletPath(String direction, int x, int y) {
        switch (direction) {
            case "up" -> y--;
            case "down" -> y++;
            case "left" -> x--;
            case "right" -> x++;
        }
        return new Pair(x, y);
    }

    public static ServerPlayer getPlayerAt(int x, int y) {
        for (ServerPlayer p : players.values()) {
            if (p.getXpos() == x && p.getYpos() == y) {
                return p;
            }
        }
        return null;
    }

    public static List<ServerPlayer> getCurrentPlayers() {
        return new ArrayList<>(players.values());
    }
    public static Set<String> getPlayerNames() {
        return new HashSet<>(players.keySet());
    }

    public static ServerPlayer getPlayer(String name) {
        return players.get(name);
    }


}