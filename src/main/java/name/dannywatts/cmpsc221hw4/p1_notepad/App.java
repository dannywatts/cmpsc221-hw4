package name.dannywatts.cmpsc221hw4.p1_notepad;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class App extends Application {
    // UI elements that need to be accessed by multiple functions
    private final TextArea textArea = new TextArea();
    private final FileChooser fileChooser = new FileChooser();
    private final BorderPane root = new BorderPane();
    private final Label statusBar = new Label();
    private final CheckMenuItem wordWrapItem = new CheckMenuItem("Word Wrap");
    private final CheckMenuItem statusBarItem = new CheckMenuItem("Status Bar");
    private Stage stage;

    // File state
    private Path currentPath = null; // null while the file is untitled
    private boolean modified = false;

    // View state
    private int zoomPercent = 100;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        MenuItem newItem = new MenuItem("New");
        MenuItem openItem = new MenuItem("Open...");
        MenuItem saveItem = new MenuItem("Save");
        MenuItem saveAsItem = new MenuItem("Save As...");
        MenuItem exitItem = new MenuItem("Exit");
        newItem.setOnAction(_ -> newFile());
        openItem.setOnAction(_ -> openFile());
        saveItem.setOnAction(_ -> save());
        saveAsItem.setOnAction(_ -> saveAs());
        exitItem.setOnAction(_ -> exit());

        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(newItem, openItem, saveItem, saveAsItem, exitItem);

        MenuItem undoItem = new MenuItem("Undo");
        MenuItem redoItem = new MenuItem("Redo");
        MenuItem cutItem = new MenuItem("Cut");
        MenuItem copyItem = new MenuItem("Copy");
        MenuItem pasteItem = new MenuItem("Paste");
        MenuItem deleteItem = new MenuItem("Delete");
        MenuItem selectAllItem = new MenuItem("Select All");
        undoItem.setOnAction(_ -> textArea.undo());
        redoItem.setOnAction(_ -> textArea.redo());
        cutItem.setOnAction(_ -> textArea.cut());
        copyItem.setOnAction(_ -> textArea.copy());
        pasteItem.setOnAction(_ -> textArea.paste());
        deleteItem.setOnAction(_ -> textArea.replaceSelection(""));
        selectAllItem.setOnAction(_ -> textArea.selectAll());

        // Greyed out when they would do nothing, like Notepad
        undoItem.disableProperty().bind(textArea.undoableProperty().not());
        redoItem.disableProperty().bind(textArea.redoableProperty().not());
        cutItem.disableProperty().bind(textArea.selectedTextProperty().isEmpty());
        copyItem.disableProperty().bind(textArea.selectedTextProperty().isEmpty());
        deleteItem.disableProperty().bind(textArea.selectedTextProperty().isEmpty());

        Menu editMenu = new Menu("Edit");
        editMenu.getItems().addAll(undoItem, redoItem, cutItem, copyItem, pasteItem, deleteItem, selectAllItem);

        MenuItem zoomInItem = new MenuItem("Zoom In");
        MenuItem zoomOutItem = new MenuItem("Zoom Out");
        MenuItem restoreZoomItem = new MenuItem("Restore Default Zoom");
        zoomInItem.setOnAction(_ -> zoomIn());
        zoomOutItem.setOnAction(_ -> zoomOut());
        restoreZoomItem.setOnAction(_ -> restoreZoom());
        Menu zoomMenu = new Menu("Zoom");
        zoomMenu.getItems().addAll(zoomInItem, zoomOutItem, restoreZoomItem);

        statusBarItem.setSelected(true); // shown by default, like Windows 11 Notepad
        wordWrapItem.setOnAction(_ -> toggleWordWrap());
        statusBarItem.setOnAction(_ -> toggleStatusBar());

        Menu viewMenu = new Menu("View");
        viewMenu.getItems().addAll(zoomMenu, statusBarItem, wordWrapItem);

        MenuBar menuBar = new MenuBar(fileMenu, editMenu, viewMenu);

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Documents (*.txt)", "*.txt"),
                new FileChooser.ExtensionFilter("All Files (*.*)", "*.*"));

        // Any edit marks the file as having unsaved changes
        textArea.textProperty().addListener((_, _, _) -> {
            modified = true;
            updateTitle();
        });

        // Keep the line and column in the status bar up to date as the cursor moves
        textArea.caretPositionProperty().addListener((_, _, _) -> updateStatusBar());

        // Clicking the window's X asks to save first, and Cancel keeps the window open
        stage.setOnCloseRequest(event -> {
            if (!confirmDiscardChanges()) {
                event.consume();
            }
        });

        root.setTop(menuBar);
        root.setCenter(textArea);
        root.setBottom(statusBar);

        updateTitle();
        updateStatusBar();
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }

    private void newFile() {
        if (confirmDiscardChanges()) {
            textArea.clear();
            currentPath = null;
            modified = false; // clearing the text fired the listener
            updateTitle();
        }
    }

    private void openFile() {
        if (!confirmDiscardChanges()) {
            return;
        }
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            loadFile(file.toPath());
        }
    }

    // Returns false if the file was not saved (cancelled or failed)
    private boolean save() {
        if (currentPath == null) {
            return saveAs();
        } else {
            return writeFile(currentPath);
        }
    }

    // Returns false if the file was not saved (cancelled or failed)
    private boolean saveAs() {
        if (currentPath == null) {
            fileChooser.setInitialFileName("Untitled.txt");
        } else {
            fileChooser.setInitialFileName(currentPath.getFileName().toString());
        }
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) {
            return false;
        } else {
            return writeFile(file.toPath());
        }
    }

    private void exit() {
        // stage.close() does not trigger the close request, so the user is only asked once
        if (confirmDiscardChanges()) {
            stage.close();
        }
    }

    private void zoomIn() {
        if (zoomPercent < 500) {
            zoomPercent += 10;
            applyZoom();
        }
    }

    private void zoomOut() {
        if (zoomPercent > 10) {
            zoomPercent -= 10;
            applyZoom();
        }
    }

    private void restoreZoom() {
        zoomPercent = 100;
        applyZoom();
    }

    private void applyZoom() {
        textArea.setFont(Font.font(Font.getDefault().getSize() * zoomPercent / 100));
        updateStatusBar();
    }

    private void toggleWordWrap() {
        textArea.setWrapText(wordWrapItem.isSelected());
    }

    private void toggleStatusBar() {
        if (statusBarItem.isSelected()) {
            root.setBottom(statusBar);
        } else {
            root.setBottom(null);
        }
    }

    // Shows the cursor's line and column (both starting at 1) and the zoom level
    private void updateStatusBar() {
        String text = textArea.getText();
        int caret = textArea.getCaretPosition();
        int line = 1;
        int lineStart = 0;
        for (int i = 0; i < caret; i++) {
            if (text.charAt(i) == '\n') {
                line++;
                lineStart = i + 1;
            }
        }
        int column = caret - lineStart + 1;
        statusBar.setText("Ln " + line + ", Col " + column + "    " + zoomPercent + "%");
    }

    private void loadFile(Path path) {
        try {
            String text = Files.readString(path);
            textArea.setText(text);
            currentPath = path;
            modified = false; // setting the text fired the listener
            updateTitle();
        } catch (IOException e) {
            showError("Could not open " + path.getFileName() + ".\n" + e.getMessage());
        }
    }

    // Returns false if writing failed
    private boolean writeFile(Path path) {
        try {
            Files.writeString(path, textArea.getText());
            currentPath = path;
            modified = false;
            updateTitle();
            return true;
        } catch (IOException e) {
            showError("Could not save " + path.getFileName() + ".\n" + e.getMessage());
            return false;
        }
    }

    // Asks to save unsaved changes. Returns true if it is OK to continue (saved or discarded)
    private boolean confirmDiscardChanges() {
        if (!modified) {
            return true;
        }

        ButtonType saveButton = new ButtonType("Save");
        ButtonType dontSaveButton = new ButtonType("Don't Save");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Do you want to save changes to " + getFileName() + "?",
                saveButton, dontSaveButton, ButtonType.CANCEL);
        alert.initOwner(stage);
        alert.setTitle("Notepad");
        alert.setHeaderText(null);
        alert.showAndWait();

        // Closing the dialog with its X counts as Cancel
        if (alert.getResult() == saveButton) {
            return save();
        } else return alert.getResult() == dontSaveButton;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.initOwner(stage);
        alert.setTitle("Notepad");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    // Shows "*" in front of the title when there are unsaved changes, like Windows Notepad
    private void updateTitle() {
        if (modified) {
            stage.setTitle("*" + getFileName() + " - Notepad");
        } else {
            stage.setTitle(getFileName() + " - Notepad");
        }
    }

    private String getFileName() {
        if (currentPath == null) {
            return "Untitled";
        } else {
            return currentPath.getFileName().toString();
        }
    }
}
