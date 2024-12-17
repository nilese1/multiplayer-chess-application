package gui.menus;

import chess.Piece;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import net.ChessClient;
import chess.ChessGame;

public class RulesMenu extends Menu {

    private String hostColor;
    private String guestColor;
    private String timeControl;
    private String gameType;

    private ChessClient clientThread;

    public RulesMenu(Node parent) {
        super(parent);

        assert hostColor.equals("white") || hostColor.equals("black") : "Host color must be white or black";
        assert guestColor.equals("white") || guestColor.equals("black") : "Guest color must be white or black";

        this.hostColor = "white";
        this.guestColor = "black";

        // TODO: Implement time control and game type
        this.timeControl = "null";
        this.gameType = "null";
    }

    public RulesMenu(Node parent, ChessClient clientThread) {
        this(parent);
        this.clientThread = clientThread;
    }

    @Override
    public void run() {
        Text tTitle = new Text("Rules");
        tTitle.getStyleClass().add("title");

        Text tHostColor = new Text("Selected Color: ");
        tHostColor.getStyleClass().add("text");

        Button btHost = new Button("Play as White");
        Button btJoin = new Button("Play as Black");
        
        Button btStart = new Button("Start Game");

        ImageView kingIcon = getKingIcon(hostColor);
        kingIcon.getStyleClass().add("piece-icon");

        btHost.getStyleClass().add("button");
        btJoin.getStyleClass().add("button");
        btStart.getStyleClass().add("small-button");

        btStart.setPrefSize(200, 50); 


        // Switch host color when button is clicked
        EventHandler<MouseEvent> colorSelectionHandler = e -> {
            String color = (e.getSource() == btHost) ? "white" : "black";
            setHostColor(color);
            ImageView newKingIcon = getKingIcon(hostColor);
            root.getChildren().removeIf(node -> node instanceof ImageView && node.getStyleClass().contains("piece-icon"));
            newKingIcon.getStyleClass().add("piece-icon");
            root.getChildren().add(2, newKingIcon);

            if (e.getSource() == btHost) {
                btHost.setDisable(true);
                btJoin.setDisable(false);
            } else {
                btHost.setDisable(false);
                btJoin.setDisable(true);
            }
        };

        btHost.setOnMouseClicked(colorSelectionHandler);
        btJoin.setOnMouseClicked(colorSelectionHandler);

        root.setOrientation(Orientation.VERTICAL);
        root.getChildren().add(tTitle);

        root.setOrientation(Orientation.HORIZONTAL);
        root.setAlignment(Pos.CENTER);
        root.setHgap(15);
        root.getChildren().addAll(tHostColor, kingIcon);

        root.setAlignment(Pos.CENTER);
        root.setVgap(15);
        root.getChildren().addAll(btHost, btJoin);

        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(btStart);

        btStart.setOnMouseClicked(e -> startGame());
    }

    private void startGame() {
        if (clientThread != null) {
            clientThread.setClientColor(hostColor.equals("white") ? 'w' : 'b');
            super.changeMenu(new GameMenu(parent, clientThread.getGame()));
        }
        else {
            super.changeMenu(new GameMenu(parent, new ChessGame()));
        }
    }

    private ImageView getKingIcon(String color) {
        char king = color.equals("white") ? 'K' : 'k';
        String imageFileName = Piece.FEN_TO_IMG.get(king);
        String path = getClass().getResource("/pieces/" + imageFileName).toString();
        return new ImageView(path);
    }

    public String getHostColor() {
        return hostColor;
    }

    public String getGuestColor() {
        return guestColor;
    }

    public String getTimeControl() {
        return timeControl;
    }

    public String getGameType() {
        return gameType;
    }

    public void setHostColor(String hostColor) {
        this.hostColor = hostColor;
    }

    public void setGuestColor(String guestColor) {
        this.guestColor = guestColor;
    }
}
