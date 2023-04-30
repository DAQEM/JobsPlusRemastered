package com.daqem.jobsplus.resources.job.action.condition.conditions.block.crop;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;

public class CropAgeActionCondition extends ActionCondition {

    private final int age;

    public CropAgeActionCondition(int age) {
        super(ActionConditions.CROP_AGE);
        this.age = age;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        BlockState blockState = actionData.getSpecification(ActionSpecification.BLOCK_STATE);
        if (blockState != null) {
            Collection<Property<?>> properties = blockState.getProperties();
            Optional<Property<?>> optionalAgeProperty = properties.stream()
                    .filter(property -> property.getName().equals("age"))
                    .findFirst();
            if (optionalAgeProperty.isPresent()) {
                IntegerProperty ageProperty = (IntegerProperty) optionalAgeProperty.get();
                Collection<Integer> possibleValues = ageProperty.getPossibleValues();
                Integer lastValue = possibleValues.stream().reduce((a, b) -> b).orElse(null);
                if (lastValue != null) {
                    JobsPlus.LOGGER.info("ageProperty: " + lastValue.intValue());
                }
                Optional<Integer> optionalAgeValue = blockState.getOptionalValue(ageProperty);
                if (optionalAgeValue.isPresent()) {
                    JobsPlus.LOGGER.info("age: " + optionalAgeValue.get());
                    return optionalAgeValue.get() == this.age;
                }
            }
        }
        return false;
    }

    public int getAge() {
        return this.age;
    }

    @Override
    public String toString() {
        return "CropAgeActionCondition{" +
                "type=" + getType() +
                ", age=" + getAge() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<CropAgeActionCondition> {

        @Override
        public CropAgeActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            return new CropAgeActionCondition(
                    jsonObject.get("age").getAsInt());
        }
    }
}
