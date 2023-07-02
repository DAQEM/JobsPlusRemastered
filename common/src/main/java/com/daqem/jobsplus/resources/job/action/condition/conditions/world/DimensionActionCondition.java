package com.daqem.jobsplus.resources.job.action.condition.conditions.world;

import com.daqem.jobsplus.player.action.ActionData;
import com.daqem.jobsplus.player.action.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.lang.reflect.Type;

public class DimensionActionCondition extends ActionCondition {

    ResourceKey<Level> dimension;

    public DimensionActionCondition(ResourceKey<Level> dimension) {
        super(ActionConditions.DIMENSION);
        this.dimension = dimension;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        Level world = actionData.getSpecification(ActionSpecification.WORLD);
        if (world == null)
            world = actionData.getPlayer().level();
        return world.dimension().location().equals(dimension.location());
    }

    public static class Deserializer implements JsonDeserializer<DimensionActionCondition> {

        @Override
        public DimensionActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String dimensionName;
            if (jsonObject.has("dimension")) {
                dimensionName = jsonObject.get("dimension").getAsString();
            } else {
                throw new JsonParseException("Missing dimension, expected to find a string in DimensionActionCondition");
            }

            ResourceKey<Level> dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(dimensionName));
            return new DimensionActionCondition(dimension);
        }
    }
}
