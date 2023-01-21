package com.daqem.multiloaderconfig.client;

import com.daqem.multiloaderconfig.BaseConfig;
import com.daqem.multiloaderconfig.ConfigType;

import java.nio.file.Path;

public abstract class BaseClientConfig extends BaseConfig {

    public BaseClientConfig(Path configPath, String modId, String fileName) {
        super(configPath, modId, fileName, ConfigType.CLIENT);
    }
}
