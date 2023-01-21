package com.daqem.multiloaderconfig.common;

import com.daqem.multiloaderconfig.BaseConfig;
import com.daqem.multiloaderconfig.ConfigType;

import java.nio.file.Path;

public abstract class BaseCommonConfig extends BaseConfig {

    public BaseCommonConfig(Path configPath, String modId, String fileName) {
        super(configPath, modId, fileName, ConfigType.COMMON);
    }
}
