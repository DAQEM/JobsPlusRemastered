package com.daqem.jobsplus.resources.job.action.reward.rewards;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.resources.job.action.reward.ActionReward;
import com.daqem.jobsplus.resources.job.action.reward.ActionRewards;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class CancelActionActionReward extends ActionReward {

    public CancelActionActionReward() {
        super(ActionRewards.CANCEL_ACTION);
    }

    @Override
    public boolean apply(ActionData actionData) {
        return true;
    }

    public static class Deserializer implements JsonDeserializer<CancelActionActionReward> {

        @Override
        public CancelActionActionReward deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new CancelActionActionReward();
        }
    }
}
