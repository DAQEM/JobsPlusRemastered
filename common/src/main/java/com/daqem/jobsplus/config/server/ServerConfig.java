package com.daqem.jobsplus.config.server;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.JobsPlusExpectPlatform;
import com.daqem.multiloaderconfiglib.server.BaseServerConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Map;

public class ServerConfig extends BaseServerConfig {

    public static final String DEBUG = "debug";
    public static final String TEST = "test";
    public static final String WTF = "wtf";
    public static final String ARRAY = "array";

    public boolean isDebug;
    public String Test;
    public char wtf;
    public JsonArray array;

    public ServerConfig() {
        super(JobsPlusExpectPlatform.getConfigDirectory(), JobsPlus.MOD_ID, "server-config");
    }

    @Override
    protected void deserializeConfig(JsonObject jsonObject) {
        isDebug = jsonObject.get(DEBUG).getAsBoolean();
        Test = jsonObject.get(TEST).getAsString();
        wtf = jsonObject.get(WTF).getAsCharacter();
        array = jsonObject.getAsJsonArray(ARRAY);
    }

    @Override
    protected Map<String, Object> serializeConfig() {
        addDefaultValue(DEBUG, false);
        addDefaultValue(TEST, "Dit is een mudder f*cking test");
        addDefaultValue(WTF, 'D');
        JsonArray array = new JsonArray();
        array.add("test");
        addDefaultValue(ARRAY, array);
        return getDefaultConfigValues();
    }
}
