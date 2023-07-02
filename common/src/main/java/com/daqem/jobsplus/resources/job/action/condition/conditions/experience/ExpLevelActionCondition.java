package com.daqem.jobsplus.resources.job.action.condition.conditions.experience;

import com.daqem.jobsplus.player.action.ActionData;
import com.daqem.jobsplus.player.action.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.*;

import java.lang.reflect.Type;

public class ExpLevelActionCondition extends ActionCondition {

    private final int expLevel;

    public ExpLevelActionCondition(int expLevel) {
        super(ActionConditions.EXP_LEVEL);
        this.expLevel = expLevel;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        Integer expLevel = actionData.getSpecification(ActionSpecification.EXP_LEVEL);
        return expLevel != null && expLevel == this.expLevel;
    }

    public static class Deserializer implements JsonDeserializer<ExpLevelActionCondition> {

        @Override
        public ExpLevelActionCondition deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            int expLevel;
            if (jsonObject.has("exp_level")) {
                expLevel = jsonObject.get("exp_level").getAsInt();
            } else {
                throw new JsonParseException("Missing exp_level, expected to find a int in ExpLevelActionCondition");
            }
            return new ExpLevelActionCondition(expLevel);
        }
    }
}
