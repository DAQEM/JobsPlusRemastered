package com.daqem.jobsplus.resources.job.action.condition.conditions.experience;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.*;

import java.lang.reflect.Type;

public class ExpDropActionCondition extends ActionCondition {

    private final int minExpDrop;
    private final int maxExpDrop;

    public ExpDropActionCondition(int minExpDrop, int maxExpDrop) {
        super(ActionConditions.EXP_DROP);
        this.minExpDrop = minExpDrop;
        this.maxExpDrop = maxExpDrop;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        Integer expDrop = actionData.getSpecification(ActionSpecification.EXP_DROP);
        return expDrop != null && expDrop >= minExpDrop && expDrop <= maxExpDrop;
    }

    public static class Deserializer implements JsonDeserializer<ExpDropActionCondition> {


        @Override
        public ExpDropActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            int minExpDrop;
            int maxExpDrop;
            if (jsonObject.has("min_exp_drop")) {
                minExpDrop = jsonObject.get("min_exp_drop").getAsInt();
            } else {
                throw new JsonParseException("Missing min_exp_drop, expected to find a int in ExpDropActionCondition");
            }
            if (jsonObject.has("max_exp_drop")) {
                maxExpDrop = jsonObject.get("max_exp_drop").getAsInt();
            } else {
                throw new JsonParseException("Missing max_exp_drop, expected to find a int in ExpDropActionCondition");
            }
            return new ExpDropActionCondition(minExpDrop, maxExpDrop);
        }
    }
}
