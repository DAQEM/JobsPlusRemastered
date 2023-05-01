package com.daqem.jobsplus.resources.job.action.condition.conditions.recipe;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;

import java.lang.reflect.Type;

public class IsBlastingRecipeActionCondition extends IsRecipeActionCondition<BlastingRecipe> {

    public IsBlastingRecipeActionCondition() {
        super(ActionConditions.IS_BLASTING_RECIPE);
    }

    @Override
    public boolean isMet(ActionData actionData) {
        Recipe<?> recipe = actionData.getSpecification(ActionSpecification.RECIPE);
        if (recipe != null) {
            if (recipe instanceof BlastingRecipe) {
                return true;
            }
            if (recipe instanceof SmeltingRecipe smeltingRecipe) {
                return isSmeltingRecipeVersion(actionData, smeltingRecipe, BlastingRecipe.class);
            }
        }
        return false;
    }

    public static class Deserializer implements JsonDeserializer<IsBlastingRecipeActionCondition> {

        @Override
        public IsBlastingRecipeActionCondition deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new IsBlastingRecipeActionCondition();
        }
    }
}
