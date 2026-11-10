package name.dannywatts.cmpsc221hw4.p4_fractal;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class App extends Application {
    // General pattern of fractal
    private final int[][] PATTERN = {{0, 0}, {2, 0}, {1, 1}, {0, 2}, {2, 2}};

    // UI elements that need to be accessed by multiple functions
    private final BorderPane root = new BorderPane();
    private final TextField iterationField = new TextField("1");
    private final Label errorLabel = new Label();

    @Override
    public void start(Stage stage) {
        Button generateButton = new Button("Generate");
        generateButton.setOnAction(_ -> regenerate());
        iterationField.setOnAction(_ -> regenerate()); // Makes enter work the same as button

        HBox controls = new HBox(8, new Label("Iteration (1-6):"), iterationField, generateButton);
        errorLabel.setStyle("-fx-text-fill: red;");

        VBox top = new VBox(4, controls, errorLabel);
        root.setTop(top);

        regenerate();

        stage.setTitle("Vicsek fractal generator");
        stage.setScene(new Scene(root));
        stage.show();
    }

    // Labelled method because it is called by multiple events
    private void regenerate() {
        int iteration;
        try {
            iteration = Integer.parseInt(iterationField.getText().trim());
        } catch (NumberFormatException e) {
            errorLabel.setText("Please enter a whole number.");
            return;
        }

        if (iteration < 1 || iteration > 6) {
            errorLabel.setText("Iteration must be between 1 and 6 to fit within window.");
            return;
        }

        errorLabel.setText("");
        root.setCenter(generateFractal(iteration, 729));
    }

    private Node generateFractal(int level, double size) {
        if (level == 0) {
            return new Rectangle(size, size);
        } else {
            GridPane grid = new GridPane();
            // Without this, on a scaled display (e.g. 125%) each cell is rounded up to whole screen pixels,
            // and that rounding compounds through every level until the fractal outgrows the window.
            grid.setSnapToPixel(false);
            grid.setMaxSize(size, size);
            for (int[] cell : PATTERN) {
                grid.add(generateFractal(level - 1, size / 3), cell[0], cell[1]);
            }
            return grid;
        }
    }
}
