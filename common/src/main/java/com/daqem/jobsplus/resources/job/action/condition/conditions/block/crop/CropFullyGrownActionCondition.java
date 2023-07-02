package com.daqem.jobsplus.resources.job.action.condition.conditions.block.crop;

import com.daqem.jobsplus.player.action.ActionData;
import com.daqem.jobsplus.player.action.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;

public class CropFullyGrownActionCondition extends ActionCondition {

    public CropFullyGrownActionCondition() {
        super(ActionConditions.CROP_FULLY_GROWN);
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
                    int fullyGrownAge = lastValue;
                    Optional<Integer> optionalAgeValue = blockState.getOptionalValue(ageProperty);
                    if (optionalAgeValue.isPresent()) {
                        return optionalAgeValue.get() == fullyGrownAge;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "CropFullyGrownActionCondition{" +
                "type=" + getType() +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<CropFullyGrownActionCondition> {

        @Override
        public CropFullyGrownActionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new CropFullyGrownActionCondition();
        }
    }
}
