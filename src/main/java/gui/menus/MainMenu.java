package gui.menus;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import net.ChessClient;

public class MainMenu extends Menu {
    private ChessClient clientThread;

    public MainMenu(Node parent, ChessClient clientThread) {
        super(parent);
        this.clientThread = clientThread;
    }

    public MainMenu(Node parent) {
        super(parent);
    }

    @Override
    public void run() {
        Text tTitle = new Text("Chess");
        tTitle.getStyleClass().add("title");

        Button btSolo = new Button("Pass and Play");
        btSolo.getStyleClass().add("button");

        Button btMultiplayer = new Button("Online Multiplayer");
        btMultiplayer.getStyleClass().add("button");

        addButtonListeners(btSolo, btMultiplayer);

        root.setOrientation(Orientation.VERTICAL);
        root.setAlignment(Pos.CENTER);
        root.setVgap(15);

        root.getChildren().addAll(tTitle, btSolo, btMultiplayer);
    }

    public void addButtonListeners(Button btSolo, Button btMultiplayer) {
        btSolo.setOnMouseClicked(e -> super.changeMenu(new RulesMenu(parent, clientThread)));
        btMultiplayer.setOnMouseClicked(e -> super.changeMenu(new MultiplayerMenu(parent)));
    }
}
