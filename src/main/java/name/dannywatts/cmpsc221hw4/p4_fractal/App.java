package name.dannywatts.cmpsc221hw4.p4_fractal;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.w3c.dom.css.Rect;

public class App extends Application {
    private VBox root;
    private HBox form;

    @Override
    public void start(Stage stage) {
        Label label = new Label("Level:");
        TextField field = new TextField();
        Button button = new Button("Generate");
        button.setOnAction(e -> {
            root.getChildren().setAll(form, generateFractal(Integer.parseInt(field.getText()), stage.getScene().getWidth()));
        });
        form = new HBox();
        form.getChildren().setAll(label, field, button);

        root = new VBox();
        root.getChildren().setAll(form);

        Scene scene = new Scene(root, 300, 400);
        stage.setTitle("Vicsek fractal generator");
        stage.setScene(scene);
        stage.show();
    }

    BorderPane generateFractal(int level, double width) {
        BorderPane out = new BorderPane();
        out.setMaxWidth(width);

        // Base case
        if (level == 1) {
            Rectangle topLeft = new Rectangle(width / 3, width / 3);
            topLeft.setFill(Color.BLACK);
            Rectangle topRight = new Rectangle(width / 3, width / 3);
            topRight.setFill(Color.BLACK);
            BorderPane top = new BorderPane();
            top.setLeft(topLeft);
            top.setRight(topRight);
            out.setTop(top);

            Rectangle center = new Rectangle(width / 3, width / 3);
            center.setFill(Color.BLACK);
            out.setCenter(center);

            Rectangle bottomLeft = new Rectangle(width / 3, width / 3);
            bottomLeft.setFill(Color.BLACK);
            Rectangle bottomRight = new Rectangle(width / 3, width / 3);
            bottomRight.setFill(Color.BLACK);
            BorderPane bottom = new BorderPane();
            bottom.setLeft(bottomLeft);
            bottom.setRight(bottomRight);
            out.setBottom(bottom);
        }
        // Recursive case
        else {
            BorderPane top = new BorderPane();
            out.setTop(top);

            BorderPane center = new BorderPane();
            Rectangle centerLeft = new Rectangle(width * (1 - Math.pow((double) 1 / 3, level - 1)) / 2, width * (1 - Math.pow((double) 1 / 3, level - 1)) / 2);
            centerLeft.setFill(Color.WHITE);
            center.setLeft(centerLeft);
            center.setCenter(generateFractal(1, width * Math.pow((double) 1 / 3, level - 1)));
            Rectangle centerRight = new Rectangle(width * (1 - Math.pow((double) 1 / 3, level - 1)) / 2, width * (1 - Math.pow((double) 1 / 3, level - 1)) / 2);
            centerRight.setFill(Color.WHITE);
            center.setRight(centerRight);
            out.setCenter(center);

            BorderPane bottom = new BorderPane();
            out.setBottom(bottom);
        }

        return out;
    }
}
