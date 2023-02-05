package com.daqem.jobsplus.resources.job.action.condition.conditions.swim;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.*;

import java.lang.reflect.Type;

public class SwimmingDistanceActionCondition extends ActionCondition {

    private final int distanceInBlocks;
    private int lastDistanceInBlocks = 0;

    public SwimmingDistanceActionCondition(int distanceInBlocks) {
        super(ActionConditions.SWIMMING_DISTANCE);
        this.distanceInBlocks = distanceInBlocks;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        Integer distanceInCm = actionData.getSpecification(ActionSpecification.SWIMMING_DISTANCE_IN_CM);
        if (distanceInCm != null) {
            int currentDistanceInBlocks = distanceInCm / 100;
            if (currentDistanceInBlocks != lastDistanceInBlocks) {
                if (currentDistanceInBlocks % distanceInBlocks == 0) {
                    lastDistanceInBlocks = currentDistanceInBlocks;
                    JobsPlus.LOGGER.info("fired for job " + actionData.getSourceJob().getJobInstance().getName() + " with distance " + currentDistanceInBlocks);
                    return true;
                }
            }
        }
        return false;
    }

    public static class Serializer implements JsonDeserializer<SwimmingDistanceActionCondition> {

        @Override
        public SwimmingDistanceActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            int distanceInBlocks;
            if (jsonObject.has("distance_in_blocks")) {
                distanceInBlocks = jsonObject.get("distance_in_blocks").getAsInt();
            } else {
                throw new JsonParseException("Missing distance_in_blocks, expected to find a int in SwimmingDistanceActionCondition");
            }
            return new SwimmingDistanceActionCondition(distanceInBlocks);
        }
    }
}
