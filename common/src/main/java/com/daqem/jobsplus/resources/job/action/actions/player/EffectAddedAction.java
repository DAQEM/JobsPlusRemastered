package com.daqem.jobsplus.resources.job.action.actions.player;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class EffectAddedAction extends Action {

    public EffectAddedAction() {
        super(Actions.EFFECT_ADDED);
    }

    @Override
    public String toString() {
        return "EffectAddedAction{" +
                "actionType=" + getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<EffectAddedAction> {

        @Override
        public EffectAddedAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new EffectAddedAction();
        }
    }

}
