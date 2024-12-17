package gui.windows;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PromptWindow extends Window {
    private String prompt;
    private String username = "null";

    public PromptWindow(String prompt) {
        this.prompt = prompt;
    }

    public Pane loadPane() {
        FlowPane root = new FlowPane();

        Text txtPrompt = new Text(prompt);
        txtPrompt.getStyleClass().add("prompt-text");

        TextField tfPrompt = new TextField();
        tfPrompt.getStyleClass().add("prompt-field");

        Button btConfirm = new Button("Confirm");
        btConfirm.getStyleClass().add("button");

        btConfirm.setOnAction(e -> updateUsername(tfPrompt));

        root.getChildren().addAll(txtPrompt, tfPrompt, btConfirm);

        return root;
    }

    public void updateUsername(TextField tf) {
        String newUsername = tf.getText();

        if (newUsername.isEmpty())
            newUsername = "null";

        this.username = newUsername;

        super.close();
    }

    public void setTitle(Stage stage) {
        stage.setTitle("Prompt");
    }

    public String getUsername() { return username; }
}
