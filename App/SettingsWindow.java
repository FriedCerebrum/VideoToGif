package App;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SettingsWindow extends Stage {
    private TextField startTimeField;
    private TextField durationField;
    private TextField widthField;
    private TextField heightField;
    private TextField fpsField;
    private TextField qualityField;
    private boolean settingsChanged = false;

    public SettingsWindow(Stage owner) {
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Settings");

        startTimeField = new TextField("0");
        durationField = new TextField("10");
        widthField = new TextField("640");
        heightField = new TextField("480");
        fpsField = new TextField("10");
        qualityField = new TextField("10");

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            settingsChanged = true;
            close();
        });

        GridPane layout = new GridPane();
        layout.add(new Label("Start Time (s):"), 0, 0);
        layout.add(startTimeField, 1, 0);
        layout.add(new Label("Duration (s):"), 0, 1);
        layout.add(durationField, 1, 1);
        layout.add(new Label("Width:"), 0, 2);
        layout.add(widthField, 1, 2);
        layout.add(new Label("Height:"), 0, 3);
        layout.add(heightField, 1, 3);
        layout.add(new Label("FPS:"), 0, 4);
        layout.add(fpsField, 1, 4);
        layout.add(new Label("Quality (1-31):"), 0, 5);
        layout.add(qualityField, 1, 5);
        layout.add(saveButton, 0, 6, 2, 1);

        Scene scene = new Scene(layout, 300, 250);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        setScene(scene);
    }

    public int getStartTime() {
        return Integer.parseInt(startTimeField.getText());
    }

    public int getDuration() {
        return Integer.parseInt(durationField.getText());
    }

    /* public double getWidth() {
        return Double.parseDouble(widthField.getText());
    } */

    public double getVideoWidth() {
        return Double.parseDouble(widthField.getText());
    }

    /* public double getHeight() {
        return Double.parseDouble(heightField.getText());
    } */

    public double getVideoHeight() {
        return Double.parseDouble(heightField.getText());
    }

    public int getFps() {
        return Integer.parseInt(fpsField.getText());
    }

    public int getQuality() {
        return Integer.parseInt(qualityField.getText());
    }

    public boolean isSettingsChanged() {
        return settingsChanged;
    }
}

