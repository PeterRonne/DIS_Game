package server.model;

import server.threads.ClientHandler;

public class Player {
    private final String name;
    private Pair location;
    private int point;
    private String direction;
    private ClientHandler clientHandler;

    public Player(String name, Pair loc, ClientHandler clientHandler) {
        this.name = name;
        this.location = loc;
        this.direction = getRandomDirection();
        this.point = 0;
        this.clientHandler = clientHandler;
        clientHandler.setPlayer(this);
    }

    public Player(String name, Pair loc) {
        this.name = name;
        this.location = loc;
        this.direction = getRandomDirection();
        this.point = 0;
    }

    public Pair getLocation() {
        return this.location;
    }

    public void setLocation(Pair p) {
        this.location = p;
    }

    public int getXpos() {
        return location.getX();
    }

    public int getYpos() {
        return location.getY();
    }

    public void setYpos(int ypos) {
        this.location.setY(ypos);
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getName() {
        return this.name;
    }

    public int getPoint() {
        return point;
    }

    public void resetPoint() {
        this.point = 0;
    }

    public void addPoints(int p) {
        point += p;
    }

    public String getRandomDirection() {
        int n = (int) (Math.random() * 4);
        return switch (n) {
            case 0 -> "up";
            case 1 -> "down";
            case 2 -> "left";
            case 3 -> "right";
            default -> "up";
        };
    }

    public String toString() {
        return name + "," + location.getX() + "," + location.getY() + "," + direction + "," + point;
    }
}
