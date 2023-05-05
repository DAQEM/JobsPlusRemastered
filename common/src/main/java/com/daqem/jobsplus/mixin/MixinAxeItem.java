package com.daqem.jobsplus.mixin;

import com.daqem.jobsplus.event.triggers.PlayerEvents;
import com.daqem.jobsplus.player.JobsServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AxeItem.class)
public abstract class MixinAxeItem extends DiggerItem {

    public MixinAxeItem(float f, float g, Tier tier, TagKey<Block> tagKey, Properties properties) {
        super(f, g, tier, tagKey, properties);
    }

    @Inject(at = @At("HEAD"), method = "useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;")
    public void useOn(UseOnContext useOnContext, CallbackInfoReturnable<InteractionResult> cir) {
        if (useOnContext.getPlayer() instanceof JobsServerPlayer serverPlayer) {
            PlayerEvents.onStripLog(serverPlayer, useOnContext.getClickedPos(), useOnContext.getLevel());
        }
    }
}
