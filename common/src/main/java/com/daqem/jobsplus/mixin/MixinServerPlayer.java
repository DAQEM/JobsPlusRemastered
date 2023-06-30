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
import com.daqem.jobsplus.player.stat.StatData;
import com.daqem.jobsplus.resources.crafting.CraftingResult;
import com.daqem.jobsplus.resources.crafting.CraftingType;
import com.daqem.jobsplus.resources.job.JobInstance;
import com.daqem.jobsplus.resources.job.JobManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer extends Player implements JobsServerPlayer {

    @Shadow
    public abstract void readAdditionalSaveData(CompoundTag compoundTag);

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
    private boolean isGrinding = false;
    private boolean updatedFromOldJobsPLus = false;

    public MixinServerPlayer(Level level, BlockPos blockPos, float yaw, GameProfile gameProfile, @Nullable ProfilePublicKey profilePublicKey) {
        super(level, blockPos, yaw, gameProfile, profilePublicKey);
    }

    @Override
    public List<Job> getJobs() {
        return jobs;
    }

    @Override
    public List<JobInstance> getJobInstances() {
        return jobs.stream()
                .map(Job::getJobInstance)
                .toList();
    }

    @Override
    public List<Job> getInactiveJobs() {
        return JobManager.getInstance().getJobs().values().stream()
                .filter(jobInstance -> !getJobInstances().contains(jobInstance))
                .map(jobInstance -> new Job(this, jobInstance))
                .toList();
    }

    @Override
    public @Nullable Job addNewJob(@NotNull JobInstance jobInstance) {
        if (jobInstance.getLocation() == null) return null;
        Job job = getJob(jobInstance);
        if (job == null) {
            job = new Job(this, jobInstance, 1, 0);
            jobs.add(job);
            return job;
        }
        return null;
    }

    @Override
    public void removeJob(JobInstance jobInstance) {
        Job job = getJob(jobInstance);
        if (job != null) {
            jobs.remove(job);
        }
    }

    @Override
    public void refundJob(@NotNull JobInstance jobInstance) {
        int refund = jobInstance.getStopRefund();
        if (getJobs().size() > JobsPlusCommonConfig.amountOfFreeJobs.get()) {
            if (refund > 0) {
                addCoins(refund);
            }
        }
    }

    @Override
    public void removeAndRefundJob(@NotNull JobInstance jobInstance) {
        removeJob(jobInstance);
        refundJob(jobInstance);
    }

    @Override
    public @Nullable Job getJob(@Nullable JobInstance jobLocation) {
        if (jobLocation == null) return null;
        return this.jobs.stream()
                .filter(job -> job.getJobInstance().getLocation().equals(jobLocation.getLocation()))
                .findFirst()
                .orElse(null);
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

    @Override
    public CraftingResult canCraft(CraftingType crafting, ItemStack itemStack) {
        for (JobInstance jobInstance : JobManager.getInstance().getJobs().values()) {
            Job job = getJob(jobInstance);
            int level = job == null ? 0 : job.getLevel();
            CraftingResult craftingResult = jobInstance.canCraft(crafting, itemStack, level);
            if (!craftingResult.canCraft()) {
                if (!JobsPlusCommonConfig.restrictionsEnabledForCreative.get()) {
                    if (getServerPlayer().isCreative()) {
                        if (JobsPlusCommonConfig.showRestrictionMessageForCreative.get()) {
                            getServerPlayer().sendSystemMessage(JobsPlus.translatable("inventory.bypass").withStyle(ChatFormatting.GREEN), true);
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
    public boolean getUpdatedFromOldJobsPlus() {
        return this.updatedFromOldJobsPLus;
    }

    @Override
    public void setUpdatedFromOldJobsPlus(boolean updatedFromOldJobsPlus) {
        this.updatedFromOldJobsPLus = updatedFromOldJobsPlus;
    }

    @Override
    public double nextRandomDouble() {
        return this.getServerPlayer().getRandom().nextDouble();
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

        if (getServerPlayer().containerMenu instanceof GrindstoneMenu) {
            if (isGrinding) {
                boolean firstSlot = false;
                boolean secondSlot = false;
                for (Slot slot : containerMenu.slots) {
                    if (!(slot.getItem().getItem() instanceof AirItem || slot.container instanceof Inventory)) {

                        if (slot.getContainerSlot() == 0) {
                            firstSlot = true;
                        }
                        if (slot.getContainerSlot() == 1) {
                            secondSlot = true;
                        }
                    }
                }
                if (!firstSlot && !secondSlot) {
                    PlayerEvents.onGrindItem(this);
                }
            }
            boolean firstSlot = false;
            boolean secondSlot = false;
            for (Slot slot : containerMenu.slots) {
                if (!(slot.getItem().getItem() instanceof AirItem || slot.container instanceof Inventory)) {

                    if (slot.getContainerSlot() == 0) {
                        firstSlot = true;
                    }
                    if (slot.getContainerSlot() == 1) {
                        secondSlot = true;
                    }
                }
            }
            isGrinding = firstSlot && secondSlot;
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
            this.updatedFromOldJobsPLus = oldJobsServerPlayer.getUpdatedFromOldJobsPlus();
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
    }

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
                this.addCoins((int) (powerUps.stream().filter(b -> b).count() * 10));
            }
        });
        this.jobs = jobs;
        this.updatedFromOldJobsPLus = true;
    }

    @Inject(at = @At("TAIL"), method = "onEnchantmentPerformed(Lnet/minecraft/world/item/ItemStack;I)V")
    public void onEnchantmentPerformed(ItemStack itemStack, int level, CallbackInfo ci) {
        PlayerEvents.onEnchantItem(this, itemStack, level);
    }
}
