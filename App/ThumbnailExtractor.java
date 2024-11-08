package App;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ThumbnailExtractor {
    public static void extractThumbnail(String videoFilePath, String outputImagePath, int timeInSeconds) {
        try {
            // Создаем список аргументов для FFmpeg
            List<String> command = Arrays.asList(
                Config.FFMPEG_PATH,  // Используем путь из Config
                "-y",
                "-ss", String.valueOf(timeInSeconds),
                "-i", videoFilePath,
                "-frames:v", "1",  // Указываем, что выводим только один кадр
                "-vf", "scale=640:-1", // Изменение масштаба изображения, если нужно (например, чтобы уменьшить размер)
                outputImagePath
            );

            System.out.println("Executing command: " + String.join(" ", command)); // Вывод команды

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            processBuilder.redirectOutput(new File("ffmpeg_output.txt")); // Перенаправление вывода в файл для отладки

            // Запускаем процесс и ожидаем завершения
            Process process = processBuilder.start();
            int exitCode = process.waitFor(); // Ожидание завершения процесса

            // Проверяем код возврата процесса
            if (exitCode != 0) {
                throw new RuntimeException("Ошибка при извлечении миниатюры из видео. Код возврата: " + exitCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
