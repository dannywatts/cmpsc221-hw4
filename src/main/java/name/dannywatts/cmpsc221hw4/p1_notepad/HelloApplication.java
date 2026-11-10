package name.dannywatts.cmpsc221hw4.p1_notepad;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class HelloApplication extends Application {
    private File file;
    private boolean isSaved = true;

    @Override
    public void start(Stage stage) {
        FileChooser fileChooser = new FileChooser();

        // Create UI
        BorderPane root = new BorderPane();

        TextArea textArea = new TextArea();
        textArea.setFont(new Font("Consolas", 14));
        textArea.setOnKeyTyped(e -> {
            // TODO
        });

        HBox statusBar = new HBox();
        Label cursorPosition = new Label("Line 1, column 1");
        Label numCharacters = new Label("0 characters");
        Label zoomFactor = new Label("100%");
        statusBar.getChildren().setAll(cursorPosition, numCharacters, zoomFactor);

        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem newMenuItem = new MenuItem("New");
        newMenuItem.setOnAction(e -> {
            // TODO: File -> New
        });
        MenuItem openMenuItem = new MenuItem("Open");
        openMenuItem.setOnAction(e -> {
            // TODO: File -> Open
            file = fileChooser.showOpenDialog(stage);
        });
        Menu openRecentMenu = new Menu("Open recent");
        // TODO: Populate "Open recent" menu
        MenuItem saveMenuItem = new MenuItem("Save");
        saveMenuItem.setOnAction(e -> {
            // TODO: File -> Save
            if (file == null) {
                fileChooser.showSaveDialog(stage);
            } else {
                // TODO: Save to opened file
            }
        });
        MenuItem saveAsMenuItem = new MenuItem("Save as");
        saveAsMenuItem.setOnAction(e -> {
            // TODO: File -> Save as
            fileChooser.showSaveDialog(stage);
        });
        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(e -> {
            // TODO: File -> Exit
        });
        fileMenu.getItems().setAll(newMenuItem, openMenuItem, openRecentMenu, saveMenuItem, saveAsMenuItem, exitMenuItem);

        Menu editMenu = new Menu("Edit");
        MenuItem undoMenuItem = new MenuItem("Undo");
        undoMenuItem.setOnAction(e -> {
            // TODO: Edit -> Undo
        });
        MenuItem redoMenuItem = new MenuItem("Redo");
        redoMenuItem.setOnAction(e -> {
            // TODO: Edit -> Redo
        });
        MenuItem copyMenuItem = new MenuItem("Copy");
        copyMenuItem.setOnAction(e -> {
            // TODO: Edit -> Copy
        });
        MenuItem cutMenuItem = new MenuItem("Cut");
        cutMenuItem.setOnAction(e -> {
            // TODO: Edit -> Cut
        });
        MenuItem pasteMenuItem = new MenuItem("Paste");
        pasteMenuItem.setOnAction(e -> {
            // TODO: Edit -> Paste
        });
        MenuItem findMenuItem = new MenuItem("Find");
        findMenuItem.setOnAction(e -> {
            // TODO: Edit -> Find
        });
        MenuItem findAndReplaceMenuItem = new MenuItem("Find and replace");
        findAndReplaceMenuItem.setOnAction(e -> {
            // TODO: Edit -> Find and replace
        });
        MenuItem fontMenuItem = new MenuItem("Font");
        fontMenuItem.setOnAction(e -> {
            // TODO: Edit -> Font
        });
        editMenu.getItems().setAll(undoMenuItem, redoMenuItem, copyMenuItem, cutMenuItem, pasteMenuItem, findMenuItem, findAndReplaceMenuItem, fontMenuItem);

        Menu viewMenu = new Menu("View");
        MenuItem zoomOutMenuItem = new MenuItem("Zoom out 10%");
        zoomOutMenuItem.setOnAction(e -> {
            // TODO: View -> Zoom out 10%
        });
        MenuItem zoomInMenuItem = new MenuItem("Zoom in 10%");
        zoomInMenuItem.setOnAction(e -> {
            // TODO: View -> Zoom in 10%
        });
        MenuItem resetZoomMenuItem = new MenuItem("Reset zoom to 100%");
        resetZoomMenuItem.setOnAction(e -> {
            // TODO: View -> Reset zoom to 100%
        });
        MenuItem toggleWordWrapMenuItem = new MenuItem("Toggle word wrap");
        toggleWordWrapMenuItem.setOnAction(e -> {
            // TODO: View -> Toggle word wrap
        });
        MenuItem toggleStatusBarMenuItem = new MenuItem("Toggle status bar");
        toggleStatusBarMenuItem.setOnAction(e -> {
            // TODO: View -> Toggle status bar
        });
        viewMenu.getItems().setAll(zoomOutMenuItem, zoomInMenuItem, resetZoomMenuItem, toggleWordWrapMenuItem, toggleStatusBarMenuItem);

        menuBar.getMenus().setAll(fileMenu, editMenu, viewMenu);

        root.setTop(menuBar);
        root.setCenter(textArea);
        root.setBottom(statusBar);

        // Initialize window with UI
        Scene scene = new Scene(root);
        stage.setTitle("Notepad");
        stage.setScene(scene);
        stage.show();
    }
}
