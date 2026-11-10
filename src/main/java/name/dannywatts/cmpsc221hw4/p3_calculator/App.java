package name.dannywatts.cmpsc221hw4.p3_calculator;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {
    // UI elements that need to be accessed by multiple functions
    private final TextField display = new TextField("0");
    private final Label memoryLabel = new Label();

    // Calculator state
    private double value = 0;
    private String pendingOperator = null;
    private boolean startNewEntry = true;
    private double memory = 0;
    private boolean error = false; // display is showing an error message instead of a number

    @Override
    public void start(Stage stage) {
        display.setEditable(false);

        // Memory column
        Button memoryClearButton = new Button("MC");
        Button memoryRecallButton = new Button("MR");
        Button memoryStoreButton = new Button("MS");
        Button memoryAddButton = new Button("M+");
        memoryClearButton.setOnAction(_ -> memoryClear());
        memoryRecallButton.setOnAction(_ -> memoryRecall());
        memoryStoreButton.setOnAction(_ -> memoryStore());
        memoryAddButton.setOnAction(_ -> memoryAdd());

        // Clearing row
        Button backspaceButton = new Button("Backspace");
        Button clearEntryButton = new Button("CE");
        Button clearButton = new Button("C");
        backspaceButton.setOnAction(_ -> backspace());
        clearEntryButton.setOnAction(_ -> clearEntry());
        clearButton.setOnAction(_ -> clear());

        // Digits
        Button button0 = new Button("0");
        Button button1 = new Button("1");
        Button button2 = new Button("2");
        Button button3 = new Button("3");
        Button button4 = new Button("4");
        Button button5 = new Button("5");
        Button button6 = new Button("6");
        Button button7 = new Button("7");
        Button button8 = new Button("8");
        Button button9 = new Button("9");
        button0.setOnAction(_ -> appendDigit("0"));
        button1.setOnAction(_ -> appendDigit("1"));
        button2.setOnAction(_ -> appendDigit("2"));
        button3.setOnAction(_ -> appendDigit("3"));
        button4.setOnAction(_ -> appendDigit("4"));
        button5.setOnAction(_ -> appendDigit("5"));
        button6.setOnAction(_ -> appendDigit("6"));
        button7.setOnAction(_ -> appendDigit("7"));
        button8.setOnAction(_ -> appendDigit("8"));
        button9.setOnAction(_ -> appendDigit("9"));

        // Operators and functions
        Button divideButton = new Button("/");
        Button multiplyButton = new Button("*");
        Button subtractButton = new Button("-");
        Button addButton = new Button("+");
        Button sqrtButton = new Button("sqrt");
        Button percentButton = new Button("%");
        Button reciprocalButton = new Button("1/x");
        Button negateButton = new Button("+/-");
        Button decimalButton = new Button(".");
        Button equalsButton = new Button("=");
        divideButton.setOnAction(_ -> setOperator("/"));
        multiplyButton.setOnAction(_ -> setOperator("*"));
        subtractButton.setOnAction(_ -> setOperator("-"));
        addButton.setOnAction(_ -> setOperator("+"));
        sqrtButton.setOnAction(_ -> squareRoot());
        percentButton.setOnAction(_ -> percent());
        reciprocalButton.setOnAction(_ -> reciprocal());
        negateButton.setOnAction(_ -> negate());
        decimalButton.setOnAction(_ -> decimalPoint());
        equalsButton.setOnAction(_ -> evaluate());

        // Same size for every square button (and the memory indicator) so the columns line up
        Button[] squareButtons = {memoryClearButton, memoryRecallButton, memoryStoreButton, memoryAddButton,
                button0, button1, button2, button3, button4, button5, button6, button7, button8, button9,
                divideButton, multiplyButton, subtractButton, addButton, sqrtButton, percentButton,
                reciprocalButton, negateButton, decimalButton, equalsButton};
        for (Button button : squareButtons) {
            button.setPrefSize(50, 35);
        }
        memoryLabel.setPrefSize(50, 35);

        // The three clearing buttons together are as wide as the keypad: 3 * 86 + 2 * 6 = 5 * 50 + 4 * 5
        backspaceButton.setPrefSize(86, 35);
        clearEntryButton.setPrefSize(86, 35);
        clearButton.setPrefSize(86, 35);

        GridPane keypad = new GridPane();
        keypad.setHgap(5);
        keypad.setVgap(5);
        keypad.add(button7, 0, 0);
        keypad.add(button8, 1, 0);
        keypad.add(button9, 2, 0);
        keypad.add(divideButton, 3, 0);
        keypad.add(sqrtButton, 4, 0);
        keypad.add(button4, 0, 1);
        keypad.add(button5, 1, 1);
        keypad.add(button6, 2, 1);
        keypad.add(multiplyButton, 3, 1);
        keypad.add(percentButton, 4, 1);
        keypad.add(button1, 0, 2);
        keypad.add(button2, 1, 2);
        keypad.add(button3, 2, 2);
        keypad.add(subtractButton, 3, 2);
        keypad.add(reciprocalButton, 4, 2);
        keypad.add(button0, 0, 3);
        keypad.add(negateButton, 1, 3);
        keypad.add(decimalButton, 2, 3);
        keypad.add(addButton, 3, 3);
        keypad.add(equalsButton, 4, 3);

        VBox memoryColumn = new VBox(5, memoryLabel, memoryClearButton, memoryRecallButton, memoryStoreButton, memoryAddButton);
        HBox clearingRow = new HBox(6, backspaceButton, clearEntryButton, clearButton);
        VBox mainColumn = new VBox(5, clearingRow, keypad);

        BorderPane root = new BorderPane();
        root.setTop(display); // spans the whole width of the window
        root.setCenter(new HBox(10, memoryColumn, mainColumn));

        stage.setTitle("Calculator");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void appendDigit(String digit) {
        resetIfError();
        if (startNewEntry || display.getText().equals("0")) {
            display.setText(digit);
            startNewEntry = false;
        } else {
            display.setText(display.getText() + digit);
        }
    }

    private void decimalPoint() {
        resetIfError();
        if (startNewEntry) {
            display.setText("0.");
            startNewEntry = false;
        } else if (!display.getText().contains(".")) {
            display.setText(display.getText() + ".");
        }
    }

    private void setOperator(String operator) {
        resetIfError();
        if (pendingOperator != null && !startNewEntry) {
            // Chained operation like 2 + 3 + : finish 2 + 3 first (left to right, like Windows)
            calculate();
            if (error) {
                return;
            }
        } else if (pendingOperator == null) {
            value = getDisplayValue();
        }
        // Otherwise an operator was pressed twice in a row, so just switch to the new one
        pendingOperator = operator;
        startNewEntry = true;
    }

    private void evaluate() {
        resetIfError();
        if (pendingOperator != null) {
            calculate();
            pendingOperator = null;
        }
        startNewEntry = true;
    }

    // Applies the pending operator to the accumulator and the displayed number
    private void calculate() {
        double current = getDisplayValue();
        double result;
        switch (pendingOperator) {
            case "+" -> result = value + current;
            case "-" -> result = value - current;
            case "*" -> result = value * current;
            default -> {
                if (current == 0) {
                    showError("Cannot divide by zero");
                    return;
                }
                result = value / current;
            }
        }
        value = result;
        showNumber(result);
    }

    private void squareRoot() {
        resetIfError();
        double value = getDisplayValue();
        if (value < 0) {
            showError("Invalid input");
        } else {
            showNumber(Math.sqrt(value));
            startNewEntry = true;
        }
    }

    private void reciprocal() {
        resetIfError();
        double value = getDisplayValue();
        if (value == 0) {
            showError("Cannot divide by zero");
        } else {
            showNumber(1 / value);
            startNewEntry = true;
        }
    }

    // Like Windows: 200 + 10 % turns the 10 into 20 (10% of 200)
    private void percent() {
        resetIfError();
        showNumber(value * getDisplayValue() / 100);
        startNewEntry = true;
    }

    private void negate() {
        resetIfError();
        String text = display.getText();
        if (text.startsWith("-")) {
            display.setText(text.substring(1));
        } else if (!text.equals("0")) {
            display.setText("-" + text);
        }
    }

    private void backspace() {
        resetIfError();
        // Only the number being typed can be edited, not a calculated result
        if (startNewEntry) {
            return;
        }
        String text = display.getText();
        text = text.substring(0, text.length() - 1);
        if (text.isEmpty() || text.equals("-")) {
            display.setText("0");
        } else {
            display.setText(text);
        }
    }

    private void clearEntry() {
        resetIfError();
        display.setText("0");
        startNewEntry = true;
    }

    private void clear() {
        display.setText("0");
        value = 0;
        pendingOperator = null;
        startNewEntry = true;
        error = false;
    }

    private void memoryClear() {
        memory = 0;
        updateMemoryLabel();
    }

    private void memoryRecall() {
        resetIfError();
        showNumber(memory);
        startNewEntry = true;
    }

    private void memoryStore() {
        resetIfError();
        memory = getDisplayValue();
        updateMemoryLabel();
        startNewEntry = true;
    }

    private void memoryAdd() {
        resetIfError();
        memory += getDisplayValue();
        updateMemoryLabel();
        startNewEntry = true;
    }

    // Shows "M" next to the memory buttons when something is stored, like Windows
    private void updateMemoryLabel() {
        if (memory != 0) {
            memoryLabel.setText("M");
        } else {
            memoryLabel.setText("");
        }
    }

    private double getDisplayValue() {
        return Double.parseDouble(display.getText());
    }

    private void showNumber(double value) {
        // Whole numbers are shown without ".0"
        if (value == Math.floor(value) && Math.abs(value) < 1e15) {
            display.setText(String.valueOf((long) value));
        } else {
            display.setText(Double.toString(value));
        }
    }

    private void showError(String message) {
        clear();
        display.setText(message);
        error = true;
    }

    // Any button pressed after an error starts over, so the message is never read as a number
    private void resetIfError() {
        if (error) {
            clear();
        }
    }
}
