package App;

import java.io.File;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.nio.file.Paths;
import javafx.stage.DirectoryChooser;
import javafx.scene.layout.HBox;

public class VideoToGifApp extends Application {
    private AppConfig config;
    private String inputFilePath;
    private String outputFilePath; // Путь по умолчанию для GIF
    private VideoToGifConverter converter;

    // Label для отображения имени файла
    private Label videoFileNameLabel = new Label("Выберите видео файл");
    private ImageView videoThumbnail = new ImageView(); // Для отображения миниатюры видео

    @Override
    public void start(Stage primaryStage) {
        config = new AppConfig();

        loadSettings();

        primaryStage.setTitle("Video to GIF Converter");

        // Устанавливаем фиксированный размер окна
        primaryStage.setWidth(512);
        primaryStage.setHeight(400);
        primaryStage.setResizable(false); // Запретить изменение размера

        // Создаем кнопки
        Button settingsButton = new Button("Settings");
        Button chooseFolderButton = new Button("Choose Folder");
        Button chooseVideoButton = new Button("Choose Video");
        Button startButton = new Button("Start");

        // Привязываем события к кнопкам
        settingsButton.setOnAction(e -> openSettingsWindow(primaryStage));
        chooseFolderButton.setOnAction(e -> chooseOutputFolder(primaryStage));
        chooseVideoButton.setOnAction(e -> chooseVideoFile(primaryStage));
        startButton.setOnAction(e -> startConversion());

        // Устанавливаем размер кнопок
        settingsButton.setPrefWidth(150);
        chooseFolderButton.setPrefWidth(150);
        chooseVideoButton.setPrefWidth(150);
        startButton.setPrefWidth(150);

        // Создаем VBox для кнопок и настраиваем отступы
        VBox buttonBox = new VBox();
        buttonBox.setPadding(new Insets(20, 10, 20, 10));
        buttonBox.setSpacing(10);
        buttonBox.setAlignment(Pos.TOP_LEFT);

        // Добавляем кнопки в VBox с нужными отступами
        buttonBox.getChildren().addAll(settingsButton, chooseFolderButton, chooseVideoButton);

        // Добавляем дополнительный отступ перед кнопкой Start
        VBox.setMargin(startButton, new Insets(20, 0, 0, 0));
        buttonBox.getChildren().add(startButton);

        // Создаем VBox для миниатюры и названия видео
        VBox videoBox = new VBox();
        videoBox.setAlignment(Pos.CENTER);
        videoBox.setSpacing(10);

        // Настраиваем ImageView для миниатюры
        videoThumbnail.setFitWidth(140);
        videoThumbnail.setPreserveRatio(true);

        // Добавляем миниатюру и название видео в videoBox
        videoBox.getChildren().addAll(videoThumbnail, videoFileNameLabel);

        // Основной макет
        BorderPane layout = new BorderPane();
        layout.setLeft(buttonBox);
        layout.setCenter(videoBox);

        // Создаем сцену и добавляем стиль
        Scene scene = new Scene(layout, 512, 400);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadSettings(){
        int startTime = config.getIntProperty("startTime", 0);
        int duration = config.getIntProperty("duration", 10);
        int width = config.getIntProperty("width", 640);
        int height = config.getIntProperty("height", 480);
        int fps = config.getIntProperty("fps", 10);
        int quality = config.getIntProperty("quality", 10);
        outputFilePath = config.getProperty("outputFolder", "output");

        if (inputFilePath != null){
            converter = new VideoToGifConverter(inputFilePath);
            converter.setOutputFilePath(outputFilePath);
            converter.setStartTime(startTime);
            converter.setDuration(duration);
            converter.setWidth(width);
            converter.setHeight(height);
            converter.setFps(fps);
            converter.setQuality(quality);
        }
    }

    private void openSettingsWindow(Stage owner) {
        SettingsWindow settingsWindow = new SettingsWindow(owner, config);
        settingsWindow.showAndWait();

        // После закрытия окна настроек, обновим параметры конвертера
        if (settingsWindow.isSettingsChanged()) {
            updateConverterSettings(settingsWindow);
        }
    }

    private void updateConverterSettings(SettingsWindow settings) {
        if (converter == null) {
            converter = new VideoToGifConverter(inputFilePath);
            converter.setOutputFilePath(outputFilePath);
        }
        converter.setStartTime(settings.getStartTime());
        converter.setDuration(settings.getDuration());
        converter.setWidth((int) settings.getVideoWidth());
        converter.setHeight((int) settings.getVideoHeight());
        converter.setFps(settings.getFps());
        converter.setQuality(settings.getQuality());

        config.setIntProperty("startTime", settings.getStartTime());
        config.setIntProperty("duration", settings.getDuration());
        config.setIntProperty("width", (int) settings.getVideoWidth());
        config.setIntProperty("height", (int) settings.getVideoHeight());
        config.setIntProperty("fps", settings.getFps());
        config.setIntProperty("quality", settings.getQuality());
        config.setProperty("outputFolder", outputFilePath);

        config.save();
    }

    private void chooseOutputFolder(Stage owner) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Output Folder");
        File selectedDirectory = directoryChooser.showDialog(owner);
    
        if (selectedDirectory != null) {
            // Указываем полный путь с расширением .gif
            outputFilePath = new File(selectedDirectory, "output.gif").getAbsolutePath();
            config.setProperty("outputFolder", outputFilePath);
            config.save();
            if (converter != null) {
                converter.setOutputFilePath(outputFilePath);
            }
        }
    }
    

    private void chooseVideoFile(Stage owner) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose video");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Video Files", "*.mp4",
                "*.avi", "*.mov", "*.mkv"));
        File file = fileChooser.showOpenDialog(owner);
        if (file != null){
            inputFilePath = file.getAbsolutePath();
            videoFileNameLabel.setText(file.getName());
            setVideoThumbnail(file);

            converter = new VideoToGifConverter(inputFilePath);
            converter.setOutputFilePath(outputFilePath);
            converter.setStartTime(config.getIntProperty("startTime", 0));
            converter.setDuration(config.getIntProperty("duration", 10));
            converter.setWidth(config.getIntProperty("width", 640));
            converter.setHeight(config.getIntProperty("height", 480));
            converter.setFps(config.getIntProperty("fps", 10));
            converter.setQuality(config.getIntProperty("quality", 10));
        }
    }

    private void setVideoThumbnail(File file) {
        // Создаем директорию 'temp', если она не существует
        File tempDir = new File("temp");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        String thumbnailPath = new File(tempDir, "thumbnail.png").getAbsolutePath(); // Абсолютный путь для сохранения миниатюры
        ThumbnailExtractor.extractThumbnail(file.getAbsolutePath(), thumbnailPath, 1); // Извлекаем миниатюру на 1-й секунде

        // Загрузка миниатюры в ImageView
        Image fxImage = new Image("file:" + thumbnailPath);
        videoThumbnail.setImage(fxImage);
    }

    private void startConversion() {
        if (inputFilePath == null || outputFilePath == null) {
            showStatusWindow("Ошибка", "Необходимо выбрать видео и выходной файл.");
            return;
        }

        Task<Void> conversionTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    converter.convert();
                    updateMessage("Конвертация successful");
                } catch (Exception e) {
                    updateMessage("Ошибка конвертации: " + e.getMessage());
                }
                return null;
            }
        };

        conversionTask.setOnSucceeded(e -> {
            showStatusWindow("Успех", "Конвертация завершена успешно.\nЛог сохранен в папке 'logs'.");
        });

        conversionTask.setOnFailed(e -> {
            Throwable exception = conversionTask.getException();
            exception.printStackTrace();
            showStatusWindow("Ошибка",
                    "Произошла ошибка при конвертации. " +
                    "Подробности можно найти в логах в папке 'logs'.\n" +
                    "Ошибка: " + exception.getMessage());
        });

        new Thread(conversionTask).start();
    }

    private void showStatusWindow(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
