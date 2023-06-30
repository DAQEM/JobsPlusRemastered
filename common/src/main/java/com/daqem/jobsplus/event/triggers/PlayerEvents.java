package com.daqem.jobsplus.event.triggers;

import com.daqem.jobsplus.player.ActionDataBuilder;
import com.daqem.jobsplus.player.ActionSpecification;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.resources.job.action.Actions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
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

    public static void onEffectAdded(JobsServerPlayer player, MobEffectInstance effect, Entity source) {
        new ActionDataBuilder(player, Actions.EFFECT_ADDED)
                .withSpecification(ActionSpecification.MOB_EFFECT_INSTANCE, effect)
                .withSpecification(ActionSpecification.ENTITY, source)
                .build()
                .sendToAction();
    }

    public static void onSmeltItem(JobsServerPlayer player, Recipe<?> recipe, ItemStack stack, BlockPos furnacePos, Level level) {
        new ActionDataBuilder(player, Actions.SMELT_ITEM)
                .withSpecification(ActionSpecification.ITEM_STACK, stack)
                .withSpecification(ActionSpecification.BLOCK_POSITION, furnacePos)
                .withSpecification(ActionSpecification.BLOCK_STATE, level.getBlockState(furnacePos))
                .withSpecification(ActionSpecification.WORLD, level)
                .withSpecification(ActionSpecification.RECIPE, recipe)
                .build()
                .sendToAction();
    }

    public static void onCraftItem(JobsServerPlayer player, Recipe<?> recipe, ItemStack stack, Level level) {
        new ActionDataBuilder(player, Actions.CRAFT_ITEM)
                .withSpecification(ActionSpecification.ITEM_STACK, stack)
                .withSpecification(ActionSpecification.WORLD, level)
                .withSpecification(ActionSpecification.RECIPE, recipe)
                .build()
                .sendToAction();
    }

    public static void onEnchantItem(JobsServerPlayer player, ItemStack stack, int level) {
        new ActionDataBuilder(player, Actions.ENCHANT_ITEM)
                .withSpecification(ActionSpecification.ITEM_STACK, stack)
                .withSpecification(ActionSpecification.EXP_LEVEL, level)
                .build()
                .sendToAction();
    }

    public static void onFishedUpItem(JobsServerPlayer player, ItemStack stack) {
        new ActionDataBuilder(player, Actions.FISHED_UP_ITEM)
                .withSpecification(ActionSpecification.ITEM_STACK, stack)
                .withSpecification(ActionSpecification.ITEM, stack.getItem())
                .build()
                .sendToAction();
    }

    public static void onStripLog(JobsServerPlayer player, BlockPos pos, Level level) {
        new ActionDataBuilder(player, Actions.STRIP_LOG)
                .withSpecification(ActionSpecification.BLOCK_STATE, level.getBlockState(pos))
                .withSpecification(ActionSpecification.BLOCK_POSITION, pos)
                .withSpecification(ActionSpecification.WORLD, level)
                .build()
                .sendToAction();
    }

    public static void onGrindItem(JobsServerPlayer player) {
        new ActionDataBuilder(player, Actions.GRIND_ITEM)
                .build()
                .sendToAction();
    }

    public static void onUseAnvil(JobsServerPlayer player, ItemStack stack) {
        new ActionDataBuilder(player, Actions.USE_ANVIL)
                .withSpecification(ActionSpecification.ITEM_STACK, stack)
                .withSpecification(ActionSpecification.ITEM, stack.getItem())
                .build()
                .sendToAction();
    }

    public static boolean onPlayerHurtItem(JobsServerPlayer player, ItemStack itemStack) {
        return new ActionDataBuilder(player, Actions.HURT_ITEM)
                .withSpecification(ActionSpecification.ITEM_STACK, itemStack)
                .withSpecification(ActionSpecification.ITEM, itemStack.getItem())
                .build()
                .sendToAction();
    }
}
