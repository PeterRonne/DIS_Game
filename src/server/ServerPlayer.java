package server;

public class ServerPlayer {
    String name;
    Pair location;
    int point;
    String direction;

    public ServerPlayer(String name, Pair loc) {
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
        return location.x;
    }

    public void setXpos(int xpos) {
        this.location.x = xpos;
    }

    public int getYpos() {
        return location.y;
    }

    public void setYpos(int ypos) {
        this.location.y = ypos;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void addPoints(int p) {
        point += p;
    }

    public String getRandomDirection() {
        int n = (int) (Math.random() * 4);
        switch (n) {
            case 0: return "up";
            case 1: return "down";
            case 2: return "left";
            case 3: return "right";
            default: return "up";
        }
    }

    public String toString() {
        return name + "," + location.x + "," + location.y + "," + direction + "," + point;
    }
}
