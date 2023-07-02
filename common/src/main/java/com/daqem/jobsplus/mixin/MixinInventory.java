package com.daqem.jobsplus.mixin;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.client.JobsPlusClient;
import com.daqem.jobsplus.client.player.JobsClientPlayer;
import com.daqem.jobsplus.player.JobsPlayer;
import com.daqem.jobsplus.player.action.ActionDataBuilder;
import com.daqem.jobsplus.player.action.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.Actions;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public abstract class MixinInventory implements Container, Nameable {

    @Shadow @Final public Player player;

    @Shadow @Final public NonNullList<ItemStack> items;

    @Shadow public int selected;

    @Inject(at = @At("RETURN"), method = "getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F", cancellable = true)
    private void getDestroySpeed(BlockState blockState, CallbackInfoReturnable<Float> cir) {
        if (player instanceof JobsPlayer jobsPlayer) {

        }
    }
}
