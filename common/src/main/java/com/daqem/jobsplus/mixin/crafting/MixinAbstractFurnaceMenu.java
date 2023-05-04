package com.daqem.jobsplus.mixin.crafting;

import com.daqem.jobsplus.level.block.JobsFurnaceBlockEntity;
import com.daqem.jobsplus.networking.s2c.PacketCantCraftS2C;
import com.daqem.jobsplus.player.JobsServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceMenu.class)
public class MixinAbstractFurnaceMenu {

    @Shadow
    @Final
    private Container container;

    @Inject(at = @At("HEAD"), method = "stillValid(Lnet/minecraft/world/entity/player/Player;)Z")
    private void stillValid(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (this.container instanceof JobsFurnaceBlockEntity block) {
            if (block.getPlayer() == null) {
                if (player instanceof JobsServerPlayer serverPlayer) {
                    block.setPlayer(serverPlayer);
                }
            }
            if (block.getPlayer() != null) {
                if (block.getRecipe() != null) {
                    ItemStack itemStack = block.getRecipe().getResultItem();
                    if (!block.getPlayer().canSmeltItem(itemStack)) {
                        new PacketCantCraftS2C(itemStack.getItem().arch$registryName(), new ResourceLocation("jobsplus:miner"), 10).sendTo(block.getPlayer().getServerPlayer());
                    }
                } else {
                    new PacketCantCraftS2C(Items.AIR.arch$registryName(), new ResourceLocation("jobsplus:none"), 0).sendTo(block.getPlayer().getServerPlayer());
                }
            }
        }
    }
}
