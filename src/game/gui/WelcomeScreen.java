package game.gui;

import game.controller.GameManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WelcomeScreen extends Application {
    private static final TextField serverName = new TextField();
    private static final TextField portNumber = new TextField();
    private final TextField playerName = new TextField();
    private final Button connectBtn = new Button("Connect to server");
    private final Button submitBtn = new Button("Submit Name");

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(10));

        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getChildren().addAll(new Label("Server Name:"), serverName, new Label("Port Number:"), portNumber, connectBtn);
        pane.setCenter(box);
        connectBtn.setOnAction(actionEvent -> createConnection(pane));
        submitBtn.setOnAction(actionEvent -> submitName(stage));
        stage.setOnCloseRequest(event -> System.exit(0));
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.setTitle("Connection Setup");
        stage.show();
    }

    private void submitName(Stage stage) {
        String name = playerName.getText();
        Alert alert;
        if (!name.isEmpty()) {
            GameManager.setPlayerName(name);
            if (GameManager.validateName(name)) {
                stage.hide();
            } else {
                alert = new Alert(Alert.AlertType.ERROR, "Navnet er i brug. Vælg venligst et andet navn.");
                alert.show();
            }
        } else {
            alert = new Alert(Alert.AlertType.INFORMATION, "Indtast venligst et navn.");
            alert.show();
        }
    }

    private void createConnection(BorderPane pane) {
        boolean connected = GameManager.initializeConnection();
        if (connected) {
            GameManager.requestCurrentPlayers();
            VBox nameBox = new VBox(10, new Label("Player Name:"), playerName, submitBtn);
            pane.setCenter(nameBox);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to create connection");
            alert.show();
        }
    }

    public static void updateConnectionInfo(String name, String port) {
        Platform.runLater(() -> {
            serverName.setText(name);
            portNumber.setText(port);
        });
    }
}
