package com.daqem.multiloaderconfig;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BaseConfig {

    private final Path configPath;
    private final String modId;
    private final String fileName;
    private final ConfigType configType;
    private Map<String, Object> defaultValues;
    private File configFile;
    private final Map<String, Object> defaultConfigValues = new LinkedHashMap<>();

    public BaseConfig(Path configPath, String modId, String filename, ConfigType configType) {
        this.configPath = configPath;
        this.modId = modId;
        this.fileName = filename;
        this.configType = configType;
    }

    public void init() {
        setDefaultValues(serializeConfig());
        createRequiredDirectories();
        createConfigFile();
        try {
            fillConfigFile();
        } catch (FileNotFoundException e) {
            MultiLoaderConfig.LOGGER.error("Config file for {} doesn't exist.", getFullConfigPath());
            e.printStackTrace();
        }
        deserializeConfig(getConfigFileAsJson());
    }

    private void createRequiredDirectories() {
        try {
            Files.createDirectories(getConfigFileDirectoryPath());
        } catch (IOException e) {
            MultiLoaderConfig.LOGGER.error("Cannot create config folder(s).");
            e.printStackTrace();
        }
    }

    private void createConfigFile() {
        configFile = getFullConfigPath();
        try {
            if (configFile.createNewFile()) {
                MultiLoaderConfig.LOGGER.debug("Created {} config file.", configType);
            } else {
                MultiLoaderConfig.LOGGER.debug("{} config already exists.", configType);
                MultiLoaderConfig.LOGGER.debug("Config file length: {}", configFile.length());
            }
        } catch (IOException e) {
            MultiLoaderConfig.LOGGER.error("Cannot create config file.");
            e.printStackTrace();
        }
    }

    @NotNull
    private File getFullConfigPath() {
        return new File(Path.of(
                getConfigFileDirectoryPath().toString(),
                fileName + ".json"
        ).toUri());
    }

    private void fillConfigFile() throws FileNotFoundException {
        if (getConfigFile().exists()) {
            writeConfigToJson();
        } else {
            throw new FileNotFoundException();
        }
    }

    private Path getConfigFileDirectoryPath() {
        return Path.of(configPath.toString(), modId, configType.name().toLowerCase());
    }

    private File getConfigFile() {
        return configFile;
    }

    private JsonObject getConfigFileAsJson() {
        JsonObject jsonObject = new JsonObject();
        if (configFile.length() != 0) {
            try {
                jsonObject = GsonHelper.parse(new BufferedReader(new FileReader(configFile)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    private JsonObject serializeConfigFile() {
        JsonObject configJson = getConfigFileAsJson();

        for (Map.Entry<String, Object> defaultConfigValueEntry : defaultValues.entrySet()) {
            String key = defaultConfigValueEntry.getKey();
            if (!configJson.has(key)) {
                if (defaultConfigValueEntry.getValue() instanceof String) {
                    configJson.addProperty(key, (String) defaultConfigValueEntry.getValue());
                }
                if (defaultConfigValueEntry.getValue() instanceof Integer) {
                    configJson.addProperty(key, (Integer) defaultConfigValueEntry.getValue());
                }
                if (defaultConfigValueEntry.getValue() instanceof Character) {
                    configJson.addProperty(key, (Character) defaultConfigValueEntry.getValue());
                }
                if (defaultConfigValueEntry.getValue() instanceof Boolean) {
                    configJson.addProperty(key, (Boolean) defaultConfigValueEntry.getValue());
                }
                if (defaultConfigValueEntry.getValue() instanceof JsonElement) {
                    configJson.add(key, (JsonElement) defaultConfigValueEntry.getValue());
                }
            }
        }

        return configJson;
    }

    private void writeConfigToJson() {
        JsonObject jsonElement = serializeConfigFile();
        try {
            Writer writer = new BufferedWriter(new FileWriter(getConfigFile()));
            new GsonBuilder().setPrettyPrinting().create().toJson(jsonElement, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            MultiLoaderConfig.LOGGER.error("Failed to save {} config file.", configType);
            e.printStackTrace();
        }
    }

    private void setDefaultValues(Map<String, Object> defaultValues) {
        this.defaultValues = defaultValues;
    }

    protected void addDefaultValue(String key, Object value) {
        defaultConfigValues.put(key, value);
    }

    protected Map<String, Object> getDefaultConfigValues() {
        return defaultConfigValues;
    }

    protected abstract Map<String, Object> serializeConfig();

    protected abstract void deserializeConfig(JsonObject jsonObject);
}
