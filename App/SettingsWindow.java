package App;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.Insets;

public class SettingsWindow extends Stage {
    private TextField startTimeField;
    private TextField durationField;
    private TextField widthField;
    private TextField heightField;
    private TextField fpsField;
    private TextField qualityField;
    private boolean settingsChanged = false;
    private AppConfig config;

    public SettingsWindow(Stage owner, AppConfig config) {
        this.config = config;
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Settings");

        // Initialize fields with values from config
        startTimeField = new TextField(String.valueOf(config.getIntProperty("startTime", 0)));
        durationField = new TextField(String.valueOf(config.getIntProperty("duration", 10)));
        widthField = new TextField(String.valueOf(config.getIntProperty("width", 640)));
        heightField = new TextField(String.valueOf(config.getIntProperty("height", 480)));
        fpsField = new TextField(String.valueOf(config.getIntProperty("fps", 10)));
        qualityField = new TextField(String.valueOf(config.getIntProperty("quality", 10)));

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            if (validateInputs()) {
                settingsChanged = true;
                close();
            }
        });

        // Layout setup using GridPane
        GridPane layout = new GridPane();
        layout.setPadding(new Insets(20));
        layout.setVgap(10);
        layout.setHgap(10);

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

    // Validate user inputs before saving
    private boolean validateInputs() {
        try {
            int startTime = Integer.parseInt(startTimeField.getText());
            int duration = Integer.parseInt(durationField.getText());
            int width = Integer.parseInt(widthField.getText());
            int height = Integer.parseInt(heightField.getText());
            int fps = Integer.parseInt(fpsField.getText());
            int quality = Integer.parseInt(qualityField.getText());

            // Validation logic
            if (width <= 0 && width != -1) {
                showError("Width must be positive or -1 to maintain aspect ratio.");
                return false;
            }
            if (height <= 0 && height != -1) {
                showError("Height must be positive or -1 to maintain aspect ratio.");
                return false;
            }
            if (fps <= 0) {
                showError("FPS must be greater than 0.");
                return false;
            }
            if (duration <= 0) {
                showError("Duration must be greater than 0.");
                return false;
            }
            if (quality < 1 || quality > 31) {
                showError("Quality must be between 1 and 31.");
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            showError("All fields must be valid integers.");
            return false;
        }
    }

    // Show error alert
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Неверный ввод");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Getters for settings
    public int getStartTime() {
        return Integer.parseInt(startTimeField.getText());
    }

    public int getDuration() {
        return Integer.parseInt(durationField.getText());
    }

    public double getVideoWidth() {
        return Double.parseDouble(widthField.getText());
    }

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
