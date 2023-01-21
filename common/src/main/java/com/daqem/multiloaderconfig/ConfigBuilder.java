package com.daqem.multiloaderconfig;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigBuilder {

    private final Map<String, Object> defaultValues = new LinkedHashMap<>();

    public Map<String, Object> getDefaultValues() {
        return defaultValues;
    }

    public void addDefaultValue(String key, Object object) {
        defaultValues.put(key, object);
    }
}
