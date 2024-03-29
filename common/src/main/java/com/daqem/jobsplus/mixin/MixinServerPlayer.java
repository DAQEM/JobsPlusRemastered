package com.daqem.jobsplus.mixin;

import com.daqem.arc.api.action.holder.IActionHolder;
import com.daqem.arc.api.player.ArcPlayer;
import com.daqem.arc.api.player.ArcServerPlayer;
import com.daqem.jobsplus.Constants;
import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.config.JobsPlusConfig;
import com.daqem.jobsplus.integration.arc.holder.holders.job.JobInstance;
import com.daqem.jobsplus.integration.arc.holder.holders.job.JobManager;
import com.daqem.jobsplus.integration.arc.holder.holders.powerup.PowerupInstance;
import com.daqem.jobsplus.networking.sync.jobs.ClientboundRemoveJobPacket;
import com.daqem.jobsplus.networking.sync.jobs.ClientboundUpdateJobPacket;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.player.job.exp.ExpCollector;
import com.daqem.jobsplus.player.job.powerup.Powerup;
import com.daqem.jobsplus.player.job.powerup.PowerupState;
import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer extends Player implements JobsServerPlayer {

    @Unique
    private List<Job> jobsplus$jobs = new ArrayList<>();
    @Unique
    private int jobsplus$coins = 0;

    public MixinServerPlayer(Level level, BlockPos blockPos, float yaw, GameProfile gameProfile) {
        super(level, blockPos, yaw, gameProfile);
    }

    @Override
    public List<Job> jobsplus$getJobs() {
        return jobsplus$jobs;
    }

    @Override
    public List<JobInstance> jobsplus$getJobInstances() {
        return jobsplus$jobs.stream().map(Job::getJobInstance).toList();
    }

    @Override
    public List<Job> jobsplus$getInactiveJobs() {
        return JobManager.getInstance().getJobs().values().stream()
                .filter(jobInstance -> !jobsplus$getJobInstances().contains(jobInstance))
                .map(jobInstance -> new Job(this, jobInstance))
                .toList();
    }

    @Override
    public @Nullable Job jobsplus$addNewJob(@NotNull JobInstance jobInstance) {
        if (jobInstance.getLocation() == null) return null;
        Job job = jobsplus$getJob(jobInstance);
        if (job == null) {
            job = new Job(this, jobInstance, 1, 0);
            jobsplus$jobs.add(job);
            jobsplus$updateJob(job);
            return job;
        }
        return null;
    }

    @Override
    public void jobsplus$removeJob(JobInstance jobInstance) {
        Job job = jobsplus$getJob(jobInstance);
        if (job != null) {
            jobsplus$jobs.remove(job);
            jobsplus$removeActionHolders(job);
            jobsplus$removeJobOnClient(job);
        }
    }

    @Override
    public void jobsplus$removeActionHolders(Job job) {
        if (jobsplus$getServerPlayer() instanceof ArcPlayer arcPlayer) {
            arcPlayer.arc$removeActionHolder(job.getJobInstance());
            job.getPowerupManager().getAllPowerups().forEach(powerup -> arcPlayer.arc$removeActionHolder(powerup.getPowerupInstance()));
        }
    }

    @Override
    public void jobsplus$refundJob(@NotNull JobInstance jobInstance) {
        int refund = jobInstance.getStopRefund();
        if (jobsplus$getJobs().size() > JobsPlusConfig.amountOfFreeJobs.get()) {
            if (refund > 0) {
                jobsplus$addCoins(refund);
            }
        }
    }

    @Override
    public void jobsplus$removeAndRefundJob(@NotNull JobInstance jobInstance) {
        jobsplus$removeJob(jobInstance);
        jobsplus$refundJob(jobInstance);
    }

    @Override
    public @Nullable Job jobsplus$getJob(@Nullable JobInstance jobLocation) {
        if (jobLocation == null) return null;
        return this.jobsplus$jobs.stream()
                .filter(job -> job.getJobInstance().getLocation().equals(jobLocation.getLocation()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public @Nullable Powerup jobsplus$getPowerup(PowerupInstance powerupInstance) {
        return jobsplus$getJobs().stream()
                .map(Job::getPowerupManager)
                .flatMap(powerupManager -> powerupManager.getAllPowerups().stream())
                .filter(powerup -> powerup.getPowerupInstance().getLocation().equals(powerupInstance.getLocation()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public int jobsplus$getCoins() {
        return jobsplus$coins;
    }

    @Override
    public void jobsplus$addCoins(int coins) {
        this.jobsplus$coins = Mth.clamp(this.jobsplus$coins + coins, 0, Integer.MAX_VALUE);
    }

    @Override
    public void jobsplus$setCoins(int coins) {
        this.jobsplus$coins = coins;
    }

    @Override
    public List<IActionHolder> jobsplus$getActionHolders() {
        List<IActionHolder> actionHolders = new ArrayList<>(jobsplus$getJobInstances());
        actionHolders.addAll(jobsplus$getJobs().stream()
                .map(Job::getPowerupManager)
                .flatMap(powerupManager -> powerupManager.getAllPowerups().stream()
                        .filter(powerup -> powerup.getPowerupState() == PowerupState.ACTIVE))
                .map(Powerup::getPowerupInstance)
                .toList());
        return actionHolders;
    }

    @Override
    public ServerPlayer jobsplus$getServerPlayer() {
        //noinspection DataFlowIssue
        return (ServerPlayer) (Object) this;
    }

    @Override
    public String jobsplus$getName() {
        return super.getName().getString();
    }

    public ListTag jobsplus$inactiveJobsToNBT() {
        ListTag jobsListTag = new ListTag();
        jobsplus$getInactiveJobs().forEach(job -> jobsListTag.add(job.toNBT()));
        return jobsListTag;
    }

    @Override
    public void jobsplus$updateJob(Job job) {
        this.jobsplus$updateActionHolders(job);
        this.jobsplus$updateJobOnClient(job);
    }

    @Override
    public void jobsplus$updateActionHolders(Job job) {
        if (jobsplus$getServerPlayer() instanceof ArcPlayer arcPlayer) {
            arcPlayer.arc$removeActionHolder(job.getJobInstance());
            job.getPowerupManager().getAllPowerups().forEach(powerup -> arcPlayer.arc$removeActionHolder(powerup.getPowerupInstance()));
            arcPlayer.arc$addActionHolder(job.getJobInstance());
            job.getPowerupManager().getAllPowerups().stream()
                    .filter(powerup -> powerup.getPowerupState() == PowerupState.ACTIVE)
                    .forEach(powerup -> arcPlayer.arc$addActionHolder(powerup.getPowerupInstance()));
        }
    }

    @Override
    public void jobsplus$updateJobOnClient(Job job) {
        new ClientboundUpdateJobPacket(job).sendTo(jobsplus$getServerPlayer());
    }

    @Override
    public void jobsplus$removeJobOnClient(Job job) {
        new ClientboundRemoveJobPacket(job.getJobInstance().getLocation()).sendTo(jobsplus$getServerPlayer());
    }

    @Override
    public Player jobsplus$getPlayer() {
        return jobsplus$getServerPlayer();
    }

    @Inject(at = @At("TAIL"), method = "restoreFrom(Lnet/minecraft/server/level/ServerPlayer;Z)V")
    public void restoreFrom(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        if (oldPlayer instanceof JobsServerPlayer oldJobsServerPlayer) {
            this.jobsplus$jobs = oldJobsServerPlayer.jobsplus$getJobs();
            this.jobsplus$coins = oldJobsServerPlayer.jobsplus$getCoins();

            this.jobsplus$jobs.forEach(job -> job.setPlayer(this));
        }
    }

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        CompoundTag jobsTag = new CompoundTag();
        jobsTag.put(Constants.JOBS, Job.Serializer.toNBT(this.jobsplus$jobs));
        jobsTag.putInt(Constants.COINS, this.jobsplus$coins);

        compoundTag.put(Constants.JOBS_DATA, jobsTag);
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        CompoundTag jobsTag = compoundTag.getCompound(Constants.JOBS_DATA);
        this.jobsplus$jobs = Job.Serializer.fromNBT(this, jobsTag).stream()
                .filter(job -> job.getJobInstance() != null)
                .collect(Collectors.toCollection(ArrayList::new));
        this.jobsplus$coins = jobsTag.getInt(Constants.COINS);

        if (jobsplus$getServerPlayer() instanceof ArcServerPlayer arcServerPlayer) {
            List<IActionHolder> iActionHolders = this.jobsplus$getActionHolders();
            arcServerPlayer.arc$addActionHolders(new ArrayList<>(iActionHolders));
        }
    }

    @Inject(at = @At("TAIL"), method = "tick()V")
    public void tickTail(CallbackInfo ci) {
        jobsplus$jobs.forEach((job) -> {
            JobInstance jobInstance = job.getJobInstance();
            ExpCollector expCollector = job.getExpCollector();
            int exp = expCollector.getExp();
            if (exp > 0) {
                MutableComponent component = JobsPlus.translatable("job.exp.gain", exp, jobInstance.getName().getString()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(jobInstance.getColorDecimal()))).withStyle(ChatFormatting.BOLD);
                jobsplus$getServerPlayer().sendSystemMessage(component, true);
            }
            expCollector.clear();
        });
    }
}
