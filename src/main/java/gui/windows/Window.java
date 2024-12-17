package gui.windows;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;


public abstract class Window implements Runnable {
    private Stage stage;
    private Pane root;

    public Window() {
    }

    @Override
    public void run() {
        root = loadPane();
        stage = new Stage();

        Platform.runLater(() -> {
            setTitle(stage);

            Scene scene = new Scene(root);

            // Stage parent = LaunchApp.pStage;

            stage.setScene(scene);
        });

        stage.showAndWait();
    }

    void close() {
        stage.close();
    }

    public abstract Pane loadPane();
    public abstract void setTitle(Stage stage);


}
