package com.daqem.jobsplus.resources.job.action.condition.conditions.recipe;

import com.daqem.jobsplus.player.ActionData;
import com.daqem.jobsplus.resources.job.action.condition.ActionCondition;
import com.daqem.jobsplus.resources.job.action.condition.ActionConditionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;

import java.util.List;

public abstract class IsRecipeActionCondition<T extends Recipe<?>> extends ActionCondition {

    public IsRecipeActionCondition(ActionConditionType type) {
        super(type);
    }

    protected boolean isSmeltingRecipeVersion(ActionData actionData, SmeltingRecipe smeltingRecipe, Class<? extends Recipe<?>> recipeClass) {
        MinecraftServer server = actionData.getPlayer().getServerPlayer().getServer();
        if (server != null) {
            List<T> recipes = server.getRecipeManager().getRecipes().stream()
                    .filter(recipeClass::isInstance)
                    .map(r -> (T) r)
                    .toList();
            for (T r : recipes) {
                if (r.getResultItem().getItem().equals(smeltingRecipe.getResultItem().getItem())) {
                    for (int i = 0; i < r.getIngredients().size(); i++) {
                        Ingredient ingredient = r.getIngredients().get(i);
                        for (int i1 = 0; i1 < ingredient.getItems().length; i1++) {
                            Item item = ingredient.getItems()[i1].getItem();
                            Item item1 = smeltingRecipe.getIngredients().get(i).getItems()[i1].getItem();
                            if (item != item1) {
                                return false;
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
