package com.daqem.jobsplus.resources.job.action.condition;

import com.daqem.jobsplus.player.action.ActionData;
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
            if (!conditionObject.has("type")) {
                throw new JsonParseException("ActionCondition must have a type");
            }

            String type = conditionObject.get("type").getAsString();
            ResourceLocation location = new ResourceLocation(type);
            Class<? extends ActionCondition> clazz = ActionConditions.getClass(location);
            return (T) getGson().fromJson(conditionObject, clazz);
        }
    }
}
