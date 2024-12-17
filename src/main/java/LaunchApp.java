import chess.ChessGame;
import gui.menus.MainMenu;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LaunchApp extends Application {
    private static Stage pStage;

    @Override
    public void start(Stage primaryStage) {
        System.out.println("Starting application...");

        pStage = primaryStage;
        ChessGame game = new ChessGame();
        Scene root = new Scene(new Group());

        root.getStylesheets().add(getClass().getResource("/uicss/defaultStylesheet.css").toExternalForm());

        ObservableList<Node> scene = ((Group)root.getRoot()).getChildren();

        MainMenu ui = new MainMenu(root.getRoot());

        addMainMenu(scene, ui);

        addOnCloseListener(primaryStage);

        primaryStage.setScene(root);
        primaryStage.show();
    }

    private void addMainMenu(ObservableList<Node> scene, MainMenu ui) {
        Thread uiThread = new Thread(ui);
        uiThread.start();

        scene.add(ui);
    }

    private void addOnCloseListener(Stage primaryStage) {
        primaryStage.setOnCloseRequest(e -> {
            System.exit(0);
        });
    }

    private void addResizeListener(Scene root, ChessGame game) {
        // Allows window to resize along with height
        root.heightProperty().addListener((observableValue, number, t1) -> {
            game.resize(root.getHeight());
        });
    }

    public static void main(String[] args) { launch(args); }

    public static Stage getpStage() { return pStage; }
}
