package com.daqem.jobsplus.mixin.client;

import com.daqem.arc.api.action.holder.IActionHolder;
import com.daqem.arc.api.player.ArcPlayer;
import com.daqem.jobsplus.client.player.JobsClientPlayer;
import com.daqem.jobsplus.player.job.Job;
import com.daqem.jobsplus.integration.arc.holder.holders.job.JobInstance;
import com.daqem.jobsplus.integration.arc.holder.holders.job.JobManager;
import com.daqem.jobsplus.player.job.powerup.Powerup;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer extends AbstractClientPlayer implements JobsClientPlayer {

    @Unique
    private final List<Job> jobsplus$jobs = new ArrayList<>();
    @Unique
    private int jobsplus$coins = 0;

    public MixinLocalPlayer(ClientLevel clientLevel, GameProfile gameProfile, @Nullable ProfilePublicKey profilePublicKey) {
        super(clientLevel, gameProfile, profilePublicKey);
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

    @Nullable
    @Override
    public Job jobsplus$addNewJob(JobInstance jobInstance) {
        if (jobInstance.getLocation() == null) return null;
        Job job = jobsplus$getJob(jobInstance);
        if (job == null) {
            job = new Job(this, jobInstance, 1, 0);
            jobsplus$jobs.add(job);
            return job;
        }
        return null;
    }

    @Override
    public void jobsplus$removeJob(JobInstance jobInstance) {
        Job jobToRemove = jobsplus$getJob(jobInstance);
        jobsplus$jobs.remove(jobToRemove);
        jobsplus$removeActionHolders(jobToRemove);
    }

    @Override
    public void jobsplus$removeActionHolders(Job job) {
        if (jobsplus$getLocalPlayer() instanceof ArcPlayer arcPlayer) {
            arcPlayer.arc$removeActionHolder(job.getJobInstance());
            job.getPowerupManager().getAllPowerups().forEach(powerup -> arcPlayer.arc$removeActionHolder(powerup.getPowerupInstance()));
        }
    }

    @Override
    public Job jobsplus$getJob(@Nullable JobInstance jobLocation) {
        return jobsplus$jobs.stream().filter(job -> job.getJobInstance().equals(jobLocation)).findFirst().orElse(null);
    }

    @Override
    public int jobsplus$getCoins() {
        return this.jobsplus$coins;
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
    public String jobsplus$getName() {
        return jobsplus$getLocalPlayer().getName().getString();
    }

    @Override
    public double jobsplus$nextRandomDouble() {
        return jobsplus$getLocalPlayer().getRandom().nextDouble();
    }

    @Override
    public @NotNull UUID getUUID() {
        return super.getUUID();
    }

    @Override
    public Player jobsplus$getPlayer() {
        return jobsplus$getLocalPlayer();
    }

    @Override
    public Level jobsplus$getLevel() {
        return super.getLevel();
    }

    @Override
    public UUID jobsplus$getUUID() {
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
    public LocalPlayer jobsplus$getLocalPlayer() {
        return (LocalPlayer) (Object) this;
    }

    @Override
    public void jobsplus$replaceJobs(List<Job> jobs) {
        jobsplus$jobs.clear();
        jobsplus$jobs.addAll(jobs);

        if (jobsplus$getLocalPlayer() instanceof ArcPlayer arcPlayer) {
            arcPlayer.arc$getActionHolders().forEach(arcPlayer::arc$removeActionHolder);
            arcPlayer.arc$addActionHolders(jobsplus$getActionHolders());
        }
    }

    @Override
    public void jobsplus$replaceJob(Job job) {
        jobsplus$jobs.remove(jobsplus$getJob(job.getJobInstance()));
        jobsplus$jobs.add(job);

        if (jobsplus$getLocalPlayer() instanceof ArcPlayer arcPlayer) {
            arcPlayer.arc$removeActionHolder(job.getJobInstance());
            job.getPowerupManager().getAllPowerups().forEach(powerup -> arcPlayer.arc$removeActionHolder(powerup.getPowerupInstance()));
            arcPlayer.arc$addActionHolder(job.getJobInstance());
            job.getPowerupManager().getAllPowerups().forEach(powerup -> arcPlayer.arc$addActionHolder(powerup.getPowerupInstance()));
        }
    }
}
