package com.daqem.jobsplus.resources.job.action.condition.conditions.movement;

import com.daqem.jobsplus.player.action.ActionData;
import com.daqem.jobsplus.player.action.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.*;

import java.lang.reflect.Type;

public class DistanceActionCondition extends ActionCondition {

    private final int distanceInBlocks;
    private int lastDistanceInBlocks = 0;

    public DistanceActionCondition(int distanceInBlocks) {
        super(ActionConditions.DISTANCE);
        this.distanceInBlocks = distanceInBlocks;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        Integer distanceInCm = actionData.getSpecification(ActionSpecification.DISTANCE_IN_CM);
        if (distanceInCm != null) {
            int currentDistanceInBlocks = distanceInCm / 100;
            if (currentDistanceInBlocks != lastDistanceInBlocks) {
                if (currentDistanceInBlocks % distanceInBlocks == 0) {
                    lastDistanceInBlocks = currentDistanceInBlocks;
                    return true;
                }
            }
        }
        return false;
    }

    public static class Deserializer implements JsonDeserializer<DistanceActionCondition> {

        @Override
        public DistanceActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            int distanceInBlocks;
            if (jsonObject.has("distance_in_blocks")) {
                distanceInBlocks = jsonObject.get("distance_in_blocks").getAsInt();
            } else {
                throw new JsonParseException("Missing distance_in_blocks, expected to find a int in DistanceActionCondition");
            }
            return new DistanceActionCondition(distanceInBlocks);
        }
    }
}
