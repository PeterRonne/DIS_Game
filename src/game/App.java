package game;

import java.net.*;
import java.io.*;

import javafx.application.Application;

public class App {
    public static void main(String[] args) throws Exception {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Indtast spillernavn");
        String name = inFromUser.readLine();
        Socket clientSocket = new Socket("10.10.132.213", 6789);
        GameManager.initializeConnection(clientSocket);
        GameManager.requestGameState(0);
        GameManager.setPlayerName(name);
        GameManager.requestAddPlayer(name);
        Application.launch(Gui.class);
    }
}
