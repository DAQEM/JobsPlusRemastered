package com.daqem.jobsplus.mixin;

import com.daqem.jobsplus.JobsPlus;
import com.daqem.multiloaderconfiglib.MultiLoaderConfigLib;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;initServer()Z"), method = "runServer")
    private void beforeSetupServer(CallbackInfo info) {
        MultiLoaderConfigLib.loadServerConfigs();
        MultiLoaderConfigLib.loadCommonConfigs();

        JobsPlus.LOGGER.error(String.valueOf(JobsPlus.SERVER_CONFIG.isDebug));
    }
}
