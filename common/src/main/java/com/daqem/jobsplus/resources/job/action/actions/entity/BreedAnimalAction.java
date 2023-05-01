package com.daqem.jobsplus.resources.job.action.actions.entity;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class BreedAnimalAction extends Action {

    public BreedAnimalAction() {
        super(Actions.BREED_ANIMAL);
    }

    @Override
    public String toString() {
        return "BreedAnimalAction{" +
                "type=" + this.getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<BreedAnimalAction> {

        @Override
        public BreedAnimalAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new BreedAnimalAction();
        }
    }
}
