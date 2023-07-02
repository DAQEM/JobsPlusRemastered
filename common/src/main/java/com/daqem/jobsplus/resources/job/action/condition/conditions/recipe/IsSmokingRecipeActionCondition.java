package com.daqem.jobsplus.resources.job.action.condition.conditions.recipe;

import com.daqem.jobsplus.player.action.ActionData;
import com.daqem.jobsplus.player.action.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;

import java.lang.reflect.Type;

public class IsSmokingRecipeActionCondition extends IsRecipeActionCondition<SmokingRecipe> {

    public IsSmokingRecipeActionCondition() {
        super(ActionConditions.IS_SMOKING_RECIPE);
    }

    @Override
    public boolean isMet(ActionData actionData) {
        Recipe<?> recipe = actionData.getSpecification(ActionSpecification.RECIPE);
        if (recipe != null) {
            if (recipe instanceof SmokingRecipe) {
                return true;
            }
            if (recipe instanceof SmeltingRecipe smeltingRecipe) {
                return isSmeltingRecipeVersion(actionData, smeltingRecipe, SmokingRecipe.class);
            }
        }
        return false;
    }

    public static class Deserializer implements JsonDeserializer<IsSmokingRecipeActionCondition> {

        @Override
        public IsSmokingRecipeActionCondition deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new IsSmokingRecipeActionCondition();
        }
    }
}
