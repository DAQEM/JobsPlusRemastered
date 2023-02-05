package com.daqem.jobsplus.resources.job.action.condition;

import com.daqem.jobsplus.player.ActionData;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Type;

public abstract class ActionCondition {

    private final ActionConditionType type;

    public ActionCondition(ActionConditionType type) {
        this.type = type;
    }

    public ActionConditionType getType() {
        return type;
    }

    abstract public boolean isMet(ActionData actionData);

    public static class ActionConditionSerializer<T extends ActionCondition> implements JsonDeserializer<T> {

        private static Gson getGson() {
            GsonBuilder builder = new GsonBuilder();

            for (ActionConditionType type : ActionConditions.ACTION_CONDITION_TYPES) {
                builder.registerTypeAdapter(type.clazz(), type.deserializer());
            }

            return builder.create();
        }

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject conditionObject = json.getAsJsonObject();
            Class<? extends ActionCondition> clazz = ActionConditions.getClass(new ResourceLocation(conditionObject.get("type").getAsString()));
            conditionObject.remove("type");
            return (T) getGson().fromJson(conditionObject, clazz);
        }
    }
}
