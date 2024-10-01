package game;

import java.net.*;
import java.io.*;

import javafx.application.Application;

public class App {
    public static void main(String[] args) throws Exception {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Indtast spillernavn");
        String name = inFromUser.readLine();
        Socket clientSocket = new Socket("10.10.132.216", 6789);
        GameManager.initializeConnection(clientSocket);
        GameManager.requestGameState();
        GameManager.setPlayerName(name);
        GameManager.requestAddPlayer(name);

//        GameManager.createDefaultPlayers();
//        GameLogic.makePlayers("OldPeter");
        Application.launch(Gui.class);
    }
}
