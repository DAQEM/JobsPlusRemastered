package com.daqem.jobsplus.resources.job.action.condition.conditions.entity;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.lang.reflect.Type;

public class EntityTypeActionCondition extends ActionCondition {

    private final EntityType<?> entityType;

    public EntityTypeActionCondition(EntityType<?> entityType) {
        super(ActionConditions.ENTITY_TYPE);
        this.entityType = entityType;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        Entity entity = actionData.getSpecification(ActionSpecification.ENTITY);
        return entity != null && entity.getType() == entityType;
    }


    public static class Deserializer implements JsonDeserializer<EntityTypeActionCondition> {

        @Override
        public EntityTypeActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String entityTypeName;
            if (jsonObject.has("entity_type")) {
                entityTypeName = jsonObject.get("entity_type").getAsString();
            } else {
                throw new JsonParseException("Missing entity_type, expected to find a string in EntityTypeActionCondition");
            }
            EntityType<?> entityType = Registry.ENTITY_TYPE.get(new ResourceLocation(entityTypeName));
            if (entityType == EntityType.PIG && !entityTypeName.equals("minecraft:pig")) {
                throw new JsonParseException("Invalid entity_type, expected to find a valid entity type in EntityTypeActionCondition");
            }
            return new EntityTypeActionCondition(entityType);
        }
    }
}
