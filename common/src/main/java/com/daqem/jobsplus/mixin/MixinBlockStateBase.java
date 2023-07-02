package com.daqem.jobsplus.mixin;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.event.triggers.BlockEvents;
import com.daqem.jobsplus.player.JobsPlayer;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.player.action.ActionDataBuilder;
import com.daqem.jobsplus.player.action.ActionSpecification;
import com.daqem.jobsplus.resources.job.action.Actions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class MixinBlockStateBase {

    @Inject(at = @At("RETURN"), method = "use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;")
    public void use(Level level, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (player instanceof JobsServerPlayer jobsServerPlayer) {
            if (cir.getReturnValue() == InteractionResult.CONSUME) {
                BlockState state = level.getBlockState(hitResult.getBlockPos());
                BlockEvents.onBlockInteract(jobsServerPlayer, state, hitResult.getBlockPos(), level);
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "getDestroyProgress(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F", cancellable = true)
    public void getDestroyProgress(Player player, BlockGetter blockGetter, BlockPos blockPos, CallbackInfoReturnable<Float> cir) {
        if (player instanceof JobsPlayer jobsPlayer) {
            float old = cir.getReturnValue();
            cir.setReturnValue(
                    cir.getReturnValue()
                            * new ActionDataBuilder(jobsPlayer, Actions.GET_DESTROY_SPEED)
                            .withSpecification(ActionSpecification.ITEM_STACK, player.getMainHandItem())
                            .withSpecification(ActionSpecification.ITEM, player.getMainHandItem().getItem())
                            .withSpecification(ActionSpecification.BLOCK_STATE, blockGetter.getBlockState(blockPos))
                            .withSpecification(ActionSpecification.BLOCK_POSITION, blockPos)
                            .build()
                            .sendToAction()
                            .getDestroySpeedModifier()
            );
            float newSpeed = cir.getReturnValue();
            if (player instanceof JobsServerPlayer)
                JobsPlus.LOGGER.info("Destroy speed percentage: " + newSpeed / old * 100 + "% new: " + newSpeed);
        }
    }
}
