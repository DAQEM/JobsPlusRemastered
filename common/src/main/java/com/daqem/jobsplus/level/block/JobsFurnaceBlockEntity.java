package com.daqem.jobsplus.level.block;

import com.daqem.jobsplus.player.JobsServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

import java.util.UUID;

public interface JobsFurnaceBlockEntity {

    JobsServerPlayer getPlayer();

    void setPlayer(JobsServerPlayer player);

    UUID getPlayerUUID();

    void setPlayerUUID(UUID playerUUID);

    int getLitTime();

    void setLitTime(int i);

    boolean isLit();

    RecipeManager.CachedCheck<Container, ? extends AbstractCookingRecipe> getQuickCheck();

    AbstractFurnaceBlockEntity getAbstractFurnaceBlockEntity();

    Recipe<?> getRecipe();
}
