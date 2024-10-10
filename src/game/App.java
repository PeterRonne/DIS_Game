package game;

import game.controller.GameManager;
import game.gui.Gui;
import game.gui.WelcomeScreen;
import game.threads.BroadcastReceiverThread;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class App extends Application {
    public static void main(String[] args) throws Exception {

        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        BroadcastReceiverThread receiver = new BroadcastReceiverThread();
        receiver.start();

        WelcomeScreen welcomeScreen = new WelcomeScreen();
        welcomeScreen.start(stage);

        stage.setOnHiding(event -> {

            GameManager.requestGameState(0);
            GameManager.requestAddPlayer();

            Platform.runLater(() -> {
                Gui gui = new Gui();
                gui.start(stage);
            });
        });
    }
}