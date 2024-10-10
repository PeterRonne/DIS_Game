package game.gui;

import game.controller.GameManager;
import game.model.Generel;
import game.model.Pair;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.*;
import javafx.util.Duration;

import java.util.Map;

public class Gui extends Application {

    public static final int size = 30;
    public static final int scene_height = size * 20 + 50;
    public static final int scene_width = size * 20 + 200;

    public static Image image_floor;
    public static Image image_wall;
    public static Image hero_right, hero_left, hero_up, hero_down;
    public static Image fire_left, fire_right, fire_up, fire_down, fire_horizontal, fire_vertical, fire_wall_east, fire_wall_west, fire_wall_south, fire_wall_north;

    private static Label[][] fields;
    private static TextArea scoreList;

    // -------------------------------------------
    // | Maze: (0,0)              | Score: (1,0) |
    // |-----------------------------------------|
    // | boardGrid (0,1)          | scorelist    |
    // |                          | (1,1)        |
    // -------------------------------------------

    @Override
    public void start(Stage primaryStage) {
        try {

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(0, 10, 0, 10));

            Text mazeLabel = new Text("Maze:");
            mazeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

            Text scoreLabel = new Text("Score:");
            scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

            scoreList = new TextArea();

            GridPane boardGrid = new GridPane();

            image_wall = new Image(getClass().getResourceAsStream("Image/wall4.png"), size, size, false, false);
            image_floor = new Image(getClass().getResourceAsStream("Image/floor1.png"), size, size, false, false);

            hero_right = new Image(getClass().getResourceAsStream("Image/heroRight.png"), size, size, false, false);
            hero_left = new Image(getClass().getResourceAsStream("Image/heroLeft.png"), size, size, false, false);
            hero_up = new Image(getClass().getResourceAsStream("Image/heroUp.png"), size, size, false, false);
            hero_down = new Image(getClass().getResourceAsStream("Image/heroDown.png"), size, size, false, false);


            fire_right = new Image(getClass().getResourceAsStream("Image/fireRight.png"), size, size, false, false);
            fire_left = new Image(getClass().getResourceAsStream("Image/fireLeft.png"), size, size, false, false);
            fire_up = new Image(getClass().getResourceAsStream("Image/fireUp.png"), size, size, false, false);
            fire_down = new Image(getClass().getResourceAsStream("Image/fireDown.png"), size, size, false, false);
            fire_horizontal = new Image(getClass().getResourceAsStream("Image/fireHorizontal.png"), size, size, false, false);
            fire_vertical = new Image(getClass().getResourceAsStream("Image/fireVertical.png"), size, size, false, false);

            fire_wall_east = new Image(getClass().getResourceAsStream("Image/fireWallEast.png"), size, size, false, false);
            fire_wall_west = new Image(getClass().getResourceAsStream("Image/fireWallWest.png"), size, size, false, false);
            fire_wall_north = new Image(getClass().getResourceAsStream("Image/fireWallNorth.png"), size, size, false, false);
            fire_wall_south = new Image(getClass().getResourceAsStream("Image/fireWallSouth.png"), size, size, false, false);


            fields = new Label[20][20];
            for (int j = 0; j < 20; j++) {
                for (int i = 0; i < 20; i++) {
                    switch (Generel.board[j].charAt(i)) {
                        case 'w':
                            fields[i][j] = new Label("", new ImageView(image_wall));
                            break;
                        case ' ':
                            fields[i][j] = new Label("", new ImageView(image_floor));
                            break;
                        default:
                            throw new Exception("Illegal field value: " + Generel.board[j].charAt(i));
                    }
                    boardGrid.add(fields[i][j], i, j);
                }
            }
            scoreList.setEditable(false);


            grid.add(mazeLabel, 0, 0);
            grid.add(scoreLabel, 1, 0);
            grid.add(boardGrid, 0, 1);
            grid.add(scoreList, 1, 1);

            Scene scene = new Scene(grid, scene_width, scene_height);
            primaryStage.setScene(scene);
            primaryStage.show();

            scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                switch (event.getCode()) {
                    case UP -> playerMoved(0, -1, "up");
                    case DOWN -> playerMoved(0, +1, "down");
                    case LEFT -> playerMoved(-1, 0, "left");
                    case RIGHT -> playerMoved(+1, 0, "right");
                    case SPACE -> playerShots();
                    case ESCAPE -> quitGame();
                }
            });

            primaryStage.setOnCloseRequest(event ->  {
                quitGame();
            });

            // Putting default players on screen
//            for (int i = 0; i < GameLogic.players.size(); i++) {
//                fields[GameLogic.players.get(i).getXpos()][GameLogic.players.get(i).getYpos()].setGraphic(new ImageView(hero_up));
//            }
            scoreList.setText(getScoreList());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void quitGame() {
        GameManager.requestRemovePlayer();
        updateScoreTable();
        System.exit(0);
    }

    public static void removeObjectOnScreen(Pair oldpos) {
        Platform.runLater(() -> {
            fields[oldpos.getX()][oldpos.getY()].setGraphic(new ImageView(image_floor));
        });
    }

    public static void placePlayerOnScreen(Pair newpos, String direction) {
        Platform.runLater(() -> {
            int newx = newpos.getX();
            int newy = newpos.getY();
            if (direction.equals("right")) {
                fields[newx][newy].setGraphic(new ImageView(hero_right));
            }

            if (direction.equals("left")) {
                fields[newx][newy].setGraphic(new ImageView(hero_left));
            }

            if (direction.equals("up")) {
                fields[newx][newy].setGraphic(new ImageView(hero_up));
            }

            if (direction.equals("down")) {
                fields[newx][newy].setGraphic(new ImageView(hero_down));
            }

        });
    }

    public static void fireWeapon(Pair[] pairs, String direction) {
        final KeyFrame kf1 = new KeyFrame(Duration.seconds(0), e -> drawBulletPath(pairs, direction));
        final KeyFrame kf2 = new KeyFrame(Duration.millis(500), e -> removeBulletPath(pairs));
        final Timeline timeline = new Timeline(kf1, kf2);
        Platform.runLater(timeline::play);
    }

    public static void drawBulletPath(Pair[] pairs, String direction) {
            setBullet(pairs[0], direction, true);
            for (int i = 1; i < pairs.length; i++) {
                setBulletTrail(pairs[i], direction);

            }
            setBullet(pairs[pairs.length - 1], direction, false);
    }

    private static void setBullet(Pair pair, String direction, boolean start) {
        ImageView graphic = switch (direction) {
            case "right" -> new ImageView(start ? fire_right : fire_wall_east);
            case "left" -> new ImageView(start ? fire_left : fire_wall_west);
            case "up" -> new ImageView(start ? fire_up : fire_wall_north);
            case "down" -> new ImageView(start ? fire_down : fire_wall_south);
            default -> throw new IllegalArgumentException("Unknown direction");
        };
        fields[pair.getX()][pair.getY()].setGraphic(graphic);
    }

    private static void setBulletTrail(Pair pair, String direction) {
        ImageView view = new ImageView(fire_vertical);
        if (direction.equals("left") || direction.equals("right")) {
            view = new ImageView(fire_horizontal);
        }
        fields[pair.getX()][pair.getY()].setGraphic(view);
    }

    public static void removeBulletPath(Pair[] pairs) {
        for (Pair pair : pairs) {
            Gui.removeObjectOnScreen(pair);
        }
    }

    public static void movePlayerOnScreen(Pair oldpos, Pair newpos, String direction) {
        removeObjectOnScreen(oldpos);
        placePlayerOnScreen(newpos, direction);
        updateScoreTable();
    }

    public static void updateScoreTable() {
        Platform.runLater(() -> {
            scoreList.setText(getScoreList());
        });
    }

    public void playerMoved(int delta_x, int delta_y, String direction) {
        GameManager.requestMove(delta_x, delta_y, direction);
    }

    public void playerShots() {
        GameManager.requestShoot();
    }

    public static String getScoreList() {
        StringBuffer buffer = new StringBuffer(100);
        for (Map.Entry<String, Integer> p : GameManager.getPlayers().entrySet()) {
            buffer.append(p.getKey()).append(" ").append(p.getValue()).append("\r\n");
        }
        return buffer.toString();
    }
}

