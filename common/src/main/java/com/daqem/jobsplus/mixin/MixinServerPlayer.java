package com.daqem.jobsplus.mixin;

import com.daqem.arc.api.action.holder.IActionHolder;
import com.daqem.arc.api.player.ArcPlayer;
import com.daqem.jobsplus.Constants;
import com.daqem.jobsplus.JobsPlus;
import com.daqem.jobsplus.config.JobsPlusCommonConfig;
import com.daqem.jobsplus.networking.sync.jobs.ClientboundUpdateJobPacket;
import com.daqem.jobsplus.player.JobsServerPlayer;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.player.job.JobSerializer;
import com.daqem.jobsplus.data.crafting.CraftingResult;
import com.daqem.jobsplus.data.crafting.CraftingType;
import com.daqem.jobsplus.interation.arc.action.holder.holders.job.JobInstance;
import com.daqem.jobsplus.interation.arc.action.holder.holders.job.JobManager;
import com.daqem.jobsplus.player.job.powerup.Powerup;
import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.item.ItemStack;
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

    private List<Job> jobs = new ArrayList<>();
    private int coins = 0;
    private boolean updatedFromOldJobsPLus = false;

    public MixinServerPlayer(Level level, BlockPos blockPos, float yaw, GameProfile gameProfile, @Nullable ProfilePublicKey profilePublicKey) {
        super(level, blockPos, yaw, gameProfile, profilePublicKey);
    }

    @Override
    public List<Job> jobsplus$getJobs() {
        return jobs;
    }

    @Override
    public List<JobInstance> jobsplus$getJobInstances() {
        return jobs.stream().map(Job::getJobInstance).toList();
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
            jobs.add(job);
            jobsplus$updateJob(job);
            return job;
        }
        return null;
    }

    @Override
    public void jobsplus$removeJob(JobInstance jobInstance) {
        Job job = jobsplus$getJob(jobInstance);
        if (job != null) {
            jobs.remove(job);
            jobsplus$updateJob(job);
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
        return this.jobs.stream()
                .filter(job -> job.getJobInstance().getLocation().equals(jobLocation.getLocation()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public int jobsplus$getCoins() {
        return coins;
    }

    @Override
    public void jobsplus$addCoins(int coins) {
        this.coins += coins;
    }

    @Override
    public void jobsplus$setCoins(int coins) {
        this.coins = coins;
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
    public CraftingResult jobsplus$canCraft(CraftingType crafting, ItemStack itemStack) {
        for (JobInstance jobInstance : JobManager.getInstance().getJobs().values()) {
            Job job = jobsplus$getJob(jobInstance);
            int level = job == null ? 0 : job.getLevel();
            CraftingResult craftingResult = jobInstance.canCraft(crafting, itemStack, level);
            if (!craftingResult.canCraft()) {
                if (!JobsPlusCommonConfig.restrictionsEnabledForCreative.get()) {
                    if (jobsplus$getServerPlayer().isCreative()) {
                        if (JobsPlusCommonConfig.showRestrictionMessageForCreative.get()) {
                            jobsplus$getServerPlayer().sendSystemMessage(JobsPlus.translatable("inventory.bypass").withStyle(ChatFormatting.GREEN), true);
                        }
                        return new CraftingResult(true);
                    }
                }
                return craftingResult;
            }
        }
        return new CraftingResult(true);
    }

    @Override
    public boolean jobsplus$getUpdatedFromOldJobsPlus() {
        return this.updatedFromOldJobsPLus;
    }

    @Override
    public void jobsplus$setUpdatedFromOldJobsPlus(boolean updatedFromOldJobsPlus) {
        this.updatedFromOldJobsPLus = updatedFromOldJobsPlus;
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
            arcPlayer.arc$addActionHolder(job.getJobInstance());
        }
    }

    @Override
    public void jobsplus$updateJobOnClient(Job job) {
        new ClientboundUpdateJobPacket(job).sendTo(jobsplus$getServerPlayer());
    }

    @Override
    public Player jobsplus$getPlayer() {
        return jobsplus$getServerPlayer();
    }

    @Inject(at = @At("TAIL"), method = "restoreFrom(Lnet/minecraft/server/level/ServerPlayer;Z)V")
    public void restoreFrom(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        if (oldPlayer instanceof JobsServerPlayer oldJobsServerPlayer) {
            this.jobs = oldJobsServerPlayer.jobsplus$getJobs();
            this.coins = oldJobsServerPlayer.jobsplus$getCoins();
            this.updatedFromOldJobsPLus = oldJobsServerPlayer.jobsplus$getUpdatedFromOldJobsPlus();
        }
    }

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        CompoundTag jobsTag = new CompoundTag();
        jobsTag.put(Constants.JOBS, JobSerializer.toNBT(this.jobs));
        jobsTag.putInt(Constants.COINS, this.coins);
        jobsTag.putBoolean(Constants.JOBSPLUS_UPDATE, this.updatedFromOldJobsPLus);

        compoundTag.put(Constants.JOBS_DATA, jobsTag);
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        CompoundTag jobsTag = compoundTag.getCompound(Constants.JOBS_DATA);
        this.jobs = JobSerializer.fromNBT(this, jobsTag).stream()
                .filter(job -> job.getJobInstance() != null)
                .collect(Collectors.toCollection(ArrayList::new));
        this.coins = jobsTag.getInt(Constants.COINS);

        this.updatedFromOldJobsPLus = jobsTag.getBoolean(Constants.JOBSPLUS_UPDATE);

        CompoundTag forgeCaps = compoundTag.getCompound("ForgeCaps").getCompound("jobsplus:jobs");
        if (!forgeCaps.isEmpty()) {
            readFromOldJobsPlusData(forgeCaps);
        }

        if (jobsplus$getServerPlayer() instanceof ArcPlayer arcPlayer) {
            arcPlayer.arc$addActionHolders(new ArrayList<>(this.jobsplus$getActionHolders()));
        }
    }

    @Unique
    private void readFromOldJobsPlusData(CompoundTag tag) {
        Map<String, int[]> map = new HashMap<>();
        List<Job> jobs = new ArrayList<>();

        this.coins = tag.getInt("coins");

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
        this.jobs = jobs;
        this.updatedFromOldJobsPLus = true;
    }
}
