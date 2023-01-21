package com.daqem.jobsplus.resources.job.action.condition;

import com.daqem.jobsplus.resources.job.action.condition.type.CropAgeActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.type.JobLevelActionCondition;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Type;

public abstract class ActionCondition {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(CropAgeActionCondition.class, new CropAgeActionCondition.CropAgeActionConditionSerializer())
            .registerTypeAdapter(JobLevelActionCondition.class, new JobLevelActionCondition.JobLevelActionConditionSerializer())
            .create();

    private final ActionConditionType type;

    public ActionCondition(ActionConditionType type) {
        this.type = type;
    }

    public ActionConditionType getType() {
        return type;
    }

    public static class ActionConditionSerializer<T extends ActionCondition> implements JsonDeserializer<T> {

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject conditionObject = json.getAsJsonObject();
            Class<? extends ActionCondition> clazz = ActionConditions.getClass(new ResourceLocation(conditionObject.get("type").getAsString()));
            conditionObject.remove("type");
            return (T) GSON.fromJson(conditionObject, clazz);
        }
    }
}
