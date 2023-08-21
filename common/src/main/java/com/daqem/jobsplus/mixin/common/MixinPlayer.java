package com.daqem.jobsplus.mixin.common;

import com.daqem.arc.api.action.data.ActionDataBuilder;
import com.daqem.arc.api.player.ArcPlayer;
import com.daqem.arc.api.player.ArcServerPlayer;
import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.player.JobsPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.core.config.plugins.convert.TypeConverters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Mixin(Player.class)
public abstract class MixinPlayer extends LivingEntity implements JobsPlayer {

    @Shadow protected abstract void serverAiStep();

    protected MixinPlayer(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("RETURN"), method = "getCurrentItemAttackStrengthDelay()F", cancellable = true)
    private void getCurrentItemAttackStrengthDelay(CallbackInfoReturnable<Float> info) {
        if (this instanceof ArcPlayer arcPlayer) {
            new ActionDataBuilder(arcPlayer, ActionType.)
        }
    }
}
