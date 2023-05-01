package com.daqem.jobsplus.mixin;

import com.daqem.jobsplus.event.triggers.PlayerEvents;
import com.daqem.jobsplus.player.JobsServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(FishingHook.class)
public class MixinFishingHook {

    private Player getPlayer() {
        return ((FishingHook) (Object) this).getPlayerOwner();
    }

    @ModifyVariable(method = "retrieve(Lnet/minecraft/world/item/ItemStack;)I", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;", shift = At.Shift.BEFORE))
    private List<ItemStack> modifyList(List<ItemStack> list) {
        Player player = getPlayer();
        if (player instanceof JobsServerPlayer serverPlayer) {
            for (ItemStack itemStack : list) {
                PlayerEvents.onFishedUpItem(serverPlayer, itemStack);
            }
        }
        return list;
    }
}
