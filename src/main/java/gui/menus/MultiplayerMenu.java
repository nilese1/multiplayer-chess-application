package gui.menus;

import gui.windows.PromptWindow;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import net.ChessClient;
import net.ChessServer;
import javafx.application.Platform;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.CountDownLatch;

public class MultiplayerMenu extends Menu {
    public MultiplayerMenu(Node parent) {
        super(parent);
    }

    @Override
    public void run() {
        Text tTitle = new Text("Multiplayer");
        tTitle.getStyleClass().add("title");

        Button btHost = new Button("Host");
        btHost.getStyleClass().add("button");

        Button btJoin = new Button("Join");
        btJoin.getStyleClass().add("button");

        addButtonListeners(btHost, btJoin);

        root.setOrientation(Orientation.VERTICAL);
        root.setAlignment(Pos.CENTER);
        root.setVgap(15);

        root.getChildren().addAll(tTitle, btHost, btJoin);
    }

    private void addButtonListeners(Button btHost, Button btJoin) {
        btHost.setOnMouseClicked(e -> hostGame());
        btJoin.setOnMouseClicked(e -> joinGame(false));
    }

    private void hostGame() {
        Thread serverThread = new ChessServer(1444);
        serverThread.start();
        joinGame(true);
    }

    private void joinGame(boolean isHost) {
        try {
            String username = promptUsername();
            ChessClient clientThread = new ChessClient(InetAddress.getLocalHost(), 1444, username, isHost);

            // Only host can see rules menu
            if (isHost)
                super.changeMenu(new RulesMenu(parent, clientThread));
            else
                super.changeMenu(new GameMenu(parent, clientThread.getGame()));

            clientThread.start();
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private String promptUsername() {
        PromptWindow promptWindow = new PromptWindow("What is your username?");
        promptWindow.run();
        
        return promptWindow.getUsername();
    }
}
