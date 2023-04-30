package com.daqem.jobsplus.mixin;

import com.daqem.jobsplus.Constants;
import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.config.JobsPlusCommonConfig;
import com.daqem.jobsplus.event.triggers.MovementEvents;
import com.daqem.jobsplus.event.triggers.PlayerEvents;
import com.daqem.jobsplus.event.triggers.StatEvents;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.player.job.JobSerializer;
import com.daqem.jobsplus.player.job.powerup.PowerupState;
import com.daqem.jobsplus.player.stat.StatData;
import com.daqem.jobsplus.resources.job.JobInstance;
import com.daqem.jobsplus.resources.job.powerup.PowerupInstance;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer extends Player implements JobsServerPlayer {

    private final NonNullList<StatData> statData = NonNullList.create();
    private boolean isSwimming = false;
    private int swimmingDistanceInCm = 0;
    private boolean isWalking = false;
    private float walkingDistance = 0;
    private boolean isSprinting = false;
    private float sprintingDistance = 0;
    private boolean isCrouching = false;
    private float crouchingDistance = 0;
    private boolean isElytraFlying = false;
    private float elytraFlyingDistance = 0;
    private List<Job> jobs = new ArrayList<>();
    private int coins = 0;

    public MixinServerPlayer(Level level, BlockPos blockPos, float yaw, GameProfile gameProfile, @Nullable ProfilePublicKey profilePublicKey) {
        super(level, blockPos, yaw, gameProfile, profilePublicKey);
    }

    @Override
    public List<Job> getJobs() {
        return jobs;
    }

    @Override
    public List<JobInstance> getJobInstances() {
        return getJobs().stream()
                .map(Job::getJobInstance)
                .toList();
    }

    @Override
    public List<Job> getInactiveJobs() {
        return JobsPlus.getJobManager().getJobs().values().stream()
                .filter(jobInstance ->
                        !jobs.stream()
                                .map(Job::getJobInstance)
                                .toList()
                                .contains(jobInstance))
                .map(jobInstance -> new Job(this, jobInstance))
                .toList();
    }

    @Override
    public List<JobInstance> getInactiveJobInstances() {
        return getInactiveJobs().stream()
                .map(Job::getJobInstance)
                .toList();
    }

    @Override
    public void addNewJob(ResourceLocation jobLocation) {
        if (jobLocation == null) return;
        this.jobs.stream()
                .filter(job -> job.getJobInstance().getLocation().equals(jobLocation))
                .findFirst()
                .ifPresent(job -> {
                    throw new IllegalArgumentException("Player already has this job");
                });
        jobs.add(new Job(this, jobLocation, 1, 0, Map.of()));
    }

    @Override
    public void addNewJob(JobInstance job) {
        addNewJob(job.getLocation());
    }

    @Override
    public void removeJob(JobInstance jobInstance) {
        //Iterating over the jobs backwards to avoid ConcurrentModificationException.
        for (int i = jobs.size() - 1; i >= 0; i--) {
            Job job = jobs.get(i);
            if (job.getJobInstance().getLocation().equals(jobInstance.getLocation())) {
                jobs.remove(i);
            }
        }
    }

    @Override
    public void refundJob(JobInstance jobInstance) {
        int refund = jobInstance.getStopRefund();
        if (getJobs().size() > JobsPlusCommonConfig.amountOfFreeJobs.get()) {
            if (refund > 0) {
                addCoins(refund);
            }
        }
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
    public void addPowerup(PowerupInstance powerupInstance) {
        String jobLocation = powerupInstance.getLocation().toString().split("\\.")[0];
        Job job = getJobForPowerup(powerupInstance);
        if (hasJob(job.getJobInstance())) {
            if (!job.hasPowerup(powerupInstance)) {
                job.addPowerup(powerupInstance);
            } else {
                JobsPlus.LOGGER.error("Player {} already has powerup {} for job {}", name(), powerupInstance.getLocation(), jobLocation);
            }
        } else {
            JobsPlus.LOGGER.error("Player {} does not have job {} to add powerup {}", name(), jobLocation, powerupInstance.getLocation());
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
        return getJobs().stream()
                .anyMatch(job -> job.hasPowerup(powerupInstance));
    }

    @Override
    public void togglePowerup(PowerupInstance powerupInstance) {
        if (hasPowerup(powerupInstance)) {
            PowerupState powerupState = getPowerupState(powerupInstance);
            if (powerupState == PowerupState.ACTIVE) {
                setPowerup(powerupInstance, PowerupState.INACTIVE);
            } else {
                setPowerup(powerupInstance, PowerupState.ACTIVE);
            }
        }
    }

    private PowerupState getPowerupState(PowerupInstance powerupInstance) {
        Map<ResourceLocation, PowerupState> powerups = getJobForPowerup(powerupInstance).getPowerups();
        if (powerups.containsKey(powerupInstance.getLocation())) {
            return powerups.get(powerupInstance.getLocation());
        }
        return PowerupState.NOT_OWNED;
    }

    private Job getJobForPowerup(PowerupInstance powerupInstance) {
        return getJob(JobInstance.of(new ResourceLocation(powerupInstance.getLocation().toString().split("\\.")[0])));
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

    @Override
    public NonNullList<StatData> getStatData() {
        return this.statData;
    }

    @Override
    public void addStatData(StatData statData) {
        this.statData.add(statData);
    }

    public ListTag inactiveJobsToNBT() {
        ListTag jobsListTag = new ListTag();
        getInactiveJobs().forEach(job -> jobsListTag.add(job.toNBT()));
        return jobsListTag;
    }

    @Override
    public void setSwimmingDistanceInCm(int swimmingDistanceInCm) {
        this.swimmingDistanceInCm = swimmingDistanceInCm;
    }

    @Override
    public void setElytraFlyingDistanceInCm(float flyingDistanceInCm) {
        this.elytraFlyingDistance = flyingDistanceInCm;
    }

    @Override
    public @NotNull ItemStack eat(@NotNull Level level, @NotNull ItemStack itemStack) {
        PlayerEvents.onPlayerEat(this, itemStack);
        return super.eat(level, itemStack);
    }

    @Inject(at = @At("TAIL"), method = "tick()V")
    public void tick(CallbackInfo ci) {
        if (this.isSwimming && this.isSwimming()) {
            MovementEvents.onSwim(this, this.swimmingDistanceInCm);
        } else {
            if (this.isSwimming) {
                this.isSwimming = false;
                MovementEvents.onStopSwimming(this);
            } else {
                if (this.isSwimming()) {
                    this.isSwimming = true;
                    MovementEvents.onStartSwimming(this);
                }
            }
        }

        boolean isCurrentlyWalking = this.walkDist > this.walkingDistance;
        float distance = this.walkDist - this.walkingDistance;
        if (this.isWalking && isCurrentlyWalking) {
            this.walkingDistance = this.walkDist;
            MovementEvents.onWalk(this, (int) (this.walkingDistance * 100));
        } else {
            if (this.isWalking) {
                this.isWalking = false;
                MovementEvents.onStopWalking(this);
            } else if (isCurrentlyWalking) {
                this.isWalking = true;
                MovementEvents.onStartWalking(this);
            }
        }

        if (this.isSprinting && this.isSprinting()) {
            this.sprintingDistance += distance;
            MovementEvents.onSprint(this, (int) (this.sprintingDistance * 100));
        } else {
            if (this.isSprinting) {
                this.isSprinting = false;
                MovementEvents.onStopSprinting(this);
            } else if (this.isSprinting()) {
                this.isSprinting = true;
                MovementEvents.onStartSprinting(this);
            }
        }

        if (this.isCrouching && this.isCrouching()) {
            this.crouchingDistance += distance;
            MovementEvents.onCrouch(this, (int) (this.crouchingDistance * 100));
        } else {
            if (this.isCrouching) {
                this.isCrouching = false;
                MovementEvents.onStopCrouching(this);
            } else if (this.isCrouching()) {
                this.isCrouching = true;
                MovementEvents.onStartCrouching(this);
            }
        }

        if (this.isElytraFlying && this.isFallFlying()) {
            MovementEvents.onElytraFly(this, (int) this.elytraFlyingDistance);
        } else {
            if (this.isElytraFlying) {
                this.isElytraFlying = false;
                MovementEvents.onStopElytraFlying(this);
            } else {
                if (this.isFallFlying()) {
                    this.isElytraFlying = true;
                    MovementEvents.onStartElytraFlying(this);
                }
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "awardStat(Lnet/minecraft/stats/Stat;I)V")
    public void awardStat(Stat<?> stat, int amount, CallbackInfo ci) {
        int previousAmount = 0;
        boolean found = false;
        for (StatData statData : getStatData()) {
            if (statData.getStat().equals(stat)) {
                previousAmount = statData.getAmount();
                statData.addAmount(amount);
                found = true;
                break;
            }
        }
        if (!found) {
            addStatData(new StatData(stat, amount));
        }
        StatEvents.onAwardStat(this, stat, previousAmount, previousAmount + amount);
    }

    @Inject(at = @At("TAIL"), method = "onEffectAdded(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)V")
    public void onEffectAdded(MobEffectInstance effect, @Nullable Entity entity, CallbackInfo ci) {
        PlayerEvents.onEffectAdded(this, effect, entity);
    }

    @Inject(at = @At("TAIL"), method = "restoreFrom(Lnet/minecraft/server/level/ServerPlayer;Z)V")
    public void restoreFrom(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        if (oldPlayer instanceof JobsServerPlayer oldJobsServerPlayer) {
            this.jobs = oldJobsServerPlayer.getJobs();
            this.coins = oldJobsServerPlayer.getCoins();
        }
    }

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        CompoundTag jobsTag = new CompoundTag();
        jobsTag.put(Constants.JOBS, JobSerializer.toNBT(this.jobs));
        jobsTag.putInt(Constants.COINS, this.coins);
        compoundTag.put(Constants.JOBS_DATA, jobsTag);
//        JobsPlus.LOGGER.error("Saved jobs: {}. For player {}", this.jobs, getServerPlayer().getDisplayName().getString());
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        CompoundTag jobsTag = compoundTag.getCompound(Constants.JOBS_DATA);
        this.jobs = JobSerializer.fromNBT(this, jobsTag);
        this.coins = jobsTag.getInt(Constants.COINS);
//        JobsPlus.LOGGER.error("Loaded jobs: {}. For player {}", this.jobs, getServerPlayer().getDisplayName().getString());
    }
}
