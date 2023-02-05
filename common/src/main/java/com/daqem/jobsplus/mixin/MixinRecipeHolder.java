package com.daqem.jobsplus.mixin;

import com.daqem.jobsplus.event.triggers.PlayerEvents;
import com.daqem.jobsplus.player.JobsServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Collections;

@Mixin(RecipeHolder.class)
public interface MixinRecipeHolder {

    default void awardUsedRecipes(Player player) {
        Recipe<?> recipe = ((RecipeHolder) this).getRecipeUsed();
        if (player instanceof JobsServerPlayer jobsServerPlayer) {
            if (recipe != null) {
                PlayerEvents.onCraftItem(jobsServerPlayer, recipe, recipe.getResultItem(), player.level);
            }
        }
        if (recipe != null && !recipe.isSpecial()) {
            player.awardRecipes(Collections.singleton(recipe));
            ((RecipeHolder) this).setRecipeUsed(null);
        }
    }
}
