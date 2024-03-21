package com.daqem.jobsplus.mixin;

import com.daqem.jobsplus.networking.sync.jobs.ClientboundUpdateJobsPacket;
import com.daqem.jobsplus.player.JobsPlayer;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Shadow @Final private List<ServerPlayer> players;

    @Inject(at = @At("TAIL"), method = "reloadResources")
    private void reloadResources(CallbackInfo ci) {
        for (ServerPlayer player : this.players) {
            if (player instanceof JobsPlayer jobsPlayer) {
                new ClientboundUpdateJobsPacket(jobsPlayer.jobsplus$getJobs()).sendTo(player);
            }
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;sendPlayerPermissionLevel(Lnet/minecraft/server/level/ServerPlayer;)V", shift = At.Shift.BEFORE), method = "placeNewPlayer")
    private void placeNewPlayer(Connection connection, ServerPlayer serverPlayer, CallbackInfo ci) {
        if (serverPlayer instanceof JobsPlayer jobsPlayer) {
            new ClientboundUpdateJobsPacket(jobsPlayer.jobsplus$getJobs()).sendTo(serverPlayer);
        }
    }
}
