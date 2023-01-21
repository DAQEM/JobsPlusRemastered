package com.daqem.jobsplus.mixin;

import com.daqem.jobsplus.Constants;
import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.player.job.JobSerializer;
import com.daqem.jobsplus.player.job.display.JobDisplay;
import com.daqem.jobsplus.player.job.powerup.PowerupState;
import com.daqem.jobsplus.resources.job.JobInstance;
import com.daqem.jobsplus.resources.job.powerup.PowerupInstance;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayerEntity extends Player implements JobsServerPlayer {

    private List<Job> jobs = new ArrayList<>();
    private int coins = 0;
    private @Nullable JobDisplay display = null;

    public MixinServerPlayerEntity(Level level, BlockPos blockPos, float yaw, GameProfile gameProfile, @Nullable ProfilePublicKey profilePublicKey) {
        super(level, blockPos, yaw, gameProfile, profilePublicKey);
    }

    @Override
    public List<Job> getJobs() {
        return jobs;
    }

    @Override
    public List<Job> getInactiveJobs() {
        return JobsPlus.getJobManager().getJobs().values().stream()
                .filter(jobInstance ->
                        !jobs.stream()
                                .map(Job::getJobInstance)
                                .toList()
                                .contains(jobInstance))
                .map(Job::new)
                .toList();
    }

    @Override
    public void addNewJob(ResourceLocation jobLocation) {
        this.jobs.stream()
                .filter(job -> job.getJobInstance().getLocation().equals(jobLocation))
                .findFirst()
                .ifPresent(job -> {
                    throw new IllegalArgumentException("Player already has this job");
                });
        jobs.add(new Job(jobLocation, 0, 0, Map.of()));
    }

    @Override
    public void addNewJob(JobInstance job) {
        addNewJob(job.getLocation());
    }

    @Override
    public void removeJob(Job job) {
        this.jobs.remove(job);
    }

    @Override
    public Job getJob(JobInstance jobLocation) {
        return this.jobs.stream()
                .filter(job -> job.getJobInstance().getLocation().equals(jobLocation.getLocation()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean hasJob(JobInstance jobLocation) {
        return this.jobs.stream()
                .anyMatch(job -> job.getJobInstance().getLocation().equals(jobLocation.getLocation()));
    }

    @Override
    public int getCoins() {
        return coins;
    }

    @Override
    public void addCoins(int coins) {
        this.coins += coins;
    }

    @Override
    public void setCoins(int coins) {
        this.coins = coins;
    }

    @Override
    public Optional<JobDisplay> getDisplay() {
        return this.display == null ? Optional.empty() : Optional.of(display);
    }

    @Override
    public void setDisplay(@Nullable JobDisplay display) {
        this.display = display;
    }

    @Override
    public void addPowerup(PowerupInstance powerupInstance) {
        String jobLocation = powerupInstance.getLocation().toString().split("\\.")[0];
        JobInstance jobInstance = JobInstance.of(new ResourceLocation(jobLocation));
        if (hasJob(jobInstance)) {
            Job job = getJob(jobInstance);
            if (!job.hasPowerup(powerupInstance)) {
                job.addPowerup(powerupInstance);
            } else {
                JobsPlus.LOGGER.debug("Player {} already has powerup {} for job {}", name(), powerupInstance.getLocation(), jobLocation);
            }
        } else {
            JobsPlus.LOGGER.debug("Player {} does not have job {} to add powerup {}", name(), jobLocation, powerupInstance.getLocation());
        }
    }

    @Override
    public void setPowerup(PowerupInstance powerupInstance, PowerupState powerupState) {
        String jobLocation = powerupInstance.getLocation().toString().split("\\.")[0];
        JobInstance jobInstance = JobInstance.of(new ResourceLocation(jobLocation));
        if (hasJob(jobInstance)) {
            Job job = getJob(jobInstance);
            job.setPowerupState(powerupInstance, powerupState);
        } else {
            JobsPlus.LOGGER.debug("Player {} does not have job {} to set powerup {}", name(), jobLocation, powerupInstance.getLocation());
        }
    }

    @Override
    public void removePowerup(PowerupInstance powerupInstance) {
        String jobLocation = powerupInstance.getLocation().toString().split("\\.")[0];
        JobInstance jobInstance = JobInstance.of(new ResourceLocation(jobLocation));
        if (hasJob(jobInstance)) {
            Job job = getJob(jobInstance);
            if (job.hasPowerup(powerupInstance)) {
                job.removePowerup(powerupInstance);
            } else {
                JobsPlus.LOGGER.debug("Player {} does not have powerup {} for job {}", name(), powerupInstance.getLocation(), jobLocation);
            }
        } else {
            JobsPlus.LOGGER.debug("Player {} does not have this job {} to remove powerup {}", name(), jobLocation, powerupInstance.getLocation());
        }
    }

    @Override
    public boolean hasPowerup(PowerupInstance powerupInstance) {
        return false;
    }

    @Override
    public @NotNull UUID getUUID() {
        return super.getUUID();
    }

    @Override
    public ServerPlayer getServerPlayer() {
        return (ServerPlayer) (Object) this;
    }

    @Override
    public String name() {
        return super.getName().getString();
    }

    public ListTag inactiveJobsToNBT() {
        ListTag jobsListTag = new ListTag();
        getInactiveJobs().forEach(job -> jobsListTag.add(job.toNBT()));
        return jobsListTag;
    }

    @Inject(at = @At("TAIL"), method = "restoreFrom(Lnet/minecraft/server/level/ServerPlayer;Z)V")
    public void restoreFrom(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        if (oldPlayer instanceof JobsServerPlayer oldJobsServerPlayer) {
            this.jobs = oldJobsServerPlayer.getJobs();
            this.coins = oldJobsServerPlayer.getCoins();
            this.display = oldJobsServerPlayer.getDisplay().orElse(null);
        }
    }

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        CompoundTag jobsTag = new CompoundTag();
        jobsTag.put(Constants.JOBS, JobSerializer.toNBT(this.jobs));
        jobsTag.putInt(Constants.COINS, this.coins);
        jobsTag.putString(Constants.DISPLAY, this.display == null ? "" : this.display.getLocationString());
        compoundTag.put(Constants.JOBS_DATA, jobsTag);
//        JobsPlus.LOGGER.error("Saved jobs: {}. For player {}", this.jobs, getServerPlayer().getDisplayName().getString());
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        CompoundTag jobsTag = compoundTag.getCompound(Constants.JOBS_DATA);
        this.jobs = JobSerializer.fromNBT(jobsTag);
        this.coins = jobsTag.getInt(Constants.COINS);
        this.display = JobDisplay.fromNBT(jobsTag);
//        JobsPlus.LOGGER.error("Loaded jobs: {}. For player {}", this.jobs, getServerPlayer().getDisplayName().getString());
    }
}
