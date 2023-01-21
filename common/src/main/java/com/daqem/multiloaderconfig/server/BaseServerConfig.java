package com.daqem.multiloaderconfig.server;

import com.daqem.multiloaderconfig.BaseConfig;
import com.daqem.multiloaderconfig.ConfigType;

import java.nio.file.Path;

public abstract class BaseServerConfig extends BaseConfig {

    public BaseServerConfig(Path configPath, String modId, String fileName) {
        super(configPath, modId, fileName, ConfigType.SERVER);
    }
}
