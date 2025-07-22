package me.lennyd.antispawn.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final File CONFIG_FILE = new File("config/antispawn.json");

    private static ConfigMain config;

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            // Create default config
            config = new ConfigMain();
            save();
        } else {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                config = GSON.fromJson(reader, ConfigMain.class);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load config", e);
            }
        }
    }

    public static void save() {
        try {
            // Ensure parent directory exists
            File parentDir = CONFIG_FILE.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save config", e);
        }
    }

    public static ConfigMain getConfig() {
        return config;
    }
}
