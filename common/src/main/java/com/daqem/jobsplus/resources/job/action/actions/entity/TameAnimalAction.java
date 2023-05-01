package com.daqem.jobsplus.resources.job.action.actions.entity;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class TameAnimalAction extends Action {

    public TameAnimalAction() {
        super(Actions.TAME_ANIMAL);
    }

    @Override
    public String toString() {
        return "TameAnimalAction{" +
                "type=" + this.getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<TameAnimalAction> {

        @Override
        public TameAnimalAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new TameAnimalAction();
        }
    }
}
