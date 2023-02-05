package com.daqem.jobsplus.resources.job.action.condition.conditions.block.crop;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.*;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Type;

public class CropAgeActionCondition extends ActionCondition {

    private final int age;

    public CropAgeActionCondition(int age) {
        super(ActionConditions.CROP_AGE);
        this.age = age;
    }

    @Override
    public boolean isMet(ActionData actionData) {
        BlockState blockState = actionData.getSpecification(ActionSpecification.BLOCK_STATE);
        return blockState != null && blockState.getOptionalValue(CropBlock.AGE).orElse(-1) == this.age;
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
