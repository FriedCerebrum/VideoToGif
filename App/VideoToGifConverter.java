package App;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;


public class VideoToGifConverter {
    private static final String LOG_DIR = "logs";
    private String logFilePath;
    private String inputFilePath;
    private String outputFilePath;
    private int width = 480;      // Default width
    private int height = -1;      // Default height (maintain aspect ratio)
    private int fps = 10;         // Default fps
    private int duration = 5;     // Default duration in seconds
    private int startTime = 0;    // Default start time
    private int quality = 10;     // Default quality

    public VideoToGifConverter(String inputFilePath) {
        this.inputFilePath = inputFilePath;
        
    }

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    // Add validation in the setters
    public void setWidth(int width) {
        if (width < -1) throw new IllegalArgumentException("Width must be >= -1");
        this.width = width;
    }

    public void setHeight(int height) {
        if (height < -1) throw new IllegalArgumentException("Height must be >= -1");
        this.height = height;
    }

    public void setFps(int fps) {
        if (fps <= 0) throw new IllegalArgumentException("FPS must be > 0");
        this.fps = fps;
    }

    public void setDuration(int duration) {
        if (duration <= 0) throw new IllegalArgumentException("Duration must be > 0");
        this.duration = duration;
    }

    public void setQuality(int quality) {
        if (quality < 1 || quality > 31) throw new IllegalArgumentException("Quality must be between 1 and 31");
        this.quality = quality;
    }

    

    

    

    

    

    private void setupLogging() {
        // Create logs directory if it doesn't exist
        File logDir = new File(LOG_DIR);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }

        // Create unique log file name with timestamp
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        logFilePath = LOG_DIR + File.separator + "ffmpeg_" + timestamp + ".log";
    }

    public void convert() throws IOException, InterruptedException {
        setupLogging();
        
        // Log the conversion start and parameters
        try (FileWriter fw = new FileWriter(logFilePath, true)) {
            fw.write("=== Conversion Started at " + new Date() + " ===\n");
            fw.write("Input file: " + inputFilePath + "\n");
            fw.write("Output file: " + outputFilePath + "\n");
            fw.write("Parameters: width=" + width + ", height=" + height + 
                    ", fps=" + fps + ", duration=" + duration + 
                    ", startTime=" + startTime + ", quality=" + quality + "\n\n");
        }

        String paletteFilePath = "palette.png";
        
        // First command: Generate palette
        List<String> paletteCommand = new ArrayList<>();
        paletteCommand.add(Config.FFMPEG_PATH);
        paletteCommand.add("-y");
        paletteCommand.add("-i");
        paletteCommand.add(inputFilePath);
        paletteCommand.add("-ss");
        paletteCommand.add(String.valueOf(startTime));
        paletteCommand.add("-t");
        paletteCommand.add(String.valueOf(duration));
        paletteCommand.add("-vf");
        
        String filterString = String.format("fps=%d,scale=%d:%d:flags=lanczos",
            fps,
            width > 0 ? width : -1,
            height > 0 ? height : -1);
        paletteCommand.add(filterString + ",palettegen");
        paletteCommand.add(paletteFilePath);

        // Log palette generation command
        logCommand("Palette Generation Command", paletteCommand);

        // Execute palette generation with logging
        ProcessBuilder paletteProcessBuilder = new ProcessBuilder(paletteCommand);
        paletteProcessBuilder.redirectErrorStream(true);
        Process paletteProcess = paletteProcessBuilder.start();
        
        // Log palette generation output
        logProcessOutput("Palette Generation Output", paletteProcess);

        int paletteExitCode = paletteProcess.waitFor();
        logExitCode("Palette Generation", paletteExitCode);

        if (paletteExitCode != 0) {
            throw new RuntimeException("Palette generation failed with exit code " + paletteExitCode);
        }

        // Second command: Convert to GIF
        List<String> command = new ArrayList<>();
        command.add(Config.FFMPEG_PATH);
        command.add("-y");
        command.add("-i");
        command.add(inputFilePath);
        command.add("-i");
        command.add(paletteFilePath);
        command.add("-ss");
        command.add(String.valueOf(startTime));
        command.add("-t");
        command.add(String.valueOf(duration));
        command.add("-lavfi");
        
        String gifFilterString = String.format(
            "fps=%d,scale=%d:%d:flags=lanczos[x];[x][1:v]paletteuse",
            fps,
            width > 0 ? width : -1,
            height > 0 ? height : -1
        );
        command.add(gifFilterString);
        command.add(outputFilePath);

        // Log GIF conversion command
        logCommand("GIF Conversion Command", command);

        // Execute conversion with logging
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        // Log conversion output
        logProcessOutput("GIF Conversion Output", process);

        int conversionExitCode = process.waitFor();
        logExitCode("GIF Conversion", conversionExitCode);

        if (conversionExitCode != 0) {
            throw new RuntimeException("GIF conversion failed with exit code " + conversionExitCode);
        }

        // Clean up and log completion
        new File(paletteFilePath).delete();
        try (FileWriter fw = new FileWriter(logFilePath, true)) {
            fw.write("\n=== Conversion Completed at " + new Date() + " ===\n");
        }
    }

    private void logCommand(String title, List<String> command) throws IOException {
        try (FileWriter fw = new FileWriter(logFilePath, true)) {
            fw.write("\n=== " + title + " ===\n");
            fw.write(String.join(" ", command) + "\n\n");
        }
    }

    private void logProcessOutput(String title, Process process) throws IOException {
        try (FileWriter fw = new FileWriter(logFilePath, true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            
            fw.write("=== " + title + " ===\n");
            String line;
            while ((line = reader.readLine()) != null) {
                fw.write(line + "\n");
                System.out.println(line); // Also print to console
            }
            fw.write("\n");
        }
    }

    private void logExitCode(String operation, int exitCode) throws IOException {
        try (FileWriter fw = new FileWriter(logFilePath, true)) {
            fw.write(operation + " Exit Code: " + exitCode + "\n\n");
        }
    }


    public static void main(String[] args) {
        try {
            VideoToGifConverter converter = new VideoToGifConverter("input.mp4");
            converter.setOutputFilePath("output.gif");
            converter.setStartTime(0);
            converter.setDuration(10);
            converter.setWidth(640);
            converter.setHeight(480);
            converter.setFps(10);
            converter.setQuality(10); // Качество GIF
            converter.convert();
        } catch (IOException | InterruptedException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
