package com.daqem.jobsplus.resources.job.action.actions.player;

import com.daqem.jobsplus.resources.job.action.Action;
import com.daqem.jobsplus.resources.job.action.Actions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class ShootProjectileAction extends Action {

    public ShootProjectileAction() {
        super(Actions.SHOOT_PROJECTILE);
    }

    @Override
    public String toString() {
        return "ShootProjectileAction{" +
                "type=" + getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<ShootProjectileAction> {

        @Override
        public ShootProjectileAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new ShootProjectileAction();
        }
    }
}
