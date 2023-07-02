package com.daqem.jobsplus.resources.job.action.condition.conditions;

import com.daqem.jobsplus.player.action.ActionData;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OrActionCondition extends ActionCondition {

    private final List<ActionCondition> conditions;

    public OrActionCondition(List<ActionCondition> conditions) {
        super(ActionConditions.OR);
        this.conditions = conditions;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        return conditions.stream().anyMatch(condition -> condition.isMet(actionData));
    }

    public static class Deserializer implements JsonDeserializer<OrActionCondition> {

        private static Gson getGson() {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeHierarchyAdapter(ActionCondition.class, new ActionCondition.ActionConditionSerializer<>());
            return builder.create();
        }

        @Override
        public OrActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            List<ActionCondition> tempConditions = new ArrayList<>();
            if (jsonObject.has("conditions")) {
                JsonArray conditions = jsonObject.getAsJsonArray("conditions");
                if (conditions != null && !conditions.isEmpty()) {
                    for (JsonElement condition : conditions) {
                        JsonObject conditionObject = condition.getAsJsonObject();
                        ActionCondition actionCondition = getGson().fromJson(conditionObject, ActionCondition.class);
                        tempConditions.add(actionCondition);
                    }
                }
            } else {
                throw new JsonParseException("Missing conditions, expected to find a list of ActionConditions in OrActionCondition");
            }
            return new OrActionCondition(tempConditions);
        }
    }
}
