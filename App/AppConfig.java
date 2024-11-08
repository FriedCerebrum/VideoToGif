package App;

import java.io.*;
import java.util.Properties;

public class AppConfig {
    private static final String CONFIG_FILE = "config.properties";
    private Properties properties;

    public AppConfig() {
        properties = new Properties();
        load();
    }

    // Load properties from the configuration file
    private void load() {
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                properties.load(fis);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Save properties to the configuration file
    public void save() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "Video to GIF Converter Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getters and setters for different property types
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        try {
            return (value != null) ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public void setIntProperty(String key, int value) {
        properties.setProperty(key, String.valueOf(value));
    }
    
    // You can add more typed getters and setters as needed
}
