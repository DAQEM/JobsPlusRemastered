package com.daqem.jobsplus.resources.job.action.condition.type;

import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.*;

import java.lang.reflect.Type;

public class CropAgeActionCondition extends ActionCondition {

    private final boolean maxAge;

    public CropAgeActionCondition(boolean maxAge) {
        super(ActionConditions.CROP_AGE);
        this.maxAge = maxAge;
    }

    public boolean getMaxAge() {
        return this.maxAge;
    }

    @Override
    public String toString() {
        return "CropAgeActionCondition{" +
                "type=" + getType() +
                ", max_age=" + getMaxAge() +
                '}';
    }

    public static class CropAgeActionConditionSerializer implements JsonDeserializer<CropAgeActionCondition> {

        @Override
        public CropAgeActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            return new CropAgeActionCondition(
                    jsonObject.get("max_age").getAsBoolean());
        }
    }
}
