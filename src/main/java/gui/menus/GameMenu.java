package gui.menus;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;

import java.io.IOException;

import chess.ChessGame;

public class GameMenu extends Menu {
    private ChessGame game;

    public GameMenu(Node parent, ChessGame chessGame) {
        super(parent);
        this.game = chessGame;
    }

    @Override
    public void run() {
        Thread gameThread = new Thread(game);
        Platform.runLater(gameThread);

        Button btHelp = new Button("Help");
        btHelp.getStyleClass().add("button");

        btHelp.setOnMouseClicked(e -> {
            try {
                game.getChessClient().loginToServer();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        root.getChildren().addAll(game, btHelp);
    }
}
