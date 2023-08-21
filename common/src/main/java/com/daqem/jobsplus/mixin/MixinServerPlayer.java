package com.daqem.jobsplus.mixin;

import com.daqem.arc.api.action.holder.IActionHolder;
import com.daqem.arc.api.player.ArcPlayer;
import com.daqem.arc.api.player.ArcServerPlayer;
import com.daqem.jobsplus.Constants;
import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.config.JobsPlusCommonConfig;
import com.daqem.jobsplus.interation.arc.action.holder.holders.job.JobInstance;
import com.daqem.jobsplus.interation.arc.action.holder.holders.job.JobManager;
import com.daqem.jobsplus.networking.sync.jobs.ClientboundRemoveJobPacket;
import com.daqem.jobsplus.networking.sync.jobs.ClientboundUpdateJobPacket;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.player.job.powerup.Powerup;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer extends Player implements JobsServerPlayer {

    @Unique
    private List<Job> jobsplus$jobs = new ArrayList<>();
    @Unique
    private int jobsplus$coins = 0;
    @Unique
    private boolean jobsplus$updatedFromOldJobsPLus = false;

    public MixinServerPlayer(Level level, BlockPos blockPos, float yaw, GameProfile gameProfile, @Nullable ProfilePublicKey profilePublicKey) {
        super(level, blockPos, yaw, gameProfile, profilePublicKey);
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
        if (jobsplus$getJobs().size() > JobsPlusCommonConfig.amountOfFreeJobs.get()) {
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
    public int jobsplus$getCoins() {
        return jobsplus$coins;
    }

    @Override
    public void jobsplus$addCoins(int coins) {
        this.jobsplus$coins += coins;
    }

    @Override
    public void jobsplus$setCoins(int coins) {
        this.jobsplus$coins = coins;
    }

    @Override
    public @NotNull UUID jobsplus$getUUID() {
        return super.getUUID();
    }

    @Override
    public List<IActionHolder> jobsplus$getActionHolders() {
        List<IActionHolder> actionHolders = new ArrayList<>(jobsplus$getJobInstances());
        actionHolders.addAll(jobsplus$getJobs().stream()
                .map(Job::getPowerupManager)
                .flatMap(powerupManager -> powerupManager.getAllPowerups().stream())
                .map(Powerup::getPowerupInstance)
                .toList());
        return actionHolders;
    }

    @Override
    public ServerPlayer jobsplus$getServerPlayer() {
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
    public boolean jobsplus$getUpdatedFromOldJobsPlus() {
        return this.jobsplus$updatedFromOldJobsPLus;
    }

    @Override
    public void jobsplus$setUpdatedFromOldJobsPlus(boolean updatedFromOldJobsPlus) {
        this.jobsplus$updatedFromOldJobsPLus = updatedFromOldJobsPlus;
    }

    @Override
    public double jobsplus$nextRandomDouble() {
        return this.jobsplus$getServerPlayer().getRandom().nextDouble();
    }

    @Override
    public Level jobsplus$getLevel() {
        return super.getLevel();
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
            job.getPowerupManager().getAllPowerups().forEach(powerup -> arcPlayer.arc$addActionHolder(powerup.getPowerupInstance()));
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
            this.jobsplus$updatedFromOldJobsPLus = oldJobsServerPlayer.jobsplus$getUpdatedFromOldJobsPlus();
        }
    }

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        CompoundTag jobsTag = new CompoundTag();
        jobsTag.put(Constants.JOBS, Job.Serializer.toNBT(this.jobsplus$jobs));
        jobsTag.putInt(Constants.COINS, this.jobsplus$coins);
        jobsTag.putBoolean(Constants.JOBSPLUS_UPDATE, this.jobsplus$updatedFromOldJobsPLus);

        compoundTag.put(Constants.JOBS_DATA, jobsTag);
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        CompoundTag jobsTag = compoundTag.getCompound(Constants.JOBS_DATA);
        this.jobsplus$jobs = Job.Serializer.fromNBT(this, jobsTag).stream()
                .filter(job -> job.getJobInstance() != null)
                .collect(Collectors.toCollection(ArrayList::new));
        this.jobsplus$coins = jobsTag.getInt(Constants.COINS);

        this.jobsplus$updatedFromOldJobsPLus = jobsTag.getBoolean(Constants.JOBSPLUS_UPDATE);

        CompoundTag forgeCaps = compoundTag.getCompound("ForgeCaps").getCompound("jobsplus:jobs");
        if (!forgeCaps.isEmpty()) {
            jobsplus$readFromOldJobsPlusData(forgeCaps);
        }

        if (jobsplus$getServerPlayer() instanceof ArcServerPlayer arcServerPlayer) {
            List<IActionHolder> iActionHolders = this.jobsplus$getActionHolders();
            arcServerPlayer.arc$addActionHolders(new ArrayList<>(iActionHolders));
        }
    }

    @Unique
    private void jobsplus$readFromOldJobsPlusData(CompoundTag tag) {
        Map<String, int[]> map = new HashMap<>();
        List<Job> jobs = new ArrayList<>();

        this.jobsplus$coins = tag.getInt("coins");

        map.put("alchemist", tag.getIntArray("alchemist"));
        map.put("builder", tag.getIntArray("builder"));
        map.put("digger", tag.getIntArray("digger"));
        map.put("enchanter", tag.getIntArray("enchanter"));
        map.put("farmer", tag.getIntArray("farmer"));
        map.put("fisherman", tag.getIntArray("fisherman"));
        map.put("hunter", tag.getIntArray("hunter"));
        map.put("lumberjack", tag.getIntArray("lumberjack"));
        map.put("miner", tag.getIntArray("miner"));
        map.put("smith", tag.getIntArray("smith"));

        map.forEach((string, intArray) -> {
            int level = intArray[0];
            int experience = intArray[1];
            List<Boolean> powerUps = List.of(intArray[2] != 0, intArray[3] != 0, intArray[4] != 0);

            JobInstance jobInstance = JobManager.getInstance().getJobInstance(JobsPlus.getId(string));
            if (jobInstance != null) {
                jobs.add(new Job(this, jobInstance, level, experience, new ArrayList<>()));
                this.jobsplus$addCoins((int) (powerUps.stream().filter(b -> b).count() * 10));
            }
        });
        this.jobsplus$jobs = jobs;
        this.jobsplus$updatedFromOldJobsPLus = true;
    }
}
