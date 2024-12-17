package gui.menus;

import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

public abstract class Menu extends Pane implements Runnable {
    FlowPane root;
    Node parent;

    public Menu(Node parent) {
        this.parent = parent;
        this.root = new FlowPane();
        getChildren().add(root);
    }

    public void changeMenu(Menu menu) {
        getChildren().clear();

        getChildren().add(menu);

        menu.run();
    }

    public FlowPane getRoot() {
        return root;
    }

    @Override
    public abstract void run();
}
