package com.daqem.jobsplus.resources.job.action.condition.conditions.entity;

import com.daqem.jobsplus.player.action.ActionData;
import com.daqem.jobsplus.player.action.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EntityTypesActionCondition extends ActionCondition {

    private final List<EntityType<?>> entityTypes;

    public EntityTypesActionCondition(List<EntityType<?>> entityTypes) {
        super(ActionConditions.ENTITY_TYPES);
        this.entityTypes = entityTypes;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        Entity entity = actionData.getSpecification(ActionSpecification.ENTITY);
        return entity != null && entityTypes.contains(entity.getType());
    }

    public static class Deserializer implements JsonDeserializer<EntityTypesActionCondition> {

        @Override
        public EntityTypesActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            ArrayList<EntityType<?>> entityTypes = new ArrayList<>();
            if (jsonObject.has("entity_types")) {
                JsonArray jsonArray = jsonObject.getAsJsonArray("entity_types");
                for (JsonElement jsonElement : jsonArray) {
                    EntityType<?> entityType = Registry.ENTITY_TYPE.get(new ResourceLocation(jsonElement.getAsString()));
                    if (entityType == EntityType.PIG && !jsonElement.getAsString().equals("minecraft:pig")) {
                        throw new JsonParseException("Unknown entity type " + jsonElement.getAsString() + " in EntityTypesActionCondition.");
                    } else {
                        entityTypes.add(entityType);
                    }
                }
            } else {
                throw new JsonParseException("Missing entity_types, expected to find a list of strings in EntityTypesActionCondition");
            }
            return new EntityTypesActionCondition(entityTypes);
        }
    }
}
