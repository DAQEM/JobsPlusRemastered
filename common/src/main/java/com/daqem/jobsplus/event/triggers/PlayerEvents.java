package com.daqem.jobsplus.event.triggers;

import com.daqem.jobsplus.player.ActionDataBuilder;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.resources.job.action.Actions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PlayerEvents {

    public static void onPlayerEat(JobsServerPlayer player, ItemStack stack) {
        new ActionDataBuilder(player, Actions.EAT)
                .withSpecification(ActionSpecification.ITEM_STACK, stack)
                .build()
                .sendToAction();
    }

    public static void onPlayerDrink(JobsServerPlayer player, ItemStack stack) {
        new ActionDataBuilder(player, Actions.DRINK)
                .withSpecification(ActionSpecification.ITEM_STACK, stack)
                .build()
                .sendToAction();
    }

    public static void onShootProjectile(JobsServerPlayer player, AbstractArrow shotArrowEntity) {
        new ActionDataBuilder(player, Actions.SHOOT_PROJECTILE)
                .withSpecification(ActionSpecification.ITEM_STACK, shotArrowEntity.getPickupItem())
                .withSpecification(ActionSpecification.ENTITY, shotArrowEntity)
                .build()
                .sendToAction();
    }

    public static void onBrewPotion(JobsServerPlayer player, ItemStack stack, BlockPos pos, Level level) {
        new ActionDataBuilder(player, Actions.BREW_POTION)
                .withSpecification(ActionSpecification.ITEM_STACK, stack)
                .withSpecification(ActionSpecification.BLOCK_POSITION, pos)
                .withSpecification(ActionSpecification.BLOCK_STATE, level.getBlockState(pos))
                .withSpecification(ActionSpecification.WORLD, level)
                .build()
                .sendToAction();
    }
}
