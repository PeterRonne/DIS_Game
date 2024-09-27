package game;

import java.net.*;
import java.io.*;

import javafx.application.Application;

public class App {
    public static void main(String[] args) throws Exception {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Indtast spillernavn");
//        String navn = inFromUser.readLine();
        Socket clientSocket = new Socket("localhost", 6789);
        GameManager.initializeConnection(clientSocket);

        GameLogic.makePlayers("Peter");
        Application.launch(Gui.class);
    }
}
;